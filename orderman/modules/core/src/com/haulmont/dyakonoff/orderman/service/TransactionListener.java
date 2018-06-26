package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.*;
import com.haulmont.dyakonoff.orderman.entity.Order;
import com.haulmont.dyakonoff.orderman.entity.OrderItem;
import com.haulmont.dyakonoff.orderman.entity.OrderStatus;
import com.haulmont.dyakonoff.orderman.entity.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeCommitTransactionListener;
import com.haulmont.cuba.core.entity.Entity;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.*;

@Component("orderman_TransactionListener")
public class TransactionListener implements BeforeCommitTransactionListener {

    @Inject
    private Persistence persistence;

    private Logger log = LoggerFactory.getLogger(TransactionListener.class);

    /**
     * Validates that Stock has enough items for all orders to be committed
     * @see https://doc.cuba-platform.com/manual-6.8/transaction_listeners.html for more examples
     * @param entityManager
     * @param managedEntities
     */
    @Override
    public void beforeCommit(EntityManager entityManager, Collection<Entity> managedEntities) {
        // Check that stock has enough products to fulfill the transaction.
        // Reduces and increases quantity of products in stock accordingly
        // this is a pretty heavy operation with a DB query and couple loops inside
        // see https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html for more examples

        Set<Order> ordersToCheck = buildListOfOrdersToCheck(entityManager, managedEntities);
        if (ordersToCheck.size() == 0)
            return;

        HashMap<UUID, BigDecimal> stockChanges = buildStockChangesSet(ordersToCheck);
        if (stockChanges.size() == 0)
            return;

        validateStockHasEnoughGoods(stockChanges); // throws ValidationException if validation fails
    }


    /**
     * Building a list of orders to be committed in this transaction
     * @param managedEntities
     * @return Set<Order>
     */
    private Set<Order> buildListOfOrdersToCheck(EntityManager entityManager, Collection<Entity> managedEntities) {
        PersistenceTools persistenceTools = persistence.getTools();

        Set<Order> ordersToCheck = new HashSet<>();
        for (Entity entity : managedEntities) {

            if (!persistenceTools.isDirty(entity))
                continue;
            if (entity instanceof Order) {
                ordersToCheck.add( (Order)entity );
            }
            else if (entity instanceof OrderItem) {
                Order order = ((OrderItem)entity).getOrder();
                // a reference can be detached, so merge it into current persistence context
                ordersToCheck.add(entityManager.merge(order));
                ordersToCheck.add(order);
            }
        }
        return ordersToCheck;
    }

    /**
     * Building a list of product quantity changes in Stock
     * @param ordersToCheck
     * @return map of products changed with the quantities
     */
    private HashMap<UUID, BigDecimal> buildStockChangesSet(Set<Order> ordersToCheck) {
        PersistenceTools persistenceTools = persistence.getTools();

        HashMap<UUID, BigDecimal> stockChanges = new HashMap<>();
        // building a set of products to be removed & returned from the stock
        for (Order order : ordersToCheck) {
            BigDecimal orderMultiplicator = new BigDecimal( getOrderMultiplier(order, persistenceTools));
            for (OrderItem item : order.getItems()) {
                UUID productId = item.getProduct().getId();
                BigDecimal newQty, oldQty;
                // handling OrderItem that are going to be deleted
                if (!item.isDeleted()) {
                    newQty = item.getQuantity();
                    oldQty = (BigDecimal) persistenceTools.getOldValue(item, "quantity");
                }
                else {
                    newQty = BigDecimal.ZERO;
                    oldQty = item.getQuantity();
                }

                if (newQty == null) newQty = BigDecimal.ZERO;
                if (oldQty == null) oldQty = BigDecimal.ZERO;
                BigDecimal qtyChange = newQty.subtract(oldQty).multiply(orderMultiplicator);
                if (qtyChange.compareTo(BigDecimal.ZERO) != 0) {
                    // we have changes in Stock for this OrderItem
                    if (stockChanges.containsKey(productId))
                        stockChanges.merge(productId, qtyChange, BigDecimal::add);
                    else
                        stockChanges.put(productId, qtyChange);
                }
            }
        }

        return stockChanges;
    }

    /**
     * Check that stock has enough products to fulfill the transaction.
     * Reduces and increases quantity of products in stock accordingly
     * Throws ValidationException if not.
     * @param stockChanges
     */
    private void validateStockHasEnoughGoods(HashMap<UUID, BigDecimal> stockChanges) {
        // checking that are enough values in stock
        // changes stock
        try (Transaction tx = persistence.createTransaction()) {
            TypedQuery<Stock> query = persistence.getEntityManager().createQuery(
                    "SELECT s FROM orderman$Stock s WHERE s.product IN :products_set", Stock.class);

            Set<UUID> productIds = stockChanges.keySet();
            ArrayList<String> productStringIds = new ArrayList<>(productIds.size());
            productIds.forEach(id -> productStringIds.add(id.toString()));
            query.setParameter("products_set", productStringIds);

            List<Stock> stocks = query.getResultList();

            for (Stock s : stocks) {
                BigDecimal valChange = stockChanges.get(s.getProduct().getId());
                BigDecimal newStockVal = s.getInStock().add( valChange );
                if (newStockVal.compareTo(BigDecimal.ZERO) < 0) {
                    String msg = "Insufficient amount of product '" + s.getProduct().getName() + "' in Stock, "
                            + s.getInStock().toString() + " left, required: " + valChange.multiply(new BigDecimal(-1)).toString();
                    throw new ValidationException(msg);
                }

                // update Stock level
                s.setInStock(newStockVal);
            }
            tx.commit();
        }
    }


    private static int[][] orderTransMatrix = {
            {  0, -1, -1,  0 },
            { +1,  0,  0, +1 },
            { +1,  0,  0, +1 },
            {  0, -1, -1,  0 }
    };


    /**
     * Returns order creation | deletion transition map
     * @param order
     * @param pt
     * @return
     */
    private int getOrderMultiplier(Order order, PersistenceTools pt) {
        /*
                     | null | NEW | PAID | CANCELED |
            null     |   0    -1    -1        0
            NEW      |  +1     0     0       +1
            PAID     |  +1     0     0       +1
            CANCELED |   0    -1    -1        0

            How to read this
            rows => initial order state (before transaction)
            columns => final order state (after transaction)

            For example:
            * if order was created (comes from null to NEW), then all his OrderItem.quantity should be substracted from Stock.inStock == Multiplier == -1
            * if order was paid (comes from NEW to PAID), nothing changed, that's whi element [1, 2] == 0 (row, col, 0-based)
            * If order was deleted from PAID status (comes from PAID to null) than all order items should be returned to stock ==> multiplier === +1
        */

        int state_1, state_2;
        if (!order.isDeleted()) {
            // regular case
            state_1 = orderStatusToInt(order, OrderStatus.fromId((Integer) pt.getOldValue(order, "status")));
            state_2 = orderStatusToInt(order, order.getStatus());
        }
        else {
            // if order goes to be deleted, then his old state is always == null and actually, it goes from it's current state to null
            state_1 = orderStatusToInt(order, order.getStatus());
            state_2 = 0;
        }
        return orderTransMatrix[state_1][state_2];
    }

    private int orderStatusToInt(Order order, @Nullable OrderStatus status) {
        if (status == null) return 0;
        if (status == OrderStatus.NEW) return 1;
        if (status == OrderStatus.PAID) return 2;
        if (status == OrderStatus.CANCELLED) return 3;

        String msg = String.format("Unknown state for order %s == %s", order.getId(), status.toString());
        log.error(msg);
        throw new ValidationException(msg);
    }
}