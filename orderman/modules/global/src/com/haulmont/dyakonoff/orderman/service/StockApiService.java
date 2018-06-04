package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.dyakonoff.orderman.entity.Product;
import com.haulmont.dyakonoff.orderman.entity.Stock;

import java.math.BigDecimal;
import java.util.List;

public interface StockApiService {
    String NAME = "orderman_StockApiService";

    List<Stock> getProductsInStock();
    Stock getStockForProductByName(String productName);
    void addNewProduct(Product product, BigDecimal inStock, BigDecimal optimalLevel);
    void increaseQuantityByProductName(String productName, BigDecimal increaseAmount);
}