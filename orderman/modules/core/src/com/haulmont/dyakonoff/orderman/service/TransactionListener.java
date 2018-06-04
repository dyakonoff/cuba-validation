package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.*;
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
    private StockCheckingService stockCheckingService;

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
        stockCheckingService.checkProductsAvailability(entityManager, managedEntities);
    }




}