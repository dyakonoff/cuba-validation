package com.haulmont.dyakonoff.orderman.web.orderitem;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.dyakonoff.orderman.entity.MeasureUnit;
import com.haulmont.dyakonoff.orderman.entity.OrderItem;
import com.haulmont.dyakonoff.orderman.service.StockService;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;

public class OrderItemEdit extends AbstractEditor<OrderItem> {
    @Inject
    private StockService stockService;

    @Named("fieldGroup.quantity")
    private TextField quantityField;

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);

        OrderItem item = getItem();

        // check that only POUNDs could have a fractional number quantity
        MeasureUnit unit = item.getProduct().getMeasure();
        if (unit != MeasureUnit.POUND &&
                item.getQuantity().remainder( BigDecimal.ONE ).compareTo(BigDecimal.ZERO) != 0) {
            String msg = "You can't get a fractional number of items measured in " + unit.toString();
            errors.add(msg);
        }

        // Check that Stock has enough Product
        // This is a preliminary check that helps User to get the feedback earlier
        // The final check happens in Order's EntityListener, to be 100% safe from run conditions
        BigDecimal countInStock = stockService.getProductAvailability(item.getProduct());

        if (item.getQuantity().compareTo(countInStock) > 0) {
            String msg = String.format("Insufficient product '%s' in stock (%s left)",
                    item.getProduct().getName(), countInStock.toString());

            errors.add(quantityField, msg);
        }

        super.postValidate(errors);
    }

    @Override
    protected boolean preCommit() {
        OrderItem item = getItem();
        BigDecimal subTotal = item.getProduct().getPricePerMeasure().multiply(item.getQuantity());
        item.setSubTotal(subTotal);
        return super.preCommit();
    }
}