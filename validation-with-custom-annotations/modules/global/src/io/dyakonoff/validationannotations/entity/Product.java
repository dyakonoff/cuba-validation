package io.dyakonoff.validationannotations.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.validation.groups.UiCrossFieldChecks;
import io.dyakonoff.validationannotations.validator.CheckProductWeightType;

@CheckProductWeightType(groups = UiCrossFieldChecks.class)
@NamePattern("%s|name")
@Table(name = "VALIDATIONANNOTATIONS_PRODUCT")
@Entity(name = "validationannotations$Product")
public class Product extends StandardEntity {
    private static final long serialVersionUID = 1561721020865033907L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    protected String name;

    @NotNull
    @Column(name = "MEASURE", nullable = false)
    protected Integer measure;

    @DecimalMin("0")
    @NotNull
    @Column(name = "WEIGHT_PER_MEASURE", nullable = false)
    protected BigDecimal weightPerMeasure;

    @DecimalMin("0")
    @NotNull
    @Column(name = "PRICE_PER_MEASURE", nullable = false)
    protected BigDecimal pricePerMeasure;

    public void setPricePerMeasure(BigDecimal pricePerMeasure) {
        this.pricePerMeasure = pricePerMeasure;
    }

    public BigDecimal getPricePerMeasure() {
        return pricePerMeasure;
    }


    public void setWeightPerMeasure(BigDecimal weightPerMeasure) {
        this.weightPerMeasure = weightPerMeasure;
    }

    public BigDecimal getWeightPerMeasure() {
        return weightPerMeasure;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMeasure(ProductMeasure measure) {
        this.measure = measure == null ? null : measure.getId();
    }

    public ProductMeasure getMeasure() {
        return measure == null ? null : ProductMeasure.fromId(measure);
    }

}