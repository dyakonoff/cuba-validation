package com.haulmont.dyakonoff.orderman.entity.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Checks that Customer has either phone or email
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerContactsCheckValidator.class)
public @interface CustomerContactsCheck {
    String message() default "{msg://com.haulmont.dyakonoff.orderman.entity.validator/CustomerContactsCheck.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
