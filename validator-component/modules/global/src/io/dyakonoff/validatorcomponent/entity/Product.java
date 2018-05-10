package io.dyakonoff.validatorcomponent.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "VALIDATORCOMPONENT_PRODUCT")
@Entity(name = "validatorcomponent$Product")
public class Product extends StandardEntity {
    private static final long serialVersionUID = -6171136468098633694L;

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

    @NotNull
    @Column(name = "PRICE_PER_MEASURE", nullable = false)
    protected BigDecimal pricePerMeasure;

    @Column(name = "VENDOR_EMAIL")
    protected String vendorEmail;

    @Column(name = "VENDOR_SITE_URL")
    protected String vendorSiteUrl;

    public void setVendorSiteUrl(String vendorSiteUrl) {
        this.vendorSiteUrl = vendorSiteUrl;
    }

    public String getVendorSiteUrl() {
        return vendorSiteUrl;
    }


    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorEmail() {
        return vendorEmail;
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

    public void setMeasure(ProductMeasure measure) {
        this.measure = measure == null ? null : measure.getId();
    }

    public ProductMeasure getMeasure() {
        return measure == null ? null : ProductMeasure.fromId(measure);
    }

    public void setWeightPerMeasure(BigDecimal weightPerMeasure) {
        this.weightPerMeasure = weightPerMeasure;
    }

    public BigDecimal getWeightPerMeasure() {
        return weightPerMeasure;
    }


}