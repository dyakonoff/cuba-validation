package com.haulmont.dyakonoff.orderman.entity.validator;

import com.haulmont.dyakonoff.orderman.entity.Customer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomerContactsCheckValidator implements ConstraintValidator<CustomerContactsCheck, Customer> {

    @Override
    public void initialize(CustomerContactsCheck constraint) {
    }

    @Override
    public boolean isValid(Customer customer, ConstraintValidatorContext context) {
        if (customer == null)
            return false;


        return !((customer.getEmail() == null || customer.getEmail().length() == 0) &&
                  customer.getPhone() == null || customer.getPhone().length() == 0);
    }
}
