package io.haulmont.dyakonoff.cargoship.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|name")
@Table(name = "CARGOSHIP_SHIP")
@Entity(name = "cargoship$Ship")
public class Ship extends StandardEntity {
    private static final long serialVersionUID = 3457661174276915770L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "MAX_TONNAGE", nullable = false, columnDefinition = "Net register tonnage")
    protected Double maxTonnage;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ship")
    protected List<CargoHold> holds;

    public void setHolds(List<CargoHold> holds) {
        this.holds = holds;
    }

    public List<CargoHold> getHolds() {
        return holds;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMaxTonnage(Double maxTonnage) {
        this.maxTonnage = maxTonnage;
    }

    public Double getMaxTonnage() {
        return maxTonnage;
    }


}