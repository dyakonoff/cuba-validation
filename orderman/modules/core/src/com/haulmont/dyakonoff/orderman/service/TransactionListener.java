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
     * Validates that Stock has enough items for all orders to be commited
     * @see https://doc.cuba-platform.com/manual-6.8/transaction_listeners.html for more examples
     * @param entityManager
     * @param managedEntities
     */
    @Override
    public void beforeCommit(EntityManager entityManager, Collection<Entity> managedEntities) {

        // see https://doc.cuba-platform.com/manual-6.8/transaction_listeners.html for more examples
        PersistenceTools persistenceTools = persistence.getTools();

        // building a list of orders to commit
        ArrayList<Order> ordersToCheck = new ArrayList<>();
        for (Entity entity : managedEntities) {

            if (persistenceTools.isDirty(entity))
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

        HashMap<UUID, BigDecimal> stockChanges = new HashMap<>();
        // building a set of products to be removed & returned from the stock
        for (Order order : ordersToCheck) {
            BigDecimal orderMultiplicator = new BigDecimal( getOrderMultiplier(order, persistenceTools));
            for (OrderItem item : order.getItems()) {
                UUID productId = item.getProduct().getId();
                BigDecimal newQty = item.getQuantity();
                BigDecimal oldQty = (BigDecimal)persistenceTools.getOldValue(item, "quantity");

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
                            + s.getInStock().toString() + " left, required: " + valChange.toString();
                    throw new ValidationException(msg);
                }
                s.setInStock(newStockVal);
            }
            tx.commit();
        }

        //
    }


    private static int[][] orderTransMatrix = {
            {  0, +1, +1,  0 },
            { -1,  0,  0, -1 },
            { -1,  0,  0, -1 },
            {  0, +1, +1,  0 }
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
            null     |   0    +1    +1        0
            NEW      |  -1     0     0       -1
            PAID     |  -1     0     0       -1
            CANCELED |   0    +1    +1       0
        */

        int state_1 = orderStatusToInt(order, (OrderStatus) pt.getOldValue(order, "status"));
        int state_2 = orderStatusToInt(order, order.getStatus());
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