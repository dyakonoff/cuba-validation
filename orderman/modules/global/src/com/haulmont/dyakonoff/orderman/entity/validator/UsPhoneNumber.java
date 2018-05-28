package com.haulmont.dyakonoff.orderman.entity.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

/*
^(1\+?)?((\([0-9]{3}\))|[0-9]{3})[\s\-]?[\0-9]{3}[\s\-]?[0-9]{4}$
^(\+1\s\([2-9](\d){2}\)-[2-9](\d){2}-(\d){4})$

 */

/**
 * Validates the US phone numbers format: `+1 NXX-NXX-XXXX` , where: `N`=digits 2–9, `X`=digits 0–9
 */
@Pattern.List({
        @Pattern(regexp = "((\\+1\\s)?[2-9](\\d){2}-[2-9](\\d){2}-(\\d){4})"),
        @Pattern(regexp = "((\\+1\\s\\)?([2-9](\\d){2}\\)-[2-9](\\d){2}-(\\d){4})"),
        @Pattern(regexp = "((\\+1\\s)?[2-9](\\d){2}\\s[2-9](\\d){2}\\s(\\d){4})"),
        @Pattern(regexp = "((\\+1\\s\\)?([2-9](\\d){2}\\)\\s[2-9](\\d){2}\\s(\\d){4})")
})
@ReportAsSingleViolation
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface UsPhoneNumber {
    String message() default "{msg://com.haulmont.dyakonoff.orderman.entity.validator/PhoneNumberError.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
