package com.haulmont.dyakonoff.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;

@NamePattern("%s|product")
@Table(name = "ORDERMANAGEMENT_STOCK")
@Entity(name = "ordermanagement$Stock")
public class Stock extends StandardEntity {
    private static final long serialVersionUID = 5816512182174481053L;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;

    @Column(name = "IN_STOCK", precision = 19, scale = 0)
    protected BigDecimal inStock;

    @DecimalMin("0")
    @Column(name = "OPTIMAL_STOCK_LEVEL", precision = 19, scale = 0)
    protected BigDecimal optimalStockLevel;

    public void setInStock(BigDecimal inStock) {
        this.inStock = inStock;
    }

    public BigDecimal getInStock() {
        return inStock;
    }

    public void setOptimalStockLevel(BigDecimal optimalStockLevel) {
        this.optimalStockLevel = optimalStockLevel;
    }

    public BigDecimal getOptimalStockLevel() {
        return optimalStockLevel;
    }


    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }


}