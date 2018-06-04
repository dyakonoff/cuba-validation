# Data validation in [CUBA platform](https://www.cuba-platform.com/) applications

## Content

1. [Introduction](#introduction)
1. [Model problem description](#model-problem-description) / _[full description](order-management.md)_
1. [Sample application](#sample-application)
1. [Validation with JPA annotations](#validation-with-jpa-annotations)
    1. [JPA DB level constraints](#jpa-db-level-constraints)
    1. [Single-field constraints](#single-field-constraints)
    1. [Bean validation with custom annotations](#bean-validation-with-custom-annotations)
    1. [Generic REST Validation](#generic-rest-validation)
    1. [Validation by contract](#validation-by-contrect)
    1. [Notes on JPA validation](#jpa-validation-for-entities)
1. [GUI Validator](#gui-validator)
    1. [Standard validators](#standard-validators)
    1. [Setting validator programmatically](#setting-validator-programmatically)
    1. [Custom Java class validator](#custom-java-class-validator)
    1. [Validating with Groovy scripts](#validating-with-groovy-scripts)
1. [Validation in UI screen controllers](#validation-in-ui-screen-controllers)
1. [Using Entity and Transaction listeners for validation](#using-middleware-listeners-for-data-validation)
    1. [Single Entity Context example](#single-entity-context-example)
    1. [Transactional Context example](#transactional-context-example)
1. [Presenting error messages to a user](#presenting-error-messages-to-a-user)
1. [Summary](#summary)
1. [Appendix A](#appendix_a)

## Introduction

Input validation is one of common tasks in everyday developer’s life. We need to check our data in many different situations: after getting data from UI, from API calls, before saving our model to the DB etc, etc.

This article's goal is to summarize all validation methods common to [CUBA platform](https://www.cuba-platform.com/) present explanations and examples for all of them and talk about pros and cons of each of these methods. I hope that the article will be a good tutorial and reference for all questions related to data validation in [CUBA platform](https://www.cuba-platform.com/) based applications.

The sample application for this article could be downloaded from [here](https://github.com/dyakonoff/cuba-validation-examples). A list of additional examples and materials for further reading is in [Appendix A](#appendix_a).

![Mad scientist validates stuff](resources/mad_scientist.png)

[Top](#content)

## Model problem description

Before discussing different validation methods available for us in [CUBA platform](https://www.cuba-platform.com/) application, let's discuss what it does first.

For this article I'm going to use one application that includes all validation methods mentioned here to demonstrate how they work from different perspectives: code, user interface, CUBA studio IDE, REST endpoints etc.

This application mimics a small order-management system for an US-based store that ships it's goods over the US. That's why this system uses imperial measurement units and enforces US phone numbers and ZIP codes formats.

The application's entities structure is shown below:

![Figure 1: Entities structure](resources/database_scheme_sm.png)

_**Figure 1:** Entities structure._

The full description of the application's requirements and data constraints could be found [here](order-management.md).

List of implemented validation methods with links is [here](validations-index.md).

[Top](#content)

## Sample application

The sample application's code is [here](https://github.com/dyakonoff/cuba-validation-examples/orderman). I encourage you to download the sample project archive from [here](https://github.com/dyakonoff/cuba-validation-examples/archive/master.zip) or clone the project with the next commend:

```bash
$ git clone git@github.com:dyakonoff/cuba-validation-examples.git
```

[Top](#content)

## Validation with JPA annotations

Let's start the review of validators with the simplest ones that we have in our toolbox: JPA constraints. Annotation-based validators provide uniform approach to data checking on the middleware, [GUI](https://doc.cuba-platform.com/manual-6.9/gui_framework.html) and [REST services](https://doc.cuba-platform.com/manual-6.9/rest_api_v2.html). They are based on the JSR 349 - Bean Validation 1.1 and its reference implementation: [Hibernate Validator](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/?v=5.3).

[Documentation](https://doc.cuba-platform.com/manual-6.9/bean_validation.html) says that this mechanism allows users to set limitations on entity fields, getters and classes. Most of the annotations are available from [`javax.validation.constraints` namespace](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html), although couple come from `javax.persistence`, `javax.validation` and `org.hibernate.validator.constraints`.

Also, it's not hard to create your own annotations to validate fields and entities, which we'll se in the [later sections]().

[Top](#content)

### JPA DB level constraints

In [CUBA platform]() some of JPA annotation put constraints on DB level as table indexes or table column / multi-column constraints.
Although there are only few of such annotations, they are only only one which acts on DB server level.

* `@Column(..., unique=true)` - sets SQL `unique` constraint on a table column for entity fields marked as **Unique**
* `@Column(..., nullable=false)` - for fields marked in studio like **Mandatory** sets SQL `not null` constraint on table column (entity field). Acts together with `@NotNull` JPA annotation which works at UI and middleware level.
* `@Column(..., length = 16)` - sets a length of `varchar` column, and limits the maximum input field length at UI level.
* `@UniqueConstraint` - sets a multi column index with `unique` constraint.

[Top](#content)

#### Single column constraints

Single column constraints could be applied from CUBA studio entity editor's UI during entity field creation or modification:

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

Note that CUBA studio automatically adds `@NotNull` constraint to the field marked as **Mandatory** in Entity designer. Hence, this validations is made on UI and middleware level as well.

On the other hand, **Unique** constraint (reflected with `@Column(..., unique=true)` annotation) works only on DB level, because it can not be validated on the application level without reading whole SQL table which might be very expensive operation.

[Top](#content)

#### Multi-column constraints

This type of data constraint / validation acts on DB level only and is represented by multi-column index with unique constraint. It could be created from Entity designer in CUBA studio:

![Figure 3: Creating multi-column unique constraint](resources/unique_index_editor.png)

_**Figure 3:** Creating multi-column unique constraint._

Or right in Java code:

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

Which is reflected as `create unique index IDX_ORDERMAN_PRODUCT_UNQ on ORDERMAN_PRODUCT (NAME, MEASURE)` in generated DDL.

[Top](#content)

### Single field constraints

Let's go through other standard JPA annotations that were designed for data validation. As I mentioned before most of them came from [`javax.validation.constraints` namespace](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html), although couple are from `javax.persistence`, `javax.validation` and `org.hibernate.validator.constraints`. You can take a look at [CUBA documentation section about bean validation](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html).

Some of these annotations can be configured from CUBA studio in VALIDATION section (list of constraints available varies for different entity field's types).

![Figure 4: JPA validation tab](resources/jpa_validation_tab.png)

_**Figure 4:** JPA validation tab._

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
    @Past(message = "Order date can't be in the futire")
    @NotNull
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @Pattern(message = "Number format should be yyyy-MM-dd-<sequentional number>", regexp = "\\d{4}-\\d{2}-\\d{2}-\\d+")
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

[Here](comon_jpa_annotations.md) you can find a list of common JPA annotations used in CUBA applications.

[Top](#content)

### Bean validation with custom annotations

There is no need to limit ourselves with the standard JPA annotations, if it's needed we can define our custom ones.
[Custom annotations](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_custom_constraints) could be defined not just for entities fields but also for Entity classes, POJOs and service methods. Custom annotations can help you to express your validation logic clearer or implement custom checks that can even do the cross-field checks of an entity object.

Let’s check out how to do that. In our sample application there were implemented [two custom constraints](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator):

* `@UsPhoneNumber` - a constraint which is [implemented](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/validator/UsPhoneNumber.java) as a descendant of `@Pattern` annotation and does the simple regexp check for the phone format. 
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

* `@Constraint(validatedBy = {})` says that there is no custom class implementing the validator.
* `@Retention(RetentionPolicy.RUNTIME)` marks annotation as a runtime one, which all validators should be.
* `@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })` specifies targets that this annotation can be applied to. Althouth in our case, it would be possible to limit the scope only with `ElementType.FIELD`.
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

More complex example of a custom validator that uses a list of checks to validate data is an [org.hibernate.validator.constraints.br.CPF](https://github.com/hibernate/hibernate-validator/blob/master/engine/src/main/java/org/hibernate/validator/constraints/br/CPF.java).

Now, it's time to take a look at more complex custom annotation that does cross-field check. `@CustomerContactsCheck` interface defines the constraint that could be applied to classes only:

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

and has the next implementation:

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

So far, nothing special, but if we want to get this annotation called and be able to do the cross-field check, we need to specify appropriate [constraint group.](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_constraint_groups)

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

### Generic REST Validation

CUBA by default makes all your entities available via REST protocol which follows Swagger specification and available at following URL: http://files.cuba-platform.com/swagger/ . This feature is called [generic REST endpoints](https://doc.cuba-platform.com/manual-6.5/rest_api_v2.html) and by default JPA annotations is applied to REST calls es well.

**IN PROGRESS**
[Top](#content)






### JPA validation for entities

**IN PROGRESS**
[Top](#content)






### Validation by contract

**IN PROGRESS**
[Validation in middleware services](https://doc.cuba-platform.com/manual-6.9/bean_validation_running.html#bean_validation_in_services)
[@RestrictedView](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_cuba_annotations)






[Top](#content)


### Notes on JPA validation

TODO: 

By default, JPA annotations works:

* AT UI level when method `validateAll` of the editor's controller is called automatically on the screen commit. _(But you need to override `postValidate` method to do the custom validation in the screen controller, see later sections)_.
* At REST level when Generic REST endpoints are called.
* At middleware layer when validation is called manually.

**IN PROGRESS**


[Top](#content)










## GUI Validator

[CUBA platform](https://www.cuba-platform.com/) offers an UI-level mechanism to validate input data. Let's take a look at [gui validator documentation](https://doc.cuba-platform.com/manual-6.9/gui_validator.html). In a screen XML-descriptor, a component validator can be defined in a nested validator elements. The validator element can have the following attributes:

* **class** − name of a Java class implementing the Field.Validator interface. You can use one of the [classes that come out of the box](http://files.cuba-platform.com/javadoc/cuba/6.9/com/haulmont/cuba/gui/components/validators/package-summary.html) or implement your own custom implementation of `Field.Validator` interface. 
* **script** − path to a Groovy script performing validation. Script could be embedded into a screen's XML-descriptor or be given as a separate groovy file.

Groovy validator scripts and standard classes of Java validators, located in the [`com.haulmont.cuba.gui.components.validators`](http://files.cuba-platform.com/javadoc/cuba/6.9/com/haulmont/cuba/gui/components/validators/package-summary.html) package support message attribute − a message displayed to a user when validation fails. The attribute value should contain either a message or a message key from the [messages pack](https://doc.cuba-platform.com/manual-6.9/message_packs.html) of the current screen.

This validation mechanism works only at UI-level (ran on server-side with error messages passed to user's browser) and is called when user submits the form or application calls validation programmatically by calling `AbstractWindow.validateAll()` method or `validate()` method of a component.

Let's look at the examples.

[Top](#content)

### Standard validators

There are set of `Validator` interface implementations that comes out of the box, although many of them repeat JPA annotation-based validators' functionality:

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

or, using CUBA studio UI, which will give exactly the same result:

![Figure XX: Standard UI validator](resources/standard_ui_validator.png)

_**Figure XX:** Standard UI validator_

A validator class can be assigned to a component not only using a screen XML-descriptor, but also programmatically – by submitting a validator instance into the component’s `addValidator()` method.

[Top](#content)

### Setting validator programmatically

It's also possible to set `Validator` programmatically for a component (in your screen controller, for example), which could be a good option if you need to modify your validation rules on the fly according to some conditions that can be determined only in run-time. Here is an example of adding `EmailValidator` programmatically in a screen controller which checks that OrderItem quantity is <= 1000 and if it is not reads the error message string from the current screen `messages.properties` file and throws `ValidationException`:

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

Or we can do the same thing using lambda-function syntax:

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

Creation of a custom `Field.Validator` is not rocket science as well. If a Java class is being used as a validator, it should have a default constructor without parameters or a constructor with the following set of parameters:

* `org.dom4j.Element`, `String` – this constructor will receive the validator XML-element and the message pack name of the screen.
* `org.dom4j.Element` – this constructor will receive the validator XML-element.

If the validator is implemented as an internal class, it should be declared with a static modifier and its name should be separated by "$", for example: ```<validator class="com.sample.sales.gui.AddressEdit$ZipValidator"/>s```

As an exercise let's write a small java class to validate that product and description do not contain swear words. This can be done by creating a class in `gui` module that implements `Field.Validator` interface:

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

And setting it as a `validator` component in XML-descriptor of the screen for "name" and "description" fields:

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

![Figure XX: Setting up a groovy script for field validation](resources/standard_ui_validator.png)

_**Figure XX:** Setting up a groovy script for field validation._

or by editing XML screen layout directly:

```XML
<field property="quantity">
    <validator message="Quantity cant'be equal to 666 or 777">return (value &gt; 0 &amp;&amp; value != 666 &amp;&amp; value != 777)</validator>
</field>
```

[order-item-edit.xml](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/orderitem/order-item-edit.xml)

However, this is not the approach I would recommend for complex cases, mainly because of difficulties with groovy script debugging and lack of support from an IDE. If you still chose to try that way I would say that keeping groovy script in a separate file and giving component reference to it would be a better option.

[Top](#content)

## Validation in UI screen controllers

This is a simple and intuitive approach that wold allow you to perform quite complex checks of your screen data. Here are pros and cons of this way:

**Pros:**

* Easy to implement: you just need to override `postValidate` method in your screen controller.
* Has access to Entity object, screen UI controls, middleware services etc...
* Can do checks of arbitrary complexity.
* Easy to debug.

**Cons:**

* Acts only on one UI layer, so you'd need to repeat yourself if you have two or more UI modules (web and desktop, for example).
* Can't help with REST calls validation, even with [Generic REST](https://doc.cuba-platform.com/manual-6.9/rest_api_v2.html).
* Has difficulties with highlighting fields/components that contains incorrect data. (You'd have to do some CSS/JS magic to achieve that result.)

However, combining this approach with static and dynamically added `Field.Validator` checks would negate the last flaw.

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

These listeners are called when [DataManager](https://doc.cuba-platform.com/manual-6.9/dataManager.html) commits the data. However, transaction listener's `beforeCommit()` method is called before transaction commit after all entity listeners if the transaction is not read-only. The method accepts a current EntityManager and a collection of entities in the current persistence context. So,

* [**Entity listener**](https://doc.cuba-platform.com/manual-6.9/using_entity_listeners_recipe.html) allows you to execute your business logic each time an entity is added, updated or removed from the database. All it's methods get `Entity` object and `EntityManager`. Hence, entity listener is a good place to make the entity's field values single and cross-field checks.
* [**Transaction listener**](https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html) can be used to enforce complex business rules involving multiple entities, when all entities in the transaction are were processed by their entity listeners.

Power of this approach is based on the fact that incorrect data would not be able to pass your checks, doesn't matter from where they came.

**Performance note:** please note that transaction listener is called for *ECERY* transaction commit (if it's not read-only), including the fact that transaction listener might have quite complex business logic and even make some extra-calls you'd have to program to make the validation logic possible. The transaction listener's approach might be quite expensive in terms of the application productivity.

For sending error messages to a user you can use standard `ValidationException`, or if you want to process these error messages with different [client-level exception handler](https://doc.cuba-platform.com/manual-6.9/exceptionHandlers.html) (see the next [section](#presenting-error-messages-to-a-user) for details), you can define your custom `RuntimeException` class in global module and mark it with [@SupportedByClient](https://doc.cuba-platform.com/manual-6.9/remoteException.html) annotation to have your error messages transporting from middleware to client tier.

Both entity and transaction listeners could be easily created from the CUBA studio:

![Figure XX: Creating listeners in CUBA studio](resources/listeners_creation.png)

_**Figure XX:** Creating listeners in CUBA studio_

Which allows you to create both interfaces and managed beans for listeners with empty methods implementations.

For entity listeners you can specify what kind of [eight events](https://doc.cuba-platform.com/manual-6.9/entity_listeners.html) you'd like to process. However, for data validation `BeforeInsertEntityListener` and `BeforeUpdateEntityListener` are the two most important.

![Figure XX: Entity listener designer](resources/entity_listener_editor.png)

_**Figure XX:** Entity listener designer_

The **Use for entities** list typically contains only one entity. The same entity should be specified in the Entity type field. However, you may want to specify a `@MappedSuperclass` entity in the Entity type field and add its subclasses to the Use for entities list.

The `@Listeners` annotation is added to each class specified in the Use for entities list. If the Bean name is defined for the listener, it is used to refer to the listener in the annotation value. Otherwise, the fully qualified listener class name is used.

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

Here [`UniqueNumbersAPI`](https://doc.cuba-platform.com/manual-6.9/uniqueNumbers.html) is used to generate sequential integer numbers for every day.

[Top](#content)

### Transactional Context example

Transaction listener validation's idea is quite simple: check all changes in orders and order items that are going to be committed, calculate the products' quantities difference and compare these values with what does database has in `ORDERMAN_STOCK` table.

However, this leads to the pretty complex and computationally heavy business logic, which includes a SELECT query to the database and may even attach new Stock objects to the transaction. (see [TransactionListener.java](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/TransactionListener.java) for full details)

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

So, as we can see from this example, the [transaction listeners](https://doc.cuba-platform.com/manual-6.9/transaction_listeners.html) approach is very powerful, but may quite complex to implement and could be computationally heavy.

[Top](#content)

## Presenting error messages to a user

As we have seen above, three of the methods we have discussed use exception mechanism to notify client that some field has inappropriate value:

* Field.Validator method
* Transaction listeners
* Entity listeners

In most cases `ValidationException' is thrown to send a message to UI layer.

However, by default, CUBA platform doesn't handle this exception in a special way and shows a standard 'Unexpected error' dialog as for any other exception. This dialog is more suited for presenting some kind of system and unexpected errors rather than showing user validation errors that you as a developer kind of expecting to happen from time to time as a part of your application's business logic.

So, it's recommended to implement a [client level exception handler](https://doc.cuba-platform.com/manual-6.9/exceptionHandlers.html) 
doesn't look like a good method 

![Figure XX: Error message WITHOUT client-level exception handler](resources/no_client_exception_handler.png)

_**Figure XX:** Error message WITHOUT client-level exception handler_

![Figure XX: Error message WITH client-level exception handler](resources/client_exception_handler.png)

_**Figure XX:** Error message WITH client-level exception handler_

The basic client-level exception handler is quite simple. You just need to make [managed bean](https://doc.cuba-platform.com/manual-6.9/managed_beans.html) that implements `AbstractGenericExceptionHandler interface` in your web (or desktop)module and implement `doHandle` method:

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

|                                   | Generic UI | Generic REST | Middleware | DataStore | Transaction | DB server |
|-----------------------------------|:----------:|:------------:|:----------:|:---------:|:-----------:|:---------:|
| _DB level JPA constraints \*_     |            |              |            |           |             |    yes    |
| _@NotNull constraint \*\*_        |     yes    |   yes\*\*\*  |    yes     |           |             |    yes    |
| _Bean validation (annotations)_   |     yes    |   yes\*\*\*  |    yes     |           |             |           |
| _UI validation (Field.Validator)_ |     yes    |              |            |           |             |           |
| _Custom Field.Validator_          |     yes    |              |            |           |             |           |
| _Screen controllers validation_   |     yes    |              |            |           |             |           |
| _Entity listeners_                |            |              |            |    yes    |             |           |
| _Transaction listeners_           |            |              |            |           |     yes     |           |

\* - `@Table` and `@Column` annotations, see example: [Product.java](validation-with-custom-annotations/modules/global/src/io/dyakonoff/validationannotations/entity/Product.java)<br />
\*\* - `@NotNull` that is accompanied with `@Column(nullable = false)`<br />
\*\*\* - only for fields marked with `@Validated` annotations

You can read more about CUBA application tiers and blocks [here](https://doc.cuba-platform.com/manual-6.9/app_tiers.html).

_**Table 2:** Validation implementation complexity_

|                                             | Elementary | Simple | Average | Complex  |
|---------------------------------------------|:----------:|:------:|:-------:|:--------:|
| _DB level JPA constraints_                  |    yes     |        |         |          |
| _Bean validation (JPA annotations)_         |    yes     |        |         |          |
| _Bean validation (custom annotations)_      |            |        |   yes   |          |
| _UI validation (standard Field.Validator)_  |    yes     |        |         |          |
| _Custom Field.Validator (Java class)_       |            |        |   yes   |          |
| _Custom Field.Validator (Groovy script)_    |            |  yes   |         |          |
| _Screen controllers validation_             |            |  yes   |         |          |
| _Entity listeners_                          |            |        |  yes    |          |
| _Transaction listeners_                     |            |        |         |   yes    |

_**Table 3:** Validation scope_

|                                            | Single Field | Cross Field | DataStore | Transaction Context|
|--------------------------------------------|:------------:|:-----------:|:---------:|:------------------:|
| _DB level JPA constraints_                 |     yes      |   yes\*     |           |                    |
| _Bean validation (JPA annotations)_        |     yes      |             |           |                    |
| _Bean validation (custom annotations)_     |     yes      |    yes      |           |                    |
| _UI validation (standard Field.Validator)_ |     yes      |             |           |                    |
| _Custom Field.Validator (Java class)_      |     yes      |             |           |                    |
| _Custom Field.Validator (Groovy script)_   |     yes      |             |           |                    |
| _Screen controllers validation_            |     yes      |    yes      |           |                    |
| _Entity listeners_                         |     yes      |    yes      |    yes    |                    |
| _Transaction listeners_                    |     yes      |    yes      |    yes    |         yes        |

\* - for multi-column unique constraints (`@UniqueConstraint`), see example: [Product.java](orderman/modules/global/src/com/haulmont/dyakonoff/orderman/entity/Product.java)

1. Bean validation could use standard and custom annotations. It works on all tiers, so offers the best level of data security. Besides that it is reusable and gives good UI feedback to a user. The limitations of that approach:
    1. It can't be used for validating the whole data graph when you need to check state of more than one entity.
    1. Business logic at middleware level can change the entities directly and they will not be validated by this mechanism even before saving them to DB.
1. Defining custom Validator class and groovy scripts for UI components. Since it works at UI level only, this mechanism offers nice UI integration support (highlighting and pretty error messages formatting) but drawbacks are the same as for annotations, plus:
    1. It won't be able to check Generic REST calls.
    1. Groovy scripts are hard to debug.
1. Validation in UI screen controllers - it is the simplest way to do validation if there are no standard annotations or `Validator` classes to do it. However, you get the checks done only at UI level and the code reusability is not at the best level.
1. Entity and Transaction listeners give the best security level. Transaction listeners are capable to check the whole object graph. But this approach requires more coding, has not that good UI integration and doing data validation right before commit might lead to inability to fix the data because it could be too late to do something with data error. Also, complex listeners can degrade the system performance as they happen for **every** data commit.

[Top](#content)

## Appendix A

There is a old version of this article that used different samples approach: one simple example per validation method. And which contains couple of small examples. It can be found [here](https://github.com/dyakonoff/cuba-validation-examples/tree/version-1).

CUBA Documentation articles, related to validation:

1. [Bean Validation](https://doc.cuba-platform.com/manual-6.9/bean_validation.html)
1. [List of JPA constraints in CUBA applications](common_jpa_annotations.md)
1. ["Using entity listeners" recipe](https://doc.cuba-platform.com/manual-6.9/using_entity_listeners_recipe.html)

[Top](#content)
