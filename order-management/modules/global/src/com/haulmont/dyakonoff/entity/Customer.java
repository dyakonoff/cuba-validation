package com.haulmont.dyakonoff.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Email;
import com.haulmont.cuba.core.entity.StandardEntity;
import org.hibernate.validator.constraints.Length;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "ORDERMANAGEMENT_CUSTOMER")
@Entity(name = "ordermanagement$Customer")
public class Customer extends StandardEntity {
    private static final long serialVersionUID = -3702113892581267372L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Email(message = "Email should have valid email address format", regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @NotNull
    @Column(name = "EMAIL", nullable = false, unique = true)
    protected String email;

    // TODO: Add phone validation with @Pattern JPA
    @Column(name = "PHONE", length = 30)
    protected String phone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOGO_IMAGE_ID")
    protected FileDescriptor logoImage;

    @Length(message = "Address line 1 should have length not less than 5", min = 5)
    @NotNull
    @Column(name = "ADDRESS_LINE1", nullable = false)
    protected String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    protected String addressLine2;

    @Column(name = "ADDRESS_LINE3")
    protected String addressLine3;

    @Pattern(message = "Postal code should follow US ZIP codes format: 12345 or 12345-6789 or 12345 1234", regexp = "^\\d{5}(?:[-\\s]\\d{4})?$")
    @NotNull
    @Column(name = "POSTAL_CODE", nullable = false, length = 16)
    protected String postalCode;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setLogoImage(FileDescriptor logoImage) {
        this.logoImage = logoImage;
    }

    public FileDescriptor getLogoImage() {
        return logoImage;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }


}