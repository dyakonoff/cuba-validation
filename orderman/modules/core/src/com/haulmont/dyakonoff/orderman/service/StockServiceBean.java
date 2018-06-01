package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.dyakonoff.orderman.entity.*;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.*;

@Service(StockService.NAME)
public class StockServiceBean implements StockService {


//    @Inject
//    private Persistence persistence;

    @Inject
    private DataManager dataManager;

    // TODO: Remove unused code

    /**
     * Change Stock values
     * @param newItems
     * @param oldItems
     */
//    private void updateStockData(@Nullable List<OrderItem> newItems, @Nullable List<OrderItem> oldItems) {
//        if (newItems == null && oldItems == null)
//            return; // nothing to do
//
//        HashMap<UUID, BigDecimal> stockChange = new HashMap<>();
//
//        if (newItems != null && newItems.size() > 0) {
//            newItems.stream().forEach( orderItem -> {
//                stockChange.put(orderItem.getProduct().getId(), orderItem.getQuantity().negate());
//            });
//        }
//        if (oldItems != null && oldItems.size() > 0) {
//            oldItems.stream().forEach( orderItem -> {
//                BigDecimal countToExtract = stockChange.get(orderItem.getProduct().getId());
//                BigDecimal productQtyChange = (countToExtract == null) ?
//                        orderItem.getQuantity() :
//                        countToExtract.add(orderItem.getQuantity());
//                stockChange.put(orderItem.getProduct().getId(), productQtyChange);
//            });
//        }
//
//        // remove zero-valued elements
//        // make a copy of keys set for safe removal
//        ArrayList<UUID> uids = new ArrayList<>(stockChange.keySet());
//        for (UUID uid : uids) {
//            if (stockChange.get(uid).compareTo(BigDecimal.ZERO) == 0) {
//                stockChange.remove(uid);
//            }
//        }
//
//        if (stockChange.size() == 0)
//            return;
//
//        // changes stock
//        try (Transaction tx = persistence.createTransaction()) {
//            TypedQuery<Stock> query = persistence.getEntityManager().createQuery(
//                    "SELECT s FROM ordman$Stock s WHERE s.product IN :products_set", Stock.class);
//
//            Set<UUID> productIds = stockChange.keySet();
//            ArrayList<String> productStringIds = new ArrayList<>(productIds.size());
//            productIds.forEach(id -> productStringIds.add(id.toString()));
//            query.setParameter("products_set", productStringIds);
//
//            List<Stock> stocks = query.getResultList();
//
//            for (Stock s : stocks) {
//                BigDecimal valChange = stockChange.get(s.getProduct().getId());
//                BigDecimal newStockVal = s.getInStock().add( valChange );
//                if (newStockVal.compareTo(BigDecimal.ZERO) < 0) {
//                    String msg = "Insufficient amount od product '" + s.getProduct().getName() + "' in Stock. ("
//                            + s.getInStock().toString() + " left)";
//                    throw new ValidationException(msg);
//                }
//                s.setInStock(newStockVal);
//            }
//            tx.commit();
//        }
//    }
//
//
//    @Override
//    public void onOrderInsert(Order order) {
//        if (order.getStatus() != OrderStatus.CANCELLED)
//            updateStockData(order.getItems(), null);
//    }
//
//    @Override
//    public void onOrderUpdate(Order order) {
//        List<OrderItem> newItems = null;
//        List<OrderItem> oldItems = null;
//        OrderStatus newStatus = order.getStatus();
//        OrderStatus oldStatus = (OrderStatus)(persistence.getTools().getOldValue(order, "status"));
//
//        // canceled order means that it doesn't use any products from stock
//        if (newStatus != OrderStatus.CANCELLED)
//            newItems = order.getItems();
//        if (oldStatus != OrderStatus.CANCELLED)
//            oldItems = (List<OrderItem>)(persistence.getTools().getOldValue(order, "items"));
//
//        updateStockData(newItems, oldItems);
//    }
//
//    @Override
//    public void onOrderDelete(Order order) {
//        if (order.getStatus() != OrderStatus.CANCELLED)
//            updateStockData(null, order.getItems());
//    }

    @Override
    public BigDecimal getProductAvailability(Product product) {
        Stock stock = dataManager.load(LoadContext.create(Stock.class)
                .setQuery(LoadContext.createQuery("SELECT s FROM orderman$Stock s WHERE s.product.id = :product")
                        .setParameter("product", product.getId()))
        );
        if (stock == null)
            throw new ValidationException(String.format("Product '%s' is not in stock, please add it to stock first", product.getName()));

        return stock.getInStock();
    }
}