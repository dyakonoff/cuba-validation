package com.haulmont.dyakonoff.orderman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.UniqueConstraint;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import org.hibernate.validator.constraints.Length;

@NamePattern("%s|name")
@Table(name = "ORDERMAN_PRODUCT", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_ORDERMAN_PRODUCT_UNQ", columnNames = {"NAME", "MEASURE"})
})
@Entity(name = "orderman$Product")
public class Product extends StandardEntity {
    private static final long serialVersionUID = -8918522012175747344L;

    @Length(max = 255)
    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @Column(name = "MEASURE", nullable = false)
    protected Integer measure;

    @DecimalMin(message = "Price per measure should be {value} at least", value = "0.01")
    @NotNull
    @Column(name = "PRICE_PER_MEASURE", nullable = false)
    protected BigDecimal pricePerMeasure;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "product")
    protected Stock stock;

    public void setPricePerMeasure(BigDecimal pricePerMeasure) {
        this.pricePerMeasure = pricePerMeasure;
    }

    public BigDecimal getPricePerMeasure() {
        return pricePerMeasure;
    }


    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Stock getStock() {
        return stock;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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


}