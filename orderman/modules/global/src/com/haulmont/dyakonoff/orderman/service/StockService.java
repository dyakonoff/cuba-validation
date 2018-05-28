package com.haulmont.dyakonoff.orderman.service;


import com.haulmont.dyakonoff.orderman.entity.Order;
import com.haulmont.dyakonoff.orderman.entity.Product;

import java.math.BigDecimal;

public interface StockService {
    String NAME = "orderman_StockService";

    BigDecimal getProductAvailability(Product product);
//    void onOrderDelete(Order order);
//    void onOrderInsert(Order order);
//    void onOrderUpdate(Order order);
}