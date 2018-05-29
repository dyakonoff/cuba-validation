package com.haulmont.dyakonoff.orderman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.haulmont.cuba.core.global.validation.groups.UiCrossFieldChecks;
import com.haulmont.dyakonoff.orderman.entity.validator.CustomerContactsCheck;
import com.haulmont.dyakonoff.orderman.entity.validator.UsPhoneNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

@CustomerContactsCheck(groups = {Default.class, UiCrossFieldChecks.class})
@NamePattern("%s (%s)|name,email")
@Table(name = "ORDERMAN_CUSTOMER")
@Entity(name = "orderman$Customer")
public class Customer extends StandardEntity {
    private static final long serialVersionUID = -8612760748017102060L;

    @Length(min = 1)
    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "EMAIL")
    @Email(message = "Email should have valid email address format", regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    protected String email;

    @UsPhoneNumber
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

    public void setLogoImage(FileDescriptor logoImage) {
        this.logoImage = logoImage;
    }

    public FileDescriptor getLogoImage() {
        return logoImage;
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


    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }


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


}