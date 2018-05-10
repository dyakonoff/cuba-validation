package io.dyakonoff.validationannotations.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ProductMeasure implements EnumClass<Integer> {

    Unit(10),
    Kilogram(20),
    Ton(30);

    private Integer id;

    ProductMeasure(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ProductMeasure fromId(Integer id) {
        for (ProductMeasure at : ProductMeasure.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}