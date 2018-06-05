package com.haulmont.dyakonoff.orderman.web.order;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.dyakonoff.orderman.entity.Order;
import com.haulmont.dyakonoff.orderman.entity.OrderItem;
import com.haulmont.dyakonoff.orderman.entity.OrderStatus;

import javax.inject.Inject;
import java.math.BigDecimal;

public class OrderEdit extends AbstractEditor<Order> {
    @Inject
    private TimeSource timeSource;

    @Inject
    private Table<OrderItem> itemsTable;


    @Override
    protected void initNewItem(Order order) {
        super.initNewItem(order);
        order.setStatus(OrderStatus.NEW);
        order.setPrice(BigDecimal.ZERO);
        order.setDate(timeSource.currentTimestamp());
    }

    @Override
    protected boolean preCommit() {
        Order order = getItem();
        BigDecimal price = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            price = price.add(item.getSubTotal());
        }
        order.setPrice(price);
        return super.preCommit();
    }
}