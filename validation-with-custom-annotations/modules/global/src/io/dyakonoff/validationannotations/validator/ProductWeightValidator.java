package io.dyakonoff.validationannotations.validator;


import io.dyakonoff.validationannotations.entity.Product;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class ProductWeightValidator implements ConstraintValidator<CheckProductWeightType, Product> {
    @Override
    public void initialize(CheckProductWeightType constraint) {
    }

    @Override
    public boolean isValid(Product product, ConstraintValidatorContext context) {
        if (product == null)
            return false;

        switch (product.getMeasure()) {
            case Unit:
                return product.getWeightPerUnit().compareTo(BigDecimal.ZERO) >= 0;
            case Ton:
                return product.getWeightPerUnit().compareTo(new BigDecimal(1000)) == 0;
            case Kilogram:
                return product.getWeightPerUnit().compareTo(BigDecimal.ONE) == 0;
            default:
                throw new IllegalArgumentException("Unexpected value of ProductMeasure type");
        }
    }
}
