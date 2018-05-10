package io.dyakonoff.validationannotations.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProductWeightValidator.class)
public @interface CheckProductWeightType {
    String message() default "{msg://io.dyakonoff.cargoload.validator/CheckProductWeightType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
