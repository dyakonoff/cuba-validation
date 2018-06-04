package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.dyakonoff.orderman.entity.Product;
import com.haulmont.dyakonoff.orderman.entity.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service(StockApiService.NAME)
public class StockApiServiceBean implements StockApiService {

    @Override
    public List<Stock> getProductsInStock() {
        return null;
    }

    @Override
    public Stock getStockForProductByName(String productName) {
        return null;
    }

    @Override
    public void addNewProduct(Product product, BigDecimal inStock, BigDecimal optimalLevel) {

    }

    @Override
    public void increaseQuantityByProductName(String productName, BigDecimal increaseAmount) {

    }
}