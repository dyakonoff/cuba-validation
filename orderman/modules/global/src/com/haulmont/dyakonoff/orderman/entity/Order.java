package com.haulmont.dyakonoff.orderman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.haulmont.cuba.core.entity.StandardEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.OneToMany;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import org.hibernate.validator.constraints.Length;

@Listeners("orderman_OrderEntityListener")
@NamePattern("%s order#: %s|customer,number")
@Table(name = "ORDERMAN_ORDER")
@Entity(name = "orderman$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = -5542761764517463640L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @Temporal(TemporalType.DATE)
    @Past(message = "Order date can't be in the future")
    @NotNull
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @Length(max = 20)
    @Pattern(message = "Number format should be yyyy-MM-dd-<sequential number>", regexp = "\\d{4}-\\d{2}-\\d{2}-\\d+")
    @NotNull
    @Column(name = "NUMBER_", nullable = false, unique = true, length = 20)
    protected String number;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected Integer status;

    @Size(min = 1, max = 10)
    @Valid
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "order")
    protected List<OrderItem> items;

    @DecimalMin(message = "Price should be greater than {value}", value = "0")
    @NotNull
    @Column(name = "PRICE", nullable = false)
    protected BigDecimal price;

    public void setStatus(OrderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public OrderStatus getStatus() {
        return status == null ? null : OrderStatus.fromId(status);
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }


    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }


}