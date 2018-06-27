<h1>Validation cookbook for CUBA applications</h1>

## Content

* [Content](#content)
* [Introduction](#introduction)
* [Model problem description](#model-problem-description)
* [Sample application](#sample-application)
* [Bean validation](#bean-validation)
    * [JPA constraints](#jpa-constraints)
        * [Single column JPA constraints](#single-column-jpa-constraints)
        * [Multi-column JPA constraints](#multi-column-jpa-constraints)
    * [Single field constraints](#single-field-constraints)
    * [Bean validation with custom annotations](#bean-validation-with-custom-annotations)
    * [Notes on bean validation](#notes-on-bean-validation)
        * [At what level bean annotation works](#at-what-level-bean-annotation-works)
        * [Validation of related objects](#validation-of-related-objects)
        * [Custom messages in bean validation constraints](#custom-messages-in-bean-validation-constraints)
        * [Message packs in bean validation constraints](#message-packs-in-bean-validation-constraints)
* [Validation in REST](#validation-in-rest)
    * [Universal REST](#universal-rest)
    * [REST queries validation](#rest-queries-validation)
    * [Validation by contract](#validation-by-contract)
    * [Programmatic Validation](#programmatic-validation)
    * [Validation errors in REST](#validation-errors-in-rest)
* [GUI Validator](#gui-validator)
    * [Standard validators](#standard-validators)
    * [Setting validator programmatically](#setting-validator-programmatically)
    * [Custom Java class validator](#custom-java-class-validator)
    * [Validating with Groovy scripts](#validating-with-groovy-scripts)
* [Validation in UI screen controllers](#validation-in-ui-screen-controllers)
* [Using middleware listeners for data validation](#using-middleware-listeners-for-data-validation)
    * [Single Entity Context example](#single-entity-context-example)
    * [Transactional Context example](#transactional-context-example)
* [Presenting error messages to a user](#presenting-error-messages-to-a-user)
* [Summary](#summary)
* [Appendix A](#appendix-a)
    * [CUBA Documentation articles related to validation](#cuba-documentation-articles-related-to-validation)
    * [Other reading](#other-reading)

## Introduction

Input validation is one of the common tasks in everyday developer’s life. We need to check our data in many different situations: after getting data from UI, in API call handlers, before saving our model to the DB etc, etc.

Main goal of this cookbook is to summarize all validation methods that are common to [CUBA platform](https://www.cuba-platform.com/), give detailed explanations with examples for all of them and talk a bit about pros and cons of each of these approaches. I hope that the article will be a good tutorial and reference for all the questions related to data validation in [CUBA platform](https://www.cuba-platform.com/) based applications.

The sample application for this article could be downloaded from [here](https://github.com/dyakonoff/cuba-validation-examples).

A list of additional examples and materials for further reading is in [Appendix A](#appendix_a).

Happy reading!

![Tags cloud](resources/tags_cloud.png)

[Top](#content)

## Model problem description

Before discussing different validation methods available for us in [CUBA platform](https://www.cuba-platform.com/) application, let's discuss what the application does first.

For this article I'm going to use an application that demonstrates all validation methods mentioned here and how they work from different perspectives: code, user interface, CUBA studio IDE, REST endpoints etc.

This application mimics a small order-management system for an US-based store that ships its goods over the US. That's why this system uses imperial measurement units and enforces US phone numbers and ZIP codes formats.

The application's entities structure is shown below:

![Figure 1: Entities structure](resources/database_scheme_sm.png)

_**Figure 1:** Entities structure._

For more detailed description of the application's requirements and data constraints you can consult [this page](order-management.md).

List of implemented validation methods with links is [here](validations-index.md).

[Top](#content)

## Sample application

The sample application's repository is [here](https://github.com/dyakonoff/cuba-validation-examples/tree/master/orderman). I encourage you to clone it using the next command:

```bash
git clone git@github.com:dyakonoff/cuba-validation-examples.git
```

Or download sample app .zip archive from [here](https://github.com/dyakonoff/cuba-validation-examples/archive/master.zip).


[Top](#content)

## Bean validation

Let's start the review of validation methods with the simplest ones that we have in our toolbox: bean validations. Annotation-based validators provide uniform approach to data checking on the middleware, [GUI](https://doc.cuba-platform.com/manual-6.9/gui_framework.html) and [universal REST services](https://doc.cuba-platform.com/manual-6.9/rest_api_v2.html). They are based on the JSR 349 - Bean Validation 1.1 and its reference implementation: [Hibernate Validator](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/?v=5.3).

[Documentation](https://doc.cuba-platform.com/manual-6.9/bean_validation.html) says that this mechanism allows users to set limitations on entity fields, getters and classes. Most of the annotations are available from [`javax.validation.constraints` namespace](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html), although couple of them come from `javax.persistence`, `javax.validation` and `org.hibernate.validator.constraints`.

Also, it's not hard to create your own custom annotations to validate fields and entities, which we'll see in the [later sections](#bean-validation-with-custom-annotations).

[Top](#content)

### JPA constraints

JPA annotations put constraints on DB level as table indexes or table column / multi-column constraints. Although there are only few of such annotations, they are only ones which act on DB server level.

* `@Column(..., unique=true)` - sets SQL `unique` constraint on a table column for entity fields marked as **Unique**
* `@Column(..., nullable=false)` - sets SQL `not null` constraint on a table column (entity field) for fields marked as **Mandatory** in studio. Acts together with `@NotNull` JPA annotation which works at UI and middleware levels.
* `@Column(..., length = 16)` - sets length of a `varchar` column, and limits the maximum input field length at UI level.
* `@UniqueConstraint` - sets a multi column index with `unique` constraint.

[Top](#content)

#### Single column JPA constraints

Single column constraints could be applied from CUBA studio entity editor UI during entity field creation or modification:

![Figure 2: Setting column level DB constraints from CUBA](resources/db_constraints_column_level.png)

_**Figure 2:** Setting column level DB constraints from CUBA._

They are reflected in Java code in the next manner (which you can always edit manually, of course):

```java
public class Order extends StandardEntity {
...
    @NotNull
    @Column(name = "NUMBER_", nullable = false, unique = true, length = 20)
    protected String number;
...
}
```

[Order.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Order.java)

These `@Column` annotation parameters are translated to the DDL like that:

```sql
create table ORDERMAN_ORDER (
    ...
    NUMBER_ varchar(20) not null,
    ...
)
...
-- unique indexes
create unique index IDX_ORDERMAN_ORDER_UNIQ_NUMBER_ on ORDERMAN_ORDER (NUMBER_)
```

Note that CUBA studio automatically adds `@NotNull` constraint to the field marked as **Mandatory** in Entity designer. Hence, this validations are made on UI and middleware level as well.

On the other hand, **Unique** constraint (reflected with `@Column(..., unique=true)` annotation) works only on DB level, because it can not be validated on the application level without reading whole SQL table which might be quite expensive operation.

[Top](#content)

#### Multi-column JPA constraints

This type of data constraint / validation acts on DB level only and is represented by multi-column index with unique constraint. It could be created from Entity designer in CUBA studio:

![Figure 3: Creating multi-column unique constraint](resources/unique_index_editor.png)

_**Figure 3:** Creating multi-column unique constraint._

Or right in your Java code:

```java
@NamePattern("%s|name")
@Table(name = "ORDERMAN_PRODUCT", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_ORDERMAN_PRODUCT_UNQ", columnNames = {"NAME", "MEASURE"})
})
@Entity(name = "orderman$Product")
public class Product extends StandardEntity {
    ...
}
```

[Product.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Product.java)

Which adds `create unique index IDX_ORDERMAN_PRODUCT_UNQ on ORDERMAN_PRODUCT (NAME, MEASURE)` to generated DDL.

[Top](#content)

### Single field constraints

Let's go through other standard annotations that were designed for data validation. As I mentioned before most of them came from [`javax.validation.constraints` namespace](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html), although some are from `javax.persistence`, `javax.validation` and `org.hibernate.validator.constraints`. For details, you can take a look at [CUBA documentation section about bean validation](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html).

Some of these annotations can be configured from CUBA studio in VALIDATION section (list of constraints available varies for different entity field types).

![Figure 4: Bean validation tab](resources/jpa_validation_tab.png)

_**Figure 4:** Validation tab._

From the code perspective these annotations look like that:

```java
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

    @DecimalMin(message = "Price should be greater than 0", value = "0")
    @NotNull
    @Column(name = "PRICE", nullable = false)
    protected BigDecimal price;

    ...

}
```

[Order.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Order.java)

[Here](common_constraints_annotations.md) you can find a list of common annotations used in CUBA applications.

[Top](#content)

### Bean validation with custom annotations

There is no need to limit ourselves with the standard constraint annotations, if it's needed we can define our custom ones. 
[Custom annotations](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_custom_constraints) could be defined not just for entities fields but also for Entity classes, POJOs and service methods. Custom annotations can help you to express your validation logic clearer or implement custom checks that can even do the cross-field checks of an entity object.

Let’s check out how to do that. In our sample application there at [two custom constraints](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator)  implemented:

* `@UsPhoneNumber` - a constraint [which](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator/UsPhoneNumber.java) is a descendant of `@Pattern` annotation and does the simple regexp check for the phone format.
* `@CustomerContactsCheck` ensures that a customer entity has either phone number **or** email specified.

`@UsPhoneNumber` validator is quite primitive and implemented as a custom interface:

```java
/**
 * Validates the US phone numbers format: `+1 (NXX) NXX-XXXX` , where: `N`=digits 2–9, `X`=digits 0–9
 */
@Pattern(regexp = "\\+1\\s\\([2-9](\\d){2}\\)\\s[2-9](\\d){2}-(\\d){4}")
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface UsPhoneNumber {
    String message() default "{msg://com.haulmont.dyakonoff.orderman.entity.validator/PhoneNumberError.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

[UsPhoneNumber](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator/UsPhoneNumber.java)

Going through the code we should note the next:

* `@Constraint(validatedBy = {})` says that there is no actual class implementing the validator.
* `@Retention(RetentionPolicy.RUNTIME)` marks the annotation as a runtime one, which all validators should be.
* `@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })` specifies targets that this annotation can be applied to. Although, in our case, it would be possible to limit the scope only with `ElementType.FIELD`.
* And finally, `@Pattern(...)` part specifies the actual validator's behavior.

Usage of such annotation is simple:

```java
public class Customer extends StandardEntity {
    ...

    @UsPhoneNumber
    @Column(name = "PHONE", length = 30)
    protected String phone;

    ...
}
```

[Customer.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Customer.java)

You can also check the more complex example of a custom validator that comes with hibernate and uses a list of checks to validate data:  [org.hibernate.validator.constraints.br.CPF](https://github.com/hibernate/hibernate-validator/blob/master/engine/src/main/java/org/hibernate/validator/constraints/br/CPF.java).

Now, it's time to take a look at more complex custom annotation that does cross-field check. `@CustomerContactsCheck` interface defines a constraint that could be applied to classes only:

```java
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
```

[CustomerContactsCheck](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator/CustomerContactsCheck.java)

This annotation using `@Constraint(validatedBy = CustomerContactsCheckValidator.class)` refers to the next implementation:

```java
public class CustomerContactsCheckValidator implements ConstraintValidator<CustomerContactsCheck, Customer> {
    @Override
    public void initialize(CustomerContactsCheck constraint) {
    }

    @Override
    public boolean isValid(Customer customer, ConstraintValidatorContext context) {
        if (customer == null)
            return false;

        return !((customer.getEmail() == null || customer.getEmail().length() == 0) &&
                 (customer.getPhone() == null || customer.getPhone().length() == 0));
    }
}
```

[CustomerContactsCheckValidator.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator/CustomerContactsCheckValidator.java)

So far, we have seen nothing special, but if we want to get this annotation called and be able to do the cross-field check, we need to specify appropriate [constraint group](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_constraint_groups).

 Editor screens perform validation against class-level constraints on commit if the constraint includes the `UiCrossFieldChecks` group and if all attribute-level checks are passed. You can turn off the validation of this kind using the `crossFieldValidate` property of the screen in the screen XML descriptor or in the controller.

```java
@CustomerContactsCheck(groups = {Default.class, UiCrossFieldChecks.class})
@NamePattern("%s (%s)|name,email")
@Table(name = "ORDERMAN_CUSTOMER")
@Entity(name = "orderman$Customer")
public class Customer extends StandardEntity {
    ...
}
```

[Customer.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Customer.java)

[Top](#content)

### Notes on bean validation

#### At what level bean annotation works

By default, constraint annotations works:

* At UI level when method `validateAll` of the editor's controller is called automatically on the screen commit. _(But you need to override `postValidate` method to do the custom validation in the screen controller, see [later section](#validation-in-ui-screen-controllers))_.
* At REST level when Universal REST endpoints are called.
* At middleware layer when validating method marked with `@Validated` annotation.
* At client or middleware levels when validation is called manually using `BeanValidation` interface.

#### Validation of related objects

For cascade validation of related objects, we need to mark the reference fields with `@Valid`:

```java
public class Order extends StandardEntity {
    ...

    @Size(min = 1, max = 10)
    @Valid
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "order")
    protected List<OrderItem> items;

    ...
}
```

In the example above, when an instance of `Order` is validated, the list of `items` will be checked for the fact that it contains at least one instance (because of `@Size(min = 1)`), and all instances of Product in the list will be validated also.

#### Custom messages in bean validation constraints

All bean validation constraints can have custom messages (see [documentation](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_messages)):

```java
@Pattern(message = "Postal code should follow US ZIP codes format: 12345 or 12345-6789 or 12345 1234", regexp = "^\\d{5}(?:[-\\s]\\d{4})?$")
@NotNull
@Column(name = "POSTAL_CODE", nullable = false, length = 16)
protected String postalCode;
```

Messages can contain parameters and expressions. Parameters are enclosed in `{}` and represent either localized messages or annotation parameters, e.g. `{min}, {max}, {value}`. Expressions are enclosed in `${}` and can include the validated value variable `validatedValue`, annotation parameters like `value` or `min`, and JSR-341 (EL 3.0) expressions:

```java
@Column(name = "EMAIL")
@Email(message = "Invalid email format: ${validatedValue}", regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
protected String email;

@Length(message = "Address line 1 should have length not less than {min}", min = 5)
@NotNull
@Column(name = "ADDRESS_LINE1", nullable = false)
protected String addressLine1;
```

#### Message packs in bean validation constraints

You can also place the message in a [localized messages pack](https://doc.cuba-platform.com/manual-6.9/message_packs.html) and use the following format to specify the message in an annotation: `{msg://message_pack/message_key}` or simply `{msg://message_key}` (for entities only). For example:

```java
@Pattern(regexp = "\\S+@\\S+", message = "{msg://com.company.demo.entity/Customer.email.validationMsg}")
@Column(name = "EMAIL")
protected String email;
```

[Top](#content)

## Validation in REST

### Universal REST

In CUBA application all your entities are available via REST by default. Protocol follows Swagger specification and available here: http://files.cuba-platform.com/swagger/ . This feature is called Universal [REST API](https://doc.cuba-platform.com/manual-6.9/rest_api_v2.html). Constraints defined for entities are applied to universal REST **create** and **update** actions automatically (see [documentation](https://doc.cuba-platform.com/manual-6.9/bean_validation_running.html#bean_validation_in_rest) for details).

The CRUD entities operation (universal REST) are defined [here](http://files.cuba-platform.com/swagger/#/Entities) in swagger format.

![Figure 5: Universal REST swagger specification](resources/swagger_universal_rest.png)

_**Figure 5:** [Universal REST swagger specification](http://files.cuba-platform.com/swagger/#/Entities)_

Following [this steps](rest-commands.md), let's get OAuth2 access token and try to add a new customer using universal REST. To test validation let's make this test customer having couple fields that shouldn't allow it to pass validation checks:

```json
{
    "addressLine1": "Earth",
    "addressLine2": "1240 W Main str, Louisville, KY",
    "addressLine3": "USA",
    "email": "john.smith_invalid_email",
    "name": "John Smith",
    "phone": "+1 (000) 000-1000",
    "postalCode": "40203"
}
```

We can see that email is bad-formed and the phone number doesn't follow the requirements. So, after running the next command _(don't forget to set your own access token as it shown [here](rest-commands.md))._ We are getting expected error message in the format described in the [later section](#validation-errors-in-rest):

```bash
$ http --json POST localhost:8080/app/rest/v2/entities/orderman\$Customer 'Authorization:Bearer 819fe70b-7881-43ca-98ef-b5749f417f49'  @rest/customer_john_smith.json
HTTP/1.1 400
Content-Type: application/json;charset=UTF-8

[
    {
        "invalidValue": "john.smith_invalid_email",
        "message": "Invalid email format: john.smith_invalid_email",
        "messageTemplate": "Invalid email format: ${validatedValue}",
        "path": "email"
    },
    {
        "invalidValue": "+1 (000) 000-1000",
        "message": "must match \"\\+1\\s\\([2-9](\\d){2}\\)\\s[2-9](\\d){2}-(\\d){4}\"",
        "messageTemplate": "{javax.validation.constraints.Pattern.message}",
        "path": "phone"
    }
]
```

Let's test if our custom validation java class works as well. After making this try we can see that it works just fine:

```bash
$ http --json POST localhost:8080/app/rest/v2/entities/orderman\$Customer 'Authorization:Bearer 819fe70b-7881-43ca-98ef-b5749f417f49'  @rest/customer_mary_smith.json
HTTP/1.1 400
Content-Type: application/json;charset=UTF-8

[
    {
        "invalidValue": null,
        "message": "Either 'name' or 'email' should be defined for a customer",
        "messageTemplate": "{msg://com.haulmont.dyakonoff.orderman.entity.validator/CustomerContactsCheck.message}",
        "path": ""
    }
]
```

[Top](#content)

### REST queries validation

Just for completeness, I need to say that because [predefined JPQL Queries](https://doc.cuba-platform.com/manual-6.9/rest_api_v2_queries_config.html) allow only `SELECT` methods, and so they do only read operations. Hence, they don't provide any methods to validate input parameters. But of course, they screen input data to protect the calls from [SQL injection attack](https://en.wikipedia.org/wiki/SQL_injection).

[Top](#content)

### Validation by contract

Let's make the custom REST service and specify limitations for method parameters and return values, in the way that is somewhat similar to contract programming approach.

We want our service to:

1. List all products in stock.
1. Get a `Stock` object by particular product's name (and throw exception if there are zero or more than one product with such name in stock).
1. Add a new `Product` to `Stock`.
1. Increase amount of existing product in stock.

To do that, let's create a new middleware service using CUBA studio and call it `StockApiService`.

![Figure 6: Adding a middleware service](resources/adding_a_service.png)

_**Figure 6:** Adding a middleware service_

The next step is opening [StockApiService.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/service/StockApiService.java) in IDE and creating appropriate methods:

```java
List<Stock> getProductsInStock();
Stock getStockForProductByName(String productName);
Stock addNewProduct(Product product, BigDecimal inStock, BigDecimal optimalLevel);
Stock increaseQuantityByProductName(String productName, BigDecimal increaseAmount);
```

Next, we need to mark these methods in CUBA studio as REST methods:

![Figure 7: Marking methods as REST available](resources/service_designer.png)

_**Figure 7:** Marking methods as REST available_

Now, let's open `StockApiService` in our Java IDE again and annotate the methods properly.

1. First, we need to know, that constraint validations will be applied **only** to the methods that are marked with `@Validated` annotation. (See [documentation here](https://doc.cuba-platform.com/manual-6.9/bean_validation_running.html#bean_validation_in_services)). By default, `@Validated` uses the next [constraint groups](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_constraint_groups):
    - `Default` and `ServiceParametersChecks` - for method parameters
    - `Default` and `ServiceResultChecks` - for method return value
    - As for [constraint group](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_constraint_groups) `RestApiChecks`, it could be used for those validations that must be checked only when instance is passed to REST-API.
    - It's also possible to mark the whole class / interface with `@Validated` annotation to say CUBA that it needs to run bean validations for all methods and their input parameters and return values.
2. [`@RequiredView` annotation](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_cuba_annotations) could be used to ensure that input parameters of a method have fields that corresponding to the specified [view](https://doc.cuba-platform.com/manual-6.9/views.html).
    - `@RequiredView` validates that the validated Entity has **at least** those fields loaded that are required by the view. It doesn't fire an error if the Entity has extra fields loaded.
    - This annotation works with entity objects and their collections.
3. All standard and custom constrauint validations can be applied either to:
    - **methods** - then the return value is checked.
    - method **parameters** - which makes these parameters to be validated
    - once again, this constraint validations used only if method is marked with `@Validated` annotation.
4. Error messages can be provided with annotations either directly or by using messages packs. (see [documentation](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_messages) for more details)

Here is a result of applying all our validations to the REST service interface:

```java
@Validated
public interface StockApiService {
    String NAME = "orderman_StockApiService";

    @NotNull
    @RequiredView("stock-api-view")
    List<Stock> getProductsInStock();

    @NotNull
    @RequiredView("stock-api-view")
    Stock getStockForProductByName(@NotNull(message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productNameMissing)")
                                   @Length(min = 1, max = 255, message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productName}")
                                           String productName);

    @NotNull
    @RequiredView("_local")
    Stock addNewProduct(@RequiredView("_local")
                                Product product,
                        @NotNull
                        @DecimalMin("0")
                        @DecimalMax(value = "1000", message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.inStockLimit}")
                                BigDecimal inStock,
                        @Min(0)
                                BigDecimal optimalLevel);

    @NotNull
    @RequiredView("stock-api-view")
    Stock increaseQuantityByProductName(@NotNull(message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productNameMissing)")
                                        @Length(min = 1, max = 255, message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productName}")
                                                String productName,
                                        @NotNull
                                        @DecimalMin(value = "0", inclusive = false)
                                        @DecimalMax(value = "1000")
                                                BigDecimal increaseAmount);
}
```

[StockApiService.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/service/StockApiService.java)

This REST interface methods are available at endpoint `/app/rest/v2/services/{serviceName}/{methodName}` as [swagger specification](http://files.cuba-platform.com/swagger/#/Services) says.

Let's run [Postman REST client](https://www.getpostman.com/) and check how our validation annotations works in `addNewProduct` method if `inStock` parameter is greater than 1000:

![Figure 8: Bean validation in REST service](resources/postman_addNewProduct.png)

_**Figure 8:** Bean validation in REST service_

The server returns `400 Bad Request` and an error message like it is specified in the later section [Validation errors in REST](#validation-errors-in-rest).

[Top](#content)

### Programmatic Validation

Sometimes, pure validations are not enough for REST services. To validate the entities passed to the method or validate entities have been created at middleware or client tiers, we may need to run bean validations against these entities manually. This is the case when [programmatic validation](https://doc.cuba-platform.com/manual-6.9/bean_validation_running.html#bean_validation_programmatic) comes to play.

You can perform bean validation  using the `BeanValidation` infrastructure interface, which is available on both middleware and client tiers. It is used to obtain a `javax.validation.Validator` implementation which runs validation and gives the result as a set of `ConstraintViolation` objects.

If you perform some custom programmatic validation in a service, use `CustomValidationException` to inform clients about validation errors in the same format as the standard bean validation does.

The sample application demonstrates this approach in `StockApiServiceBean.addNewProduct` method to validate parameter `product` passed into the method and `stock` object constructed inside it.

```java
@Service(StockApiService.NAME)
public class StockApiServiceBean implements StockApiService {
    ...

    @Override
    public Stock addNewProduct(Product product, BigDecimal inStock, BigDecimal optimalLevel) {
        // validate the product provided
        Validator validator = beanValidation.getValidator();
        Set<ConstraintViolation<Product>> product_violations = validator.validate(product);
        if (product_violations.size() > 0) {
            StringBuilder strBuilder = new StringBuilder();
            product_violations.stream().forEach(violation -> strBuilder.append(violation.getMessage()).append("; "));
            throw new CustomValidationException(strBuilder.toString());
        }

        // check if product already exist in the db
        // we don't check for soft-deleted Product and Stock entities here for simplicity
        // if we'd like to do that might need to load entities and check them with isDeleted() method.
        // see https://doc.cuba-platform.com/manual-6.9/soft_deletion_usage.html for details
        Integer cnt = (Integer) dataManager
                .loadValue("SELECT COUNT(p) FROM orderman$Product p WHERE p.name = :productName", Integer.class)
                .parameter("productName", product.getName())
                .one();
        if (cnt > 0)
            throw new CustomValidationException(messages.formatMainMessage("StockApiService.productExists", product.getName()));

        Product savedProduct = dataManager.commit(product);

        Stock stock = new Stock();
        stock.setInStock(inStock);
        stock.setOptimalStockLevel(optimalLevel);
        stock.setProduct(savedProduct);

        // validate the stock object
        Set<ConstraintViolation<Stock>> stock_violations = validator.validate(stock);
        if (stock_violations.size() > 0) {
            StringBuilder strBuilder = new StringBuilder();
            stock_violations.stream().forEach(violation -> strBuilder.append(violation.getMessage()).append("; "));
            throw new CustomValidationException(strBuilder.toString());
        }

        return dataManager.commit(stock);
    }

    ...
}
```

[StockApiServiceBean.java](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/StockApiServiceBean.java)

[Top](#content)

### Validation errors in REST

Universal [REST API](https://doc.cuba-platform.com/manual-6.9/rest_api_v2.html) automatically performs bean validation for create and update actions. Validation errors are returned to the client in the [following way](https://doc.cuba-platform.com/manual-6.9/bean_validation_running.html#bean_validation_in_rest):

- The `MethodParametersValidationException` and `MethodResultValidationException` exceptions are thrown on validation errors.
- `MethodResultValidationException` and `ValidationException` cause `500 Server error` HTTP status
- `MethodParametersValidationException`, `ConstraintViolationException` and `CustomValidationException` cause `400 Bad request` HTTP status

Response body with `Content-Type: application/json` will contain a list of objects with message, messageTemplate, path and invalidValue properties, for example:

```json
[
    {
        "message": "inStock value is limited to 1000",
        "messageTemplate": "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.inStockLimit}",
        "path": "addNewProduct.arg1",
        "invalidValue": 500000
    }
]
```

- `path` indicates a path to the invalid attribute in the validated object graph.
- `messageTemplate` contains a string which is defined in the message annotation attribute.
- `message` contains an actual value of the validation message.
- `invalidValue` is returned only if its type is one of the followings: `String, Date, Number, Enum, UUID`.

[Top](#content)

## GUI Validator

[CUBA platform](https://www.cuba-platform.com/) offers an UI-level mechanism to verify input data. Let's take a look at [gui validator documentation](https://doc.cuba-platform.com/manual-6.9/gui_validator.html).

_Validation and input type checking should be differentiated. If a given component (e.g. `TextField`) data type is set to anything different than string (this can happen when binding to an entity attribute or setting datatype), then the component will not allow the user to enter a value that does not comply with this data type. When the component loses focus or when the user presses Enter, the component will show the previous correct value._

_On the other hand, validation does not act immediately on data entry or focus loss, but rather when the component’s `validate()` method is invoked. It means that the component (and the entity attribute that it is linked to) may temporarily contain a value, which does not comply with the conditions of validation. It should not be a problem because the validated fields are typically located in edit screens, which automatically invoke validation for all their fields before commit. If the component is located not in an edit screen, its `validate()` method should be invoked explicitly in the screen controller._

In a screen XML-descriptor, a component validator can be defined in a nested `validator` elements. The validator element can have the following attributes:

* **class** − name of a Java class implementing the `Field.Validator` interface. You can use one of the [classes that come out of the box](http://files.cuba-platform.com/javadoc/cuba/6.9/com/haulmont/cuba/gui/components/validators/package-summary.html) or implement your own custom implementation of this interface.
* **script** − path to a Groovy script performing validation. Script could be embedded into a screen's XML-descriptor or be given as a separate groovy file.

Groovy validator scripts and standard classes of Java validators (that are located in the [`com.haulmont.cuba.gui.components.validators`](http://files.cuba-platform.com/javadoc/cuba/6.9/com/haulmont/cuba/gui/components/validators/package-summary.html) package) support `message` attribute − a message displayed to a user when validation fails. The attribute value should contain either a message or a message key from the [messages pack](https://doc.cuba-platform.com/manual-6.9/message_packs.html) of the current screen.

This validation mechanism works only at UI-level (ran on server-side with error messages passed to user's browser) and is called when user submits the form or application calls validation programmatically by calling `AbstractWindow.validateAll()` method or `validate()` method of a component.

Let's look at the examples.

[Top](#content)

### Standard validators

There are set of `Validator` interface implementations that comes out of the box, although many of them repeat annotation-based validators' functionality:

* DateValidator
* DoubleValidator
* EmailValidator
* IntegerValidator
* LongValidator
* PatternValidator
* ScriptValidator
* StringValidator

These validators could be added by hands to the screens' XML-descriptors, just like that:

```xml
<field property="email">
    <validator class="com.haulmont.cuba.gui.components.validators.EmailValidator"/>
</field>
```

[customer-edit.xml](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/customer/customer-edit.xml)

or the same could be done from CUBA studio UI:

![Figure 9: Standard UI validator](resources/standard_ui_validator.png)

_**Figure 9:** Standard UI validator_

A validator class can be assigned to a component not only using a screen XML-descriptor, but also programmatically which is discussed in the next section.

[Top](#content)

### Setting validator programmatically

It's possible to set `Validator` programmatically for a component (in your screen controller, for example), which could be a good option if you need to modify your validation rules on the fly according to some conditions that can be determined only in run-time.

Here is an example of adding `EmailValidator` programmatically in a screen controller which checks that OrderItem quantity is <= 1000. It reads the error message string from the current screen `messages.properties` file and throws `ValidationException` in case of verification fail:

```java
public class OrderItemEdit extends AbstractEditor<OrderItem> {
    ...
    @Named("fieldGroup.quantity")
    private TextField quantityField;

    @Override
    public void init(Map<String, Object> params) {
        quantityField.addValidator(
                new Field.Validator() {
                    @Override
                    public void validate(Object value) throws ValidationException {
                        if (value != null && value instanceof BigDecimal
                                && ((BigDecimal)value).compareTo(new BigDecimal(1000)) > 0) {
                            throw new ValidationException(getMessage("quantityIsTooBig"));
                        }
                    }
                }
        );
        super.init(params);
    }
    ...
}
```

Alternatively, we can do the same thing using lambda-function syntax:

```java
    @Override
    public void init(Map<String, Object> params) {
        super.postInit();
        quantityField.addValidator(
                (Object value) -> {
                    if (value != null && value instanceof BigDecimal && ((BigDecimal)value).compareTo(new BigDecimal(1000)) > 0)
                        throw new ValidationException(getMessage("quantityIsTooBig"));
                });
    }
```

[OrderItem.java](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/orderitem/OrderItemEdit.java)

Another example of adding `Field.Validator` in runtime can be found [here](https://www.cuba-platform.com/discuss/t/how-to-implement-error-display-when-using-custom-validator/2870/6).

[Top](#content)

### Custom Java class validator

Creation of a custom `Field.Validator` is not rocket science as well. If a Java class is used as a validator, it should have a default constructor without parameters or a constructor with the following set of parameters:

* `org.dom4j.Element`, `String` – this constructor will receive the validator XML-element and the message pack name of the screen.
* `org.dom4j.Element` – this constructor will receive the validator XML-element.

If the validator is implemented as an internal class, it should be declared with a static modifier and its name should be separated by "$", for example:
```xml
<validator class="com.sample.sales.gui.AddressEdit$ZipValidator"/>
```

As an exercise let's write a small Java class to validate that product name and description do not contain any swear words. This can be done by creating a class in `gui` module that implements `Field.Validator` interface:

```java
public class ProductNameValidator implements Field.Validator {

    private Logger log = LoggerFactory.getLogger(ProductNameValidator.class);

    protected String message;
    protected String messagesPack;
    protected Messages messages = AppBeans.get(Messages.NAME);

    public static String[] swearWords = {
        ...
    };

    public ProductNameValidator(Element element, String messagesPack) {
        message = element.attributeValue("message");
        this.messagesPack = messagesPack;
    }

    @Override
    public void validate(Object value) throws ValidationException {
        String productName = (String)value;

        for (String swearWord : swearWords) {
            Pattern pat = Pattern.compile(swearWord, Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(productName);
            if (mat.find()) {
                log.warn("Bad word found in a product name: " +  productName);

                String msgFormat = message != null ? messages.getTools().loadString(messagesPack, message) : "Bad word is detected '%s'";
                String wordFound = productName.substring(mat.start(), mat.end());
                String errorMsg = String.format(msgFormat, wordFound);
                throw new ValidationException(errorMsg);
            }
        }
    }
}
```

[ProductNameValidator.java](orderman/modules/gui/src/com/haulmont/dyakonoff/orderman/validation/ProductNameValidator.java)

and then, setting it as a `validator` component in XML-descriptor of the screen for "name" and "description" fields:

```xml
<field property="name">
    <validator class="com.haulmont.dyakonoff.orderman.validation.ProductNameValidator"
                message="msg://badNameInProductName" />
</field>
<field property="description" rows="5">
    <validator class="com.haulmont.dyakonoff.orderman.validation.ProductNameValidator"
                message="msg://badNameInProductDescription" />
</field>
```

[product-edit.xml](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/product/product-edit.xml)

_optionally_, you can provide a message in a screen message pack, as it was done in the example of validating `quantityField`.

[Top](#content)

### Validating with Groovy scripts

Running [groovy](http://groovy-lang.org/) dynamically with [Scripting interface](https://doc.cuba-platform.com/manual-6.9/scripting.html) looks quite appealing from the first glance. What you shall do is to write a small script that has access to a named variable `value` (which represents the UI component value script needs to check) and return boolean check result back to the application.

This script can be specified either from CUBA studio UI:

![Figure 10: Setting up a groovy script for field validation](resources/groovy_validator.png)

_**Figure 10:** Setting up a groovy script for field validation._

or by editing XML screen layout directly:

```XML
<field property="quantity">
    <validator message="Quantity cant'be equal to 666 or 777">return (value &gt; 0 &amp;&amp; value != 666 &amp;&amp; value != 777)</validator>
</field>
```

[order-item-edit.xml](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/orderitem/order-item-edit.xml)

However, this is not the approach I would recommend for complex cases, mainly because of difficulties with groovy script debugging and lack of support from  IDE. If you still chose to try this way I would say that keeping groovy script in a separate file and giving component a reference to it would be better option.

[Top](#content)

## Validation in UI screen controllers

This is a simple and intuitive approach that allows you to perform quite complex checks of a screen data. Here are pros and cons of this way:

**Pros:**

* Easy to implement: you just need to override `postValidate` method in your screen controller.
* It has access to Entity object, screen UI controls, middleware services etc...
* It can do checks of arbitrary complexity.
* It is easy to debug.

**Cons:**

* Acts only on one UI layer, so you'd need to repeat yourself if you have two or more UI modules (web and desktop, for example).
* Can't help with REST calls validation, even with [universal REST](https://doc.cuba-platform.com/manual-6.9/rest_api_v2.html).
* Has difficulties with highlighting fields/components that contains incorrect data. (You'd have to do some CSS/JS magic to achieve that result.)

However, combining this approach with statically and dynamically added `Field.Validator` checks would negate the last flaw.

Let's look at the code:

```java
public class OrderItemEdit extends AbstractEditor<OrderItem> {
    @Inject
    private StockService stockService;

    @Named("fieldGroup.quantity")
    private TextField quantityField;

    ...

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);

        OrderItem item = getItem();

        // check that only POUNDs could have a fractional number quantity
        MeasureUnit unit = item.getProduct().getMeasure();
        if (unit != MeasureUnit.POUND &&
                item.getQuantity().remainder( BigDecimal.ONE ).compareTo(BigDecimal.ZERO) != 0) {
            String msg = "You can't get a fractional number of items measured in " + unit.toString();
            errors.add(msg);
        }

        // Check that Stock has enough Product
        // This is a preliminary check that helps User to get the feedback earlier
        // The final check happens in Order's EntityListener, to be 100% safe from run conditions
        BigDecimal countInStock = stockService.getProductAvailability(item.getProduct());

        if (item.getQuantity().compareTo(countInStock) > 0) {
            String msg = String.format("Insufficient product '%s' in stock (%s left)",
                    item.getProduct().getName(), countInStock.toString());

            errors.add(quantityField, msg);
        }
    }
    ...
}
```

[OrderItemEdit.java](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/orderitem/OrderItemEdit.java)

[Top](#content)

## Using middleware listeners for data validation

The last two validation methods I'm going to discuss in this article are based on [entity listeners](https://doc.cuba-platform.com/manual-6.9/entity_listeners.html) and [transaction listeners](https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html). Which acts on the middle tier.

These listeners are called when [DataManager](https://doc.cuba-platform.com/manual-6.9/dataManager.html) commits the data. However, transaction listener's `beforeCommit()` method is called before transaction commit after all entity listeners if the transaction is not read-only. The method accepts a current `EntityManager` and a collection of entities in the current persistence context. So:

* [**Entity listener**](https://doc.cuba-platform.com/manual-6.9/using_entity_listeners_recipe.html) allows you to execute your business logic each time an entity is added, updated or removed from the database. All it's methods get `Entity` object and `EntityManager`. Hence, entity listener is a good place to make single-field and cross-field checks of the entity.
* [**Transaction listener**](https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html) can be used to enforce complex business rules that involve multiple entities, after all entities in the transaction have been processed by their entity listeners.

Power of this approach is based on the fact that incorrect data would not be able to pass your checks, doesn't matter from where they came.

_**Performance note:** please note that transaction listener is called for **EVERY** transaction commit (if it's not read-only). Counting the fact that transaction listener might have quite complex business logic and even make some extra-calls to the DB, the transaction listener's approach might be quite expensive in terms of the application performance._

To send error messages to a user you can use standard `ValidationException`, or if you want to process these error messages with different [client-level exception handler](https://doc.cuba-platform.com/manual-6.9/exceptionHandlers.html) (see the next [section](#presenting-error-messages-to-a-user) for details), you can define your custom `RuntimeException` class in global module and mark it with [@SupportedByClient](https://doc.cuba-platform.com/manual-6.9/remoteException.html) annotation to have your error messages transported from middleware to client tier.

Both entity and transaction listeners could be easily created from the CUBA studio:

![Figure 11: Creating listeners in CUBA studio](resources/listeners_creation.png)

_**Figure 11:** Creating listeners in CUBA studio_

It allows you to create both interfaces and managed beans for listeners with empty methods implementations.

For entity listeners you can specify what kind of [eight events](https://doc.cuba-platform.com/manual-6.9/entity_listeners.html) you'd like to process. However, for data validation `BeforeInsertEntityListener` and `BeforeUpdateEntityListener` are the two most important.

![Figure 12: Entity listener designer](resources/entity_listener_editor.png)

_**Figure 12:** Entity listener designer_

At this screen the **Use for entities** list typically contains only one entity. The same entity should be specified in the **Entity type** field. However, you may want to specify a `@MappedSuperclass` entity in the **Entity type** field and add its subclasses to the **Use for entities** list.

The `@Listeners` annotation is added to each class specified in the **Use for entities** list. If the **Bean name** is defined for the listener, it is used to refer to the listener in the annotation value. Otherwise, the fully qualified listener class name is used.

```java
@Listeners("orderman_OrderEntityListener")
@NamePattern("%s order#: %s|customer,number")
@Table(name = "ORDERMAN_ORDER")
@Entity(name = "orderman$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = -5542761764517463640L;
    ...
}
```

[Order.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Order.java)

Let's look at the examples.

[Top](#content)

### Single Entity Context example

The demo application uses [`OrderEntityListener`](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/OrderEntityListener.java) to set the unique order serial number and validate that the order price is correct and equals to the sum of it's order items.

```java
/**
 * Sets the serial number for the order and validates that price is correct
 */
@Component("orderman_OrderEntityListener")
public class OrderEntityListener implements BeforeInsertEntityListener<Order>, BeforeUpdateEntityListener<Order> {
    @Inject
    private TimeSource timeSource;

    @Inject
    private UniqueNumbersAPI uniqueNumbersAPI;

    @Override
    public void onBeforeInsert(Order order, EntityManager entityManager) {
        validateOrderPrice(order);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String date = sdf.format(timeSource.currentTimestamp());
        long serialNumb = uniqueNumbersAPI.getNextNumber("order_" + date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String date2 = sdf2.format(timeSource.currentTimestamp());
        order.setNumber(date2 + '-' + Long.toString(serialNumb));
    }

    @Override
    public void onBeforeUpdate(Order order, EntityManager entityManager) {
        validateOrderPrice(order);
    }

    private void validateOrderPrice(Order order) {
        BigDecimal price = order.getPrice();
        for (OrderItem item : order.getItems()) {
            price = price.subtract(item.getSubTotal());
        }
        if (price.compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException("Order price does not match to the total cost of Order Items");
        }
    }
}
```

[OrderEntityListener.java](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/OrderEntityListener.java)

The [`UniqueNumbersAPI`](https://doc.cuba-platform.com/manual-6.9/uniqueNumbers.html) is used to generate sequential integer numbers for every day.

[Top](#content)

### Transactional Context example

This example uses transaction listener's validation. The idea is quite simple: check all changes in `Order` and `OrderItem` objects that are going to be committed, calculate the products' quantities difference and compare these values with what database has in the `ORDERMAN_STOCK` table.

However, this leads to a pretty complex and computationally heavy business logic, which includes a `SELECT` query to the database and may attach new `Stock` objects to the transaction.

```java
@Component("orderman_TransactionListener")
public class TransactionListener implements BeforeCommitTransactionListener {

    @Inject
    private Persistence persistence;

    private Logger log = LoggerFactory.getLogger(TransactionListener.class);

    /**
     * Validates that Stock has enough items for all orders to be committed
     * @see https://doc.cuba-platform.com/manual-6.8/transaction_listeners.html for more examples
     * @param entityManager
     * @param managedEntities
     */
    @Override
    public void beforeCommit(EntityManager entityManager, Collection<Entity> managedEntities) {
        // see https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html for more examples
        Set<Order> ordersToCheck = buildListOfOrdersToCheck(entityManager, managedEntities);
        if (ordersToCheck.size() == 0) return;

        HashMap<UUID, BigDecimal> stockChanges = buildStockChangesSet(ordersToCheck);
        if (stockChanges.size() == 0) return;

        validateStockHasEnoughGoods(stockChanges);
    }

    ...
}
```

[TransactionListener.java](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/TransactionListener.java)

We can see from this example that [transaction listeners](https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html) approach is very powerful, but may be quite complex to implement and could be computationally heavy.

[Top](#content)

## Presenting error messages to a user

As we have seen, three of the methods we have discussed above use exception mechanism to notify client that some field has inappropriate value:

* Field.Validator method
* Transaction listeners
* Entity listeners

In most cases `ValidationException' is thrown to send a message to UI layer.

However, by default CUBA platform doesn't handle this exception in a special way and shows a standard **'Unexpected error'** dialog as it does for any other exception. 

![Figure 13: Error message WITHOUT client-level exception handler](resources/no_client_exception_handler.png)

_**Figure 13:** Error message WITHOUT client-level exception handler_

This dialog is more suited for presenting some kind of system and unexpected errors rather than showing user validation errors that you as a developer kind of expecting to happen from time to time as a part of your application's business logic.

So, it's recommended to implement a [client level exception handler](https://doc.cuba-platform.com/manual-6.9/exceptionHandlers.html) to improve the validation error messages look and feel.

![Figure 14: Error message WITH client-level exception handler](resources/client_exception_handler.png)

_**Figure 14:** Error message WITH client-level exception handler_

The basic client-level exception handler is quite simple. You just need to write [managed bean](https://doc.cuba-platform.com/manual-6.9/managed_beans.html) that extends `AbstractGenericExceptionHandler` in your web (or desktop) module and implements `doHandle` method:

```java
@Component("orderman_ValidationExceptionHandler")
public class ValidationExceptionHandler extends AbstractGenericExceptionHandler {

    public ValidationExceptionHandler() {
        super(ValidationException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(message, Frame.NotificationType.WARNING);
    }
}
```

[ValidationExceptionHandler.java](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/handler/ValidationExceptionHandler.java)

Additional examples for cases that do not cover common `ValidationException` handling needs could be found in [documentation](https://doc.cuba-platform.com/manual-6.9/exceptionHandlers.html).

[Top](#content)

## Summary

We have covered most of the mechanisms that [CUBA platform](https://www.cuba-platform.com/) offers for input data validation. Let's group them by

_**Table 1:** Validation levels_

|                                   | Generic UI | Universal REST | Middleware | DataStore | Transaction | DB server |
| --------------------------------- | :--------: | :------------: | :--------: | :-------: | :---------: | :-------: |
| _DB level JPA constraints \*_     |            |                |            |           |             | yes       |
| _@NotNull constraint \*\*_        | yes        | yes\*\*\*      | yes        |           |             | yes       |
| _Bean validation_                 | yes        | yes\*\*\*      | yes        |           |             |           |
| _UI validation (Field.Validator)_ | yes        |                |            |           |             |           |
| _Custom Field.Validator_          | yes        |                |            |           |             |           |
| _Screen controllers validation_   | yes        |                |            |           |             |           |
| _Entity listeners_                |            |                |            | yes       |             |           |
| _Transaction listeners_           |            |                |            |           | yes         |           |

\* - `@Table` and `@Column` annotations<br />
\*\* - `@NotNull` that is accompanied with `@Column(nullable = false)`<br />
\*\*\* - only for fields marked with `@Validated` annotations

You can read more about CUBA application tiers and blocks [here](https://doc.cuba-platform.com/manual-6.9/app_tiers.html).

_**Table 2:** Validation implementation complexity_

|                                            | Elementary | Simple | Average | Complex |
| ------------------------------------------ | :--------: | :----: | :-----: | :-----: |
| _DB level JPA constraints_                 | yes        |        |         |         |
| _Bean validation_                          | yes        |        |         |         |
| _Bean validation (custom annotations)_     |            |        | yes     |         |
| _UI validation (standard Field.Validator)_ | yes        |        |         |         |
| _Custom Field.Validator (Java class)_      |            |        | yes     |         |
| _Custom Field.Validator (Groovy script)_   |            | yes    |         |         |
| _Screen controllers validation_            |            | yes    |         |         |
| _Entity listeners_                         |            |        | yes     |         |
| _Transaction listeners_                    |            |        |         | yes     |

_**Table 3:** Validation scope_

|                                            | Single Field | Cross Field | DataStore | Transaction Context |
| ------------------------------------------ | :----------: | :---------: | :-------: | :-----------------: |
| _DB level JPA constraints_                 | yes          | yes\*       |           |                     |
| _Bean validation_                          | yes          |             |           |                     |
| _Bean validation (custom annotations)_     | yes          | yes         |           |                     |
| _UI validation (standard Field.Validator)_ | yes          |             |           |                     |
| _Custom Field.Validator (Java class)_      | yes          |             |           |                     |
| _Custom Field.Validator (Groovy script)_   | yes          |             |           |                     |
| _Screen controllers validation_            | yes          | yes         |           |                     |
| _Entity listeners_                         | yes          | yes         | yes       |                     |
| _Transaction listeners_                    | yes          | yes         | yes       | yes                 |

\* - multi-column unique constraints (`@UniqueConstraint`)

1. **Bean validation** could use standard and custom annotations. It works on all tiers and offers the best level of data security. Besides that it is reusable and gives good UI feedback. The limitations of this approach are:
    1. It can't be used for validating the whole data graph when you need to check state of more than one entity.
    2. Business logic at middleware level can change the entities directly and they will not be validated by this mechanism even before saving them to the DB.
2. Defining custom **`Validator`** class and groovy scripts for UI components. Since it works at UI level only, this mechanism offers nice UI integration support (highlighting and pretty error messages formatting) but drawbacks are the same as for annotations, plus:
    1. It won't be able to check Universal REST calls.
    2. Groovy scripts are hard to debug.
3. **Validation in UI screen controllers** approach is the simplest way to do validation if there are no standard annotations or `Validator` classes to do it. However, you get the checks done only at UI level and the code reusability is not at the best level.
4. **Entity and Transaction listeners** give the best security level. Transaction listeners are capable to check the whole object graph. But this approach requires more coding, has not that good UI integration and doing data validation right before commit which might lead to inability to fix the data because it could be too late to do something with the error. Also, complex transaction listeners can degrade the system performance as they happen for **every** data commit.

[Top](#content)

## Appendix A

### CUBA Documentation articles related to validation

1. [Bean Validation](https://doc.cuba-platform.com/manual-6.9/bean_validation.html)
1. [List of annotation-based constraints in CUBA applications](common_constraints_annotations.md)
1. ["Using entity listeners" recipe](https://doc.cuba-platform.com/manual-6.9/using_entity_listeners_recipe.html)

### Other reading

1. A previous version of this article that uses different samples approach: one simple example per validation method. It contains couple extra examples and can be found [here](https://github.com/dyakonoff/cuba-validation-examples/tree/version-1).
2. There is a good article ["Improvements in CUBAs REST API v2"](https://www.road-to-cuba-and-beyond.com/improvements-in-cubas-rest-api-v2/) by Mario David if you'd like to read more about REST in CUBA.
3. Mario also wrote a great article ["Security constraints in CUBA"](https://www.road-to-cuba-and-beyond.com/security-constraints-in-cuba/) that could help you to learn how to use constraints and validations to implement access/security rules in the CUBA app.

[Top](#content)
