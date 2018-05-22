package com.haulmont.dyakonoff.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.math.BigDecimal;
import javax.persistence.Lob;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;

@NamePattern("%s|name")
@Table(name = "ORDERMANAGEMENT_PRODUCT", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_ORDERMANAGEMENT_PRODUCT_UNQ", columnNames = {"NAME", "MEASURE"})
})
@Entity(name = "ordermanagement$Product")
public class Product extends StandardEntity {
    private static final long serialVersionUID = -8100651544837344116L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @Column(name = "MEASURE", nullable = false)
    protected Integer measure;

    @DecimalMin("0.01")
    @NotNull
    @Column(name = "PRICE_PER_MEASURE", nullable = false)
    protected BigDecimal pricePerMeasure;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMeasure(MeasureUnit measure) {
        this.measure = measure == null ? null : measure.getId();
    }

    public MeasureUnit getMeasure() {
        return measure == null ? null : MeasureUnit.fromId(measure);
    }

    public void setPricePerMeasure(BigDecimal pricePerMeasure) {
        this.pricePerMeasure = pricePerMeasure;
    }

    public BigDecimal getPricePerMeasure() {
        return pricePerMeasure;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}