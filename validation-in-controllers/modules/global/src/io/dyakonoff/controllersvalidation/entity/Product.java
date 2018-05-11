package io.dyakonoff.controllersvalidation.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.validation.constraints.DecimalMin;

@NamePattern("%s|name")
@Table(name = "CONTROLLERSVALIDATION_PRODUCT")
@Entity(name = "controllersvalidation$Product")
public class Product extends StandardEntity {
    private static final long serialVersionUID = 527771070825609827L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "RETAIL", nullable = false)
    protected Boolean retail = false;

    @DecimalMin("0")
    @NotNull
    @Column(name = "PRICE", nullable = false)
    protected BigDecimal price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VENDOR_ID")
    protected Vendor vendor;


    public void setRetail(Boolean retail) {
        this.retail = retail;
    }

    public Boolean getRetail() {
        return retail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Vendor getVendor() {
        return vendor;
    }


}