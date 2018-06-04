package com.haulmont.dyakonoff.orderman.service;

import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.dyakonoff.orderman.entity.Order;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.dyakonoff.orderman.entity.Stock;

@Component("orderman_DemoEntityListener")
public class DemoEntityListener implements BeforeInsertEntityListener<Stock>, BeforeUpdateEntityListener<Stock> {


    @Override
    public void onBeforeInsert(Stock entity, EntityManager entityManager) {

    }


    @Override
    public void onBeforeUpdate(Stock entity, EntityManager entityManager) {

    }


}