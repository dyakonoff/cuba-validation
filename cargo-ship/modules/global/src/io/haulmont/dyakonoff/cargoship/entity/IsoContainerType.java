package io.haulmont.dyakonoff.cargoship.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|isoType")
@Table(name = "CARGOSHIP_ISO_CONTAINER_TYPE")
@Entity(name = "cargoship$IsoContainerType")
public class IsoContainerType extends StandardEntity {
    private static final long serialVersionUID = 8513690997217796174L;

    @NotNull
    @Column(name = "ISO_TYPE", nullable = false, unique = true)
    protected Integer isoType;

    @NotNull
    @Column(name = "TEU_VOLUME", nullable = false, columnDefinition = "Container volume in TEU")
    protected Double teuVolume;

    @NotNull
    @Column(name = "MAX_WEIGHT", nullable = false, columnDefinition = "Maximum allowed weight of load and a container")
    protected Double maxWeight;

    public void setTeuVolume(Double teuVolume) {
        this.teuVolume = teuVolume;
    }

    public Double getTeuVolume() {
        return teuVolume;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }


    public void setIsoType(KnownContainerType isoType) {
        this.isoType = isoType == null ? null : isoType.getId();
    }

    public KnownContainerType getIsoType() {
        return isoType == null ? null : KnownContainerType.fromId(isoType);
    }


}