package com.haulmont.dyakonoff.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum MeasureUnit implements EnumClass<Integer> {

    POUND(10),
    COUNT(20),
    PACK(30);

    private Integer id;

    MeasureUnit(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static MeasureUnit fromId(Integer id) {
        for (MeasureUnit at : MeasureUnit.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}