# Input data validation in [CUBA platform](https://www.cuba-platform.com/)

## Introduction

Input validation is one of common tasks for everyday developer’s life. We need to check our data in many different situations: after getting data from UI, from API calls, before saving your model to the DB etc, etc

In this article I want to touch the main approaches of data validation that CUBA.platform offers.

Here are the approaches I’d like to discuss:
1. [Bean validation that CUBA Studio offers for entities.](simple-validation/)
1. [Validation with custom annotations.](validation-with-custom-annotations/)
1. [Defining custom Validator class and groovy scripts for UI components.](validator-component/)
1. [Validation in UI screen controllers.](validation-in-controllers/)
1. [Using Entity listeners for validation.](listeners-validation)
1. [Using Transaction listeners to validate your data model.](listeners-validation)

## [Bean Validation](simple-validation/)

This is, without any doubt, the first type of validation that new users of the platform can see in [CUBA studio IDE.](https://www.cuba-platform.com/download)

![Figure 1: Standard entity validators in CUBA studio](resources/figure_1.png)

The studio gives users an easy way to annotate entity fields with most common validators.

As it’s said in [documentation](https://doc.cuba-platform.com/manual-6.8/bean_validation.html) this mechanism allows users to set limitations on entity fields, getters and classes. The annotations are available from `javax.validation.constraints` [namespace](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html) or you can use custom validation annotations which I will describe in the next section.

Let’s look how these annotations look from the code side. Below are different examples of standard annotations usage:

[CargoBay.java](simple-validation/modules/global/src/io/dyakonoff/simplevalidation/entity/CargoBay.java)
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
