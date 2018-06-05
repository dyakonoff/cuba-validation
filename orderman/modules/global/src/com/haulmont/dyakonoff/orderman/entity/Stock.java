package com.haulmont.dyakonoff.orderman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.validation.constraints.DecimalMax;

@NamePattern("%s|product")
@Table(name = "ORDERMAN_STOCK")
@Entity(name = "orderman$Stock")
public class Stock extends StandardEntity {
    private static final long serialVersionUID = 1491476123455758481L;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ID", unique = true)
    protected Product product;

    @DecimalMax(message = "We don't keep more than 10,000 items in stock", value = "10000")
    @DecimalMin(message = "Stock can't go below 0", value = "0")
    @Column(name = "IN_STOCK")
    protected BigDecimal inStock;

    @DecimalMin(message = "Optimal stock level should be {0}or greater", value = "0")
    @Column(name = "OPTIMAL_STOCK_LEVEL")
    protected BigDecimal optimalStockLevel;

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

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
}