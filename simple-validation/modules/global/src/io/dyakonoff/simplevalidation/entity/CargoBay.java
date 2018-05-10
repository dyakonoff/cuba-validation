package io.dyakonoff.simplevalidation.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "SIMPLEVALIDATION_CARGO_BAY")
@Entity(name = "simplevalidation$CargoBay")
public class CargoBay extends StandardEntity {
    private static final long serialVersionUID = 6822994453230640530L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @Max(message = "Bay number can't be greater than 100", value = 100)
    @Min(message = "Bay number can't be negative", value = 0)
    @NotNull
    @Column(name = "BAY_NUMBER", nullable = false, unique = true)
    protected Integer bayNumber;

    @DecimalMax("100000")
    @DecimalMin("0")
    @NotNull
    @Column(name = "MAX_LOAD", nullable = false)
    protected BigDecimal maxLoad;

    @DecimalMax("1000000.0")
    @DecimalMin("1.0")
    @NotNull
    @Column(name = "BAY_AREA", nullable = false)
    protected Double bayArea;

    @Temporal(TemporalType.DATE)
    @Past
    @NotNull
    @Column(name = "LAST_OPERATION_DATE", nullable = false)
    protected Date lastOperationDate;

    public void setBayNumber(Integer bayNumber) {
        this.bayNumber = bayNumber;
    }

    public Integer getBayNumber() {
        return bayNumber;
    }

    public void setMaxLoad(BigDecimal maxLoad) {
        this.maxLoad = maxLoad;
    }

    public BigDecimal getMaxLoad() {
        return maxLoad;
    }

    public void setBayArea(Double bayArea) {
        this.bayArea = bayArea;
    }

    public Double getBayArea() {
        return bayArea;
    }

    public void setLastOperationDate(Date lastOperationDate) {
        this.lastOperationDate = lastOperationDate;
    }

    public Date getLastOperationDate() {
        return lastOperationDate;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}