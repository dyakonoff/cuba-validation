package com.haulmont.dyakonoff.orderman.service;


import com.haulmont.cuba.core.entity.Entity;
import java.util.Collection;

/**
 * Validates that Stock has enough products to finish transaction
 * Reduces and increases quantity of products in stock accordingly
 */
public interface StockCheckingService {
    String NAME = "orderman_StockCheckingService";

    void checkProductsAvailability(Object entityMgr, Collection<Entity> managedEntities);
}