package com.haulmont.dyakonoff.orderman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s * %s|product,quantity")
@Table(name = "ORDERMAN_ORDER_ITEM")
@Entity(name = "orderman$OrderItem")
public class OrderItem extends StandardEntity {
    private static final long serialVersionUID = 6460734219111806410L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;

    @DecimalMin(message = "Quantity should be greater than 0", value = "0", inclusive = false)
    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected BigDecimal quantity;

    @DecimalMin(message = "Sub total should be greater than 0", value = "0", inclusive = false)
    @NotNull
    @Column(name = "SUB_TOTAL", nullable = false)
    protected BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }


    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }


}