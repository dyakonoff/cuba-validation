package io.haulmont.dyakonoff.cargoship.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("Hold #%s|number")
@Table(name = "CARGOSHIP_CARGO_HOLD")
@Entity(name = "cargoship$CargoHold")
public class CargoHold extends StandardEntity {
    private static final long serialVersionUID = -591123526384978222L;

    @NotNull
    @Column(name = "NUMBER_", nullable = false)
    protected Integer number;

    @NotNull
    @Column(name = "WIDTH", nullable = false, columnDefinition = "Width in containers")
    protected Integer width;

    @NotNull
    @Column(name = "LEVELS_BELOW_DECK", nullable = false, columnDefinition = "Number of levels below deck level")
    protected Integer levelsBelowDeck;

    @NotNull
    @Column(name = "ALLOWED_LEVELS_ABOVE_DECK", nullable = false, columnDefinition = "Number of container levels above deck level")
    protected Integer allowedLevelsAboveDeck;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SHIP_ID")
    protected Ship ship;

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Ship getShip() {
        return ship;
    }


    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getWidth() {
        return width;
    }

    public void setLevelsBelowDeck(Integer levelsBelowDeck) {
        this.levelsBelowDeck = levelsBelowDeck;
    }

    public Integer getLevelsBelowDeck() {
        return levelsBelowDeck;
    }

    public void setAllowedLevelsAboveDeck(Integer allowedLevelsAboveDeck) {
        this.allowedLevelsAboveDeck = allowedLevelsAboveDeck;
    }

    public Integer getAllowedLevelsAboveDeck() {
        return allowedLevelsAboveDeck;
    }


    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }


}