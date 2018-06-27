package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.validation.RequiredView;
import com.haulmont.dyakonoff.orderman.entity.OrderItem;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.dyakonoff.orderman.entity.Order;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Sets the serial number for the order and validates that price is correct
 */
@Validated
@Component("orderman_OrderEntityListener")
public class OrderEntityListener implements BeforeInsertEntityListener<Order>, BeforeUpdateEntityListener<Order> {
    @Inject
    private TimeSource timeSource;

    @Inject
    private UniqueNumbersAPI uniqueNumbersAPI;

    @Override
    public void onBeforeInsert(@RequiredView("order-edit") Order order, EntityManager entityManager) {
        validateOrderPrice(order);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String date = sdf.format(timeSource.currentTimestamp());
        long serialNumb = uniqueNumbersAPI.getNextNumber("order_" + date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = sdf2.format(timeSource.currentTimestamp());
        order.setNumber(date2 + '-' + Long.toString(serialNumb));
    }

    @Override
    public void onBeforeUpdate(@RequiredView("order-edit") Order order, EntityManager entityManager) {
        validateOrderPrice(order);
    }

    private void validateOrderPrice(@NotNull Order order) {
        BigDecimal price = order.getPrice();
        for (OrderItem item : order.getItems()) {
            price = price.subtract(item.getSubTotal());
        }
        if (price.compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException("Order price does not match to the total cost of Order Items");
        }
    }
}