# Input data validation in [CUBA platform](https://www.cuba-platform.com/)

## Introduction

Input validation is one of common tasks for everyday developer’s life. We need to check our data in many different situations: after getting data from UI, from API calls, before saving your model to the DB etc, etc

In this article I want to touch the main approaches of data validation that CUBA.platform offers.

Here are the approaches I’d like to discuss:
1. [Bean validation that CUBA Studio offers for entities](#Bean-Validation) / _[Example](simple-validation/)_
1. [Validation with custom annotations](#Validation-with-custom-annotations) / _[Example 1](validation-with-custom-annotations/), [Example 2](https://github.com/cuba-platform/sample-user-registration/tree/master/modules/global/src/com/company/sample)_
1. [Defining custom Validator class and groovy scripts for UI components.](#Custom-validator-classes-and-scripts) [ _[Example](validator-component/)_ ]
1. [Validation in UI screen controllers.]() [ _[Example](validation-in-controllers/)_ ]
1. [Using Entity listeners for validation.]() [ _[Example](listeners-validation)_ ]
1. [Using Transaction listeners to validate your data model.]() [ _[Example](listeners-validation)_ ]

## [Bean Validation](simple-validation/)

This is, without any doubt, the first type of validation that new users of the platform can see in [CUBA studio IDE.](https://www.cuba-platform.com/download) It gives users an easy way to annotate entity fields through the editor screen with the most common validators.

![Figure 1: Standard entity validators in CUBA studio](resources/figure_1.png)

Annotation-based validation provides uniform validation of data on the middleware, in [Generic UI](https://doc.cuba-platform.com/manual-6.8/gui_framework.html) and [REST API](https://doc.cuba-platform.com/manual-6.8/rest_api_v2.html). It is based on the JSR 349 - Bean Validation 1.1 and its reference implementation: [Hibernate Validator](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/?v=5.3).

As it’s said in [documentation](https://doc.cuba-platform.com/manual-6.8/bean_validation.html) this mechanism allows users to set limitations on entity fields, getters and classes. The annotations are available from `javax.validation.constraints` [namespace](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html) or you can use custom validation annotations which I will describe in the next section.

Let’s look how these annotations look from the code side. Below are different examples of standard annotations usage:

```java
@NamePattern("%s|name")
@Table(name = "SIMPLEVALIDATION_CARGO_BAY")
@Entity(name = "simplevalidation$CargoBay")
public class CargoBay extends StandardEntity {
    private static final long serialVersionUID = 6822994453230640530L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @Max(message = "Bay number can't be greater than 100", value = 100)
    @Min(message = "Bay number can't be negative", value = 0)
    @NotNull
    @Column(name = "BAY_NUMBER", nullable = false, unique = true)
    protected Integer bayNumber;

    @DecimalMax("100000")
    @DecimalMin("0")
    @NotNull
    @Column(name = "MAX_LOAD", nullable = false)
    protected BigDecimal maxLoad;

    @DecimalMax("1000000.0")
    @DecimalMin("1.0")
    @NotNull
    @Column(name = "BAY_AREA", nullable = false)
    protected Double bayArea;

    @Temporal(TemporalType.DATE)
    @Past
    @NotNull
    @Column(name = "LAST_OPERATION_DATE", nullable = false)
    protected Date lastOperationDate;

    ...

}
```
[CargoBay.java](simple-validation/modules/global/src/io/dyakonoff/simplevalidation/entity/CargoBay.java)

Although these annotations are simple to use and well documented, they can cover only simple cases. However, sometimes we want to express more complex limitations while keeping the expressiveness and reusability of annotations-based approach. This is feaseble with custom validation annotations.

## Validation with custom annotations

[Custom annotations](https://doc.cuba-platform.com/manual-6.8/bean_validation_constraints.html#bean_validation_custom_constraints) could be defined not just for entities fields but also for Entity classes, POJOs and service methods. Let’s check out how to do that.

Assume we are building a products management system and an entity Product could have different measures:
* Units
* Kilograms
* Tons
On the other hand, `Product` class has `weightPerMeasure` field, which displays the weight of one product item. It’s quite obvious that only if product item’s measure is `ProductMeasure::Unit` then this `weightPerMeasure` can have arbitrary value (but still non-negative). If `product.measure == Kilograms` then `product.weightPerMeasure` should be equal 1 (one kilogram weights one kilogram exactly). The same is for Tons (1 ton = 1000 kilograms).

So, having the annotation expressing this just in one source code line looks quite appealing.

Following the [documentation](https://doc.cuba-platform.com/manual-6.8/bean_validation_constraints.html#bean_validation_custom_constraints) let's declare `@CheckProductWeightType` annotation that we want to be able to link to `weightPerMeasure` field but to be called when entity editor commits data as well.

```java
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProductWeightValidator.class)
public @interface CheckProductWeightType {
    String message() default "{msg://io.dyakonoff.validationannotations.validator/CheckProductWeightType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```
[CheckProductWeightType.java](validation-with-custom-annotations/modules/global/src/io/dyakonoff/validationannotations/validator/CheckProductWeightType.java)

Next step is to define implementation (which is specified in `@Constraint(validatedBy = ... )`) for this annotation and provide a [message pack](https://doc.cuba-platform.com/manual-6.8/message_packs.html), although you can use some existing message pack as well.

```java
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
                return product.getWeightPerMeasure().compareTo(BigDecimal.ZERO) >= 0;
            case Ton:
                return product.getWeightPerMeasure().compareTo(new BigDecimal(1000)) == 0;
            case Kilogram:
                return product.getWeightPerMeasure().compareTo(BigDecimal.ONE) == 0;
            default:
                throw new IllegalArgumentException("Unexpected value of ProductMeasure type");
        }
    }
}
```
[ProductWeightValidator.java](validation-with-custom-annotations/modules/global/src/io/dyakonoff/validationannotations/validator/ProductWeightValidator.java)

[messages.properties](validation-with-custom-annotations/modules/global/src/io/dyakonoff/validationannotations/validator/messages.properties)

So, we can see that defining custom annotations gives you an elegant, stable reusable way of doing complex validation logic in most number of cases that works for both UI, middleware and REST.

It would be a good point to finish this article at this stage, and if you are in a hurry you can stop reading here. However, this tutorial won't be complete if I didn't touch other ways that can be used for data validation:

1.	Defining a custom `Validator` class or groovy script for the field.
1.	Overriding `postValidate` method of a screen editor controller.
1.	Setting up an entity listener.
1.	Providing a transaction listener which to do validation before committing data into your database (which is the only way when many different entities are involved into the check).

Ok, let's start our advanced topics...

## Custom validator classes and scripts
