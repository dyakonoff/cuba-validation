package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.dyakonoff.orderman.entity.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.math.BigDecimal;

@Service(StockService.NAME)
public class StockServiceBean implements StockService {
    @Inject
    private DataManager dataManager;

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