package io.haulmont.dyakonoff.cargoship.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Table(name = "CARGOSHIP_ISO_CONTAINER")
@Entity(name = "cargoship$IsoContainer")
public class IsoContainer extends StandardEntity {
    private static final long serialVersionUID = 4871289028008421790L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID", columnDefinition = "Container ISO type")
    protected IsoContainerType type;

    @NotNull
    @Column(name = "SERIAL_NUMBER", nullable = false, length = 11)
    protected String serialNumber;

    @NotNull
    @Column(name = "GROSS_WEIGHT", nullable = false, columnDefinition = "Gross weight of a container")
    protected Double grossWeight;

    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }


    public void setType(IsoContainerType type) {
        this.type = type;
    }

    public IsoContainerType getType() {
        return type;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }


}