package io.haulmont.dyakonoff.cargoship.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum KnownContainerType implements EnumClass<Integer> {

    standard_20(10),
    standard_40(20),
    standard_45(30),
    standard_48(40),
    standard_53(50),
    highCube_20(60),
    highCube_40(70),
    highCube_45(80),
    highCube_48(90),
    highCube_53(100);

    private Integer id;

    KnownContainerType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static KnownContainerType fromId(Integer id) {
        for (KnownContainerType at : KnownContainerType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}