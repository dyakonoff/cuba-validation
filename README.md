# Input data validation in [CUBA platform](https://www.cuba-platform.com/)

## Introduction

Input validation is one of common tasks in everyday developer’s life. We need to check our data in many different situations: after getting data from UI, from API calls, before saving your model to the DB etc, etc.

In this article I want to touch the main approaches of data validation that [CUBA platform](https://www.cuba-platform.com/) offers.

Here are the approaches we will discuss:
1. [Model problem description](#model-problem-description)
1. [JPA DB level constraints](#jpa-db-level-constraints) / [Example]()
    * [Single-field constraints](#single-field-constraints)
    * [Multi-column indexes](#multi-column-indexes)
1. [JPA validation for entities](#jpa-validation-for-entities) / _[Example](simple-validation/)_
    * [Generic REST Validation](#generic-rest-validation)
1. [Bean validation with custom annotations](#bean-validation-with-custom-annotations) / _[Example 1](validation-with-custom-annotations/), [Example 2](https://github.com/cuba-platform/sample-user-registration/tree/master/modules/global/src/com/company/sample)_
1. [Defining custom Validator class and groovy validation scripts for UI components](#custom-validator-classes-and-scripts) / _[Example](validator-component/)_
1. [Validation in UI screen controllers](#validation-in-ui-screen-controllers)  / _[Example](validation-in-controllers/)_
1. [Using Entity and Transaction listeners for validation](#using-middleware-listeners-for-data-validation) / _[Example](listeners-validation)_
    * [Single Entity Context](#single-entity-context)
    * [Transaction Context](#transaction-context)
1. [Conclusion](#conclusion)

## Model problem description

Before discussing different validation methods available in [CUBA platform](https://www.cuba-platform.com/) let's settle down the model problem that our code example will be solving.

**Note:** If you want to check out code examples and technical details you can skip this section.

As the matter of fact, I'm going to use an example application that includes all validation methods mentioned in this article to demonstrate how they work from different perspectives: code, user interface, CUBA studio IDE, REST endpoints etc.

However, before starting coding exercises, let's discuss the problem, we are going to model with our software.

As you probably know, most of world trade are made with [containers ships](https://en.wikipedia.org/wiki/Container_ship) like this:<br />
_(all ships and containers images for the article were taken from Wikipedia)_

![APL Post-Panamax container ships Image ID: line0534, America's Coastlines. Collection Location: San Francisco.](resources/ship_and_holds.jpg)

These giant vessels can carry thousands of ISO-containers that can be [different types](https://en.wikipedia.org/wiki/Intermodal_container) but most of the vessels can carry only 20 ft, 40 ft and 45 ft containers of standard end extra height (extra height are called High Cube).

Container ships are divided into set of holds, each of them can hold 20 ft and 40 ft containers below deck level and 20, 40 and 45 ft containers above deck level.

For our task we'll implement a small cargo management system that will keep records of containers that the vessel carries.

Database schema of our application looks like that:



### Validation rules

1. Only 20, 40 and 45 containers are allowed on a ship.
1. 45 ft containers are allowed only above deck level.
1. Only 20 and 40 ft standard height containers can be loaded below deck level.
1. If hold has 20 ft containers in it, it should be even number of them.
1. There should not be more than one high-cube container in any container stack.
1. No container is allowed to have weight greater than allowed for it's type.
1. Each container should have a serial number.
1. This serial number follows [ISO-6346](https://en.wikipedia.org/wiki/ISO_6346) standard.
1. Each hold has maximum allowed number of levels above deck that should not be exceeded.
1. Cargo should be balanced, in other words it's center of mass should not be further than 10% of the ship's length to the aft or bow side from it's center.
1. Similarly, cargo's mass center should not be further than 10% of ship's width from it's centerline.
1. Total weight of containers loaded onto ship should not exceed it's net register tonnage (maximum cargo weight allowed for this ship).
1. Containers are stacked on each other without gaps.
1. For one cargo hold height of neighbour container's stacks should not be bigger than 2.


**Note:** For simplicity we are not calculating ship's balance in bottom-to-top directions.


[Top](#introduction)

## [JPA DB level constraints](validation-with-custom-annotations/)
[JPA constraints]() that
Let's start this article with a look at one of the most common constraints that [CUBA platform](https://www.cuba-platform.com/) offers. For any field of the entity you can say that this field is unique or mandatory. This can be done either during the entity field creation or after that with entity editor.

These two checkboxes alongside with `@NotNull` constraint that you can set for entities (see [figure 2](resources/figure_2.png) ) generate both annotations and DDL (SQL) constraints.

[Top](#introduction)

### Single-field constraints

![Figure 1: Database-level constraints at field creation dialog <](resources/figure_1.png)<br />
_**Figure 1:** Database-level constraints at field creation dialog_

![Figure 2: Database-level constraints in field editor <](resources/figure_2.png)<br />
_**Figure 2:** Database-level constraints in field editor_

[Top](#introduction)

### Multi-column indexes

![Figure 3: Multi-column index on an entity](resources/figure_3.png)<br />
_**Figure 3:** Multi-column index for an entity_

```java
...
@NamePattern("%s|name")
@Table(name = "VALIDATIONANNOTATIONS_PRODUCT", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_VALIDATIONANNOTATIONS_PRODUCT_UNQ", columnNames = {"NAME", "PRICE_PER_MEASURE", "MEASURE"})
})
@Entity(name = "validationannotations$Product")
public class Product extends StandardEntity {
  private static final long serialVersionUID = 1561721020865033907L;

  @NotNull
  @Column(name = "NAME", nullable = false, unique = true, length = 100)
  protected String name;

...

}
```

```sql
create table VALIDATIONANNOTATIONS_PRODUCT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    MEASURE integer not null,
    WEIGHT_PER_MEASURE decimal(19, 2) not null,
    PRICE_PER_MEASURE decimal(19, 2) not null,
    --
    primary key (ID)
)^

-- unique indexes

create unique index IDX_VALIDATIONANNOTATIONS_PRODUCT_UNIQ_NAME on VALIDATIONANNOTATIONS_PRODUCT (NAME) ^
create unique index IDX_VALIDATIONANNOTATIONS_PRODUCT_UNQ on VALIDATIONANNOTATIONS_PRODUCT (NAME, PRICE_PER_MEASURE, MEASURE) ^
```

[Top](#introduction)

## [JPA validation for entities](simple-validation/)

The next validation type that comes with [CUBA studio IDE.](https://www.cuba-platform.com/download) is a [bean validation](https://doc.cuba-platform.com/manual-6.8/bean_validation.html), some of which annotations could be configured from studio UI. This gives users an easy way to mark entity fields through the editor screen with the common validators.

![Figure 4: Standard entity validators in CUBA studio](resources/figure_4.png)<br />
_**Figure 4:** Standard entity validators in CUBA studio_

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

```java
@Pattern(message = "Incorrect IP address format",
         regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
@NotNull
@Column(name = "IP_ADDRESS", nullable = false, length = 40)
protected String ipAddress;
```
[Printer.java](listeners-validation/modules/global/src/io/dyakonoff/listenersvalidation/entity/Printer.java)

Although these annotations are simple to use and well documented, they can cover only simple cases. But sometimes we need to express more complex limitations while keeping the expressiveness and reusability of annotations-based approach. This is feaseble with custom validation annotations.

[Top](#introduction)

### Generic REST Validation

[Top](#introduction)

## Bean validation with custom annotations

[Custom annotations](https://doc.cuba-platform.com/manual-6.8/bean_validation_constraints.html#bean_validation_custom_constraints) could be defined not just for entities fields but also for Entity classes, POJOs and service methods. Let’s check out how to do that.

Assume we are building a products management system and an entity `Product` could have different measures:
* Units
* Kilograms
* Tons

On the other hand, `Product` class has `weightPerMeasure` field, which displays the weight of one product item. It’s quite obvious that only if product item’s measure is `ProductMeasure.Unit` then this `weightPerMeasure` can have arbitrary value (but still non-negative). If `product.measure == Kilograms` then `product.weightPerMeasure` should be equal 1 (one kilogram weights one kilogram exactly). The same is for Tons (1 ton = 1000 kilograms).

So, having the annotation expressing this just in one source code line looks quite appealing.

Following the [documentation](https://doc.cuba-platform.com/manual-6.8/bean_validation_constraints.html#bean_validation_custom_constraints) let's declare `@CheckProductWeightType` annotation that we want to be able to link to `weightPerMeasure` field but to be called when entity editor commits data as well.
This mean that this annotation shall be used to annotate classes and fields.

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

```java
@CheckProductWeightType(groups = UiCrossFieldChecks.class)
@NamePattern("%s|name")
@Table(name = "VALIDATIONANNOTATIONS_PRODUCT")
@Entity(name = "validationannotations$Product")
public class Product extends StandardEntity {

...

}
```
[Product.java](validation-with-custom-annotations/modules/global/src/io/dyakonoff/validationannotations/entity/Product.java)

It would be a good point to finish this article at this stage, and if you are in a hurry you can stop reading here. However, this tutorial won't be complete if I didn't touch other ways that can be used for data validation:

1.	Defining a custom `Validator` class or groovy validation script for UI components.
1.	Overriding `postValidate` method of a screen editor controller.
1.	Setting up an entity listener.
1.	Providing a transaction listener which to do validation before committing data into your database (which is the only way when many different entities are involved into the check).

Ok, let's start our advanced topics...

[Top](#introduction)

## Custom validator classes and scripts

Although [this approach](https://doc.cuba-platform.com/manual-6.8/gui_validator.html) works only at GUI level, there is a wide [range of components](http://files.cuba-platform.com/javadoc/cuba/6.8/com/haulmont/cuba/gui/components/validators/package-summary.html) implementing `Field.Validator` interface that come out of the box with CUBA platform.

Let's look at these cases:
1. Using of existing `Field.Validator`
1. Creating custom `Field.Validator` by yourself
1. Running a groovy script for a field validation
1. Setting screen validator programmatically

**Using of pre-defined Field.Validator class**

[CUBA studio](https://www.cuba-platform.com/download) gives a simple UI that allows developers to specify what class need to be used as a `Field.Validator` for the component. This editor is available from **Screen Designer** at the component properties tab.

![Figure 5: Accessing Validator property for FieldGroup](resources/figure_5.png)<br />
_**Figure 5:** Accessing Validator property for FieldGroup_

![Figure 6: Field.Validator editor](resources/figure_6.png)<br />
_**Figure 6:** Field.Validator editor_

From the XML layout perspective these validators looks quite simple and straightforward. You may wish to edit screen layout XML directly by hands, as for me this is a bit faster and simpler way of linking Validators to UI components.
```XML
<field property="vendorEmail">
    <validator class="com.haulmont.cuba.gui.components.validators.EmailValidator"/>
</field>
```
[product-edit.xml](validator-component/modules/web/src/io/dyakonoff/validatorcomponent/web/product/product-edit.xml)

**Creating custom Field.Validator**

Creation of a custom `Field.Validator` is not rocket science as well. Let's create for our simple [product management sample](validator-component/) a validator that does check the name of a product for swear words and doesn't let users to enter such bad-named-products into the system. To do that, we need to perform the next steps:

**1.** Create our custom `ProductNameValidator` class implementing `Field.Validator` interface.

```java
public class ProductNameValidator implements Field.Validator {

    private Logger log = LoggerFactory.getLogger(ProductNameValidator.class);

    protected String message;
    protected String messagesPack;
    protected Messages messages = AppBeans.get(Messages.NAME);

    public static String[] swearWords = { ... };

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
[ProductNameValidator.java](validator-component/modules/gui/src/io/dyakonoff/validatorcomponent/validation/ProductNameValidator.java)

_optionally_ provide a message in a screen message pack. (If you don't care about localisation and string resources that much, you can just override `validate` method and skip everything else in your`Field.Validator` class).

```
badNameInProductName = '%s' is not an appropriate word to be in a product name
```
[messages.properties](validator-component/modules/web/src/io/dyakonoff/validatorcomponent/web/product/messages.properties)

**2.** Use the just created `ProductNameValidator` in your screen.

Right through editing your screen XML layout:
```XML
<field property="name">
    <validator class="io.dyakonoff.validatorcomponent.validation.ProductNameValidator"
               message="msg://badNameInProductName"/>
</field>
```
[product-edit.xml](validator-component/modules/web/src/io/dyakonoff/validatorcomponent/web/product/product-edit.xml)

Or by specifying the Validator for the component using CUBA studio IDE:

![Figure 7: Setting up a custom Field.Validator using studio UI](resources/figure_7.png)<br />
_**Figure 7:** Setting up a custom Field.Validator using studio UI_

However, the second way would not let you to give additional parameters for validator like the `message` one above.

**Running a groovy script for a field validation**

Running [groovy](http://groovy-lang.org/) script dynamically with [Scripting interface](https://doc.cuba-platform.com/manual-6.8/scripting.html) looks quite appealing from the first glance. What you shall do is to write a small groovy script that has access to a named variable `value` (which represents the UI component value script needs to check) and return boolean check result back to the application.

This script can be specified either from CUBA studio UI:

![Figure 8: Setting up a groovy script for field validation](resources/figure_8.png)<br />
_**Figure 8:** Setting up a groovy script for field validation_

or by editing XML screen layout directly:

```XML
<field property="vendorSiteUrl">
    <validator>
        try
        {
            def instr = new java.net.URL(value).openStream()
            instr.close()
            return true
        }
        catch (Exception e)
        {
            return false
        }
    </validator>
</field>
```

However, this is not the approach I can recommend for complex cases, mainly because of difficulties with groovy script debugging and lack of support from an IDE. If you still chose to try that way I would say that keeping groovy script in a separate file and giving component reference to it would be a better option.

**Setting screen validator programmatically**

It's also possible to set Validator programmatically for a component (in your screen controller, for example), which could be a good option if you need to modify your validation rules on the fly according to some condition that can be determined only in run-time.

Here is a small example:

```java
public class ProductEdit extends AbstractEditor<Product> {
    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private BadWordsDetectionService badWordsDetectionService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        // Adding validator manually
        fieldGroup.getField("nameField").addValidator( value -> {
            String productName = (String) value;
            String badWord = badWordsDetectionService.detectBadWords(productName);
            if (badWord != null) {
                throw new ValidationException("Product name should not contain a word '" + badWord + "'");
            }
        });
    }

...

}
```
[ProductEdit.java](validation-in-controllers/modules/web/src/io/dyakonoff/controllersvalidation/web/product/ProductEdit.java)

Another example of adding `Field.Validator` in runtime can be found [here](https://www.cuba-platform.com/discuss/t/how-to-implement-error-display-when-using-custom-validator/2870/6).

[Top](#introduction)

## Validation in UI screen controllers

This is a simple and intuitive approach that wold allow you to perform quite complex checks of your screen data. Here are pros and cons of this way:

**Pros:**
* Easy to implement: you just need to override `postValidate` method in your screen controller.
* Has access to Entity object, screen UI controls, middleware services etc...
* Can do checks of arbitrary complexity.
* Easy to debug.

**Cons:**
* Acts only on one UI layer, so you'd need to repeat yourself if you have two or more UI modules (web and desktop, for example).
* Can't help with REST calls validation, even for [Generic REST](https://doc.cuba-platform.com/manual-6.5/rest_api_v2.html).
* Has difficulties with highlighting fields/components that contains incorrect data. (You'd have to do some CSS/JS magic to achieve that result.)

Let's look at the code:
```java
public class ProductEdit extends AbstractEditor<Product> {

...

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        Product product = getItem();
        if (product.getRetail() && product.getPrice().compareTo(new BigDecimal(10000)) > 0) {
            errors.add("Retail product can not have price greater than 10,000");
        }
    }
}
```
[ProductEdit.java](validation-in-controllers/modules/web/src/io/dyakonoff/controllersvalidation/web/product/ProductEdit.java)

However, combining this approach with static and dynamically added `Field.Validator` checks would negate the last flaw.

[Top](#introduction)

## Using middleware listeners for data validation

Let's discuss the last two ways of validating the data that [CUBA platform](https://www.cuba-platform.com/) offers: [entity listeners](https://doc.cuba-platform.com/manual-6.8/entity_listeners.html) and [transaction listeners](https://doc.cuba-platform.com/manual-6.8/transaction_listeners.html).
These listeners act on the middle tier and allow you to intercept data before the changes be passed to a database. Power of this approach is based on the fact that incorrect data would not be able to pass your checks, doesn't matter from where they came.

Also, [transaction listeners](https://doc.cuba-platform.com/manual-6.8/transaction_listeners.html) seems to be the best way to perform complex data checks that should involve analysis of more than just one entity object. So, you'd like to use them when you had to validate the state of your objects graph before committing it to the database.

In both cases, you'd need to define your custom `RuntimeException` class in global module and mark it with [@SupportedByClient](https://doc.cuba-platform.com/manual-6.8/remoteException.html) annotation to have your error messages transporting from middleware to client tier.

Also, it seems to be a good idea to implement custom [client-level exception handlers](https://doc.cuba-platform.com/manual-6.8/exceptionHandlers.html) to have your error messages displayed properly. However, if you don't care much about how your errors are displayed to a user, you can skip this step.

![without implementing client-level exception handlers](resources/figure_9.png)<br />
_**Figure 9:** Error message without implementing custom client-level exception handlers_

![after implementing custom client-level exception handlers](resources/figure_10.png)<br />
_**Figure 10:** Error message after implementing custom client-level exception handlers_

Let's look at the examples.

Assume that we have a small print jobs management system with two entities: `Printers` and `PrintJobs`. We want to check that each printer has accessible IP address before saving it's parameters to the database. Also we want to ensure that two-sided documents can be assigned only to printers that support duplex (two-sided) printing.

We will implement the first constraint using Entity Listener and the second one using Transaction Listener.

[Top](#introduction)

### Single Entity Context

For the start we need to create an Entity Listener for our `Printer` entity. The simplest way is to do that using CUBA studio and in **Middleware** section pick menu item **New / Entity Listener**.

![Figure 11: Creating Entity Listener with CUBA studio](resources/figure_11.png)<br />
_**Figure 11:** Creating Entity Listener with CUBA studio_

1. Give proper name to the Listener class,
1. Check `BeforeInsertEntityListener` and `BeforeUpdateEntityListener` interfaces to be implemented
1. Specify that entity `Printer` need  to be handled by the listener

![Figure 12: Setting parameters for Entity Listener](resources/figure_12.png)<br />
_**Figure 12:** Setting parameters for Entity Listener_

As an alternative it is possible to create [Entity Listener class](listeners-validation/modules/core/src/io/dyakonoff/listenersvalidation/listener/PrinterEntityListener.java) manually and mark `Printer class` with `@Listeners("listenersvalidation_PrinterEntityListener")` annotation:

```java
@Listeners("listenersvalidation_PrinterEntityListener")
@NamePattern("%s|name")
@Table(name = "LISTENERSVALIDATION_PRINTER")
@Entity(name = "listenersvalidation$Printer")
public class Printer extends StandardEntity {
  ...
}
```

Before start working on our Entity Listener class, let's define custom `RuntimeException` and mark it with `@SupportedByClient` annotation to allow this exception to be passed to the client tier.

```java
@SupportedByClient
public class PrinterValidationException extends RuntimeException {
    public PrinterValidationException() {
    }
    public PrinterValidationException(String message) {
        super(message);
    }
    public PrinterValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    protected PrinterValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```
[PrinterValidationException.java](listeners-validation/modules/global/src/io/dyakonoff/listenersvalidation/exception/PrinterValidationException.java)


Then wee need to open our Entity Listener class in IDE and implement both handlers. In our case they are identical and use a [middleware service](listeners-validation/modules/core/src/io/dyakonoff/listenersvalidation/listener/IpAddressCheckerServiceBean.java) to check if IP address is reachable.

```java
@Component("listenersvalidation_PrinterEntityListener")
public class PrinterEntityListener implements BeforeInsertEntityListener<Printer>, BeforeUpdateEntityListener<Printer> {

    @Inject
    private IpAddressCheckerService ipAddressCheckerService;

    @Override
    public void onBeforeInsert(Printer entity, EntityManager entityManager) {
        checkPrinterIsReachable(entity);
    }

    @Override
    public void onBeforeUpdate(Printer entity, EntityManager entityManager) {
        checkPrinterIsReachable(entity);
    }

    private void checkPrinterIsReachable(Printer printer) {
        String ipAddr = printer.getIpAddress();
        if (!ipAddressCheckerService.checkIpAddrIsReacheble(ipAddr, 2000)) {
            throw new PrinterValidationException("Printer at " + ipAddr + " is not reachable");
        }
    }
}
```
[PrinterEntityListener.java](listeners-validation/modules/core/src/io/dyakonoff/listenersvalidation/listener/PrinterEntityListener.java)

The last step will be implementing client-level exception handler that will be used for both Entity and Transaction listeners errors processing.

```java
@Component("listenersvalidation_PrintingValidationExceptionHandler")
public class PrintingValidationExceptionHandler extends AbstractGenericExceptionHandler {

    public PrintingValidationExceptionHandler() {
        super(PrintJobValidationException.class.getName(), PrinterValidationException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(message, Frame.NotificationType.ERROR);
    }
}
```
[PrintingValidationExceptionHandler.java](listeners-validation/modules/web/src/io/dyakonoff/listenersvalidation/exception/PrintingValidationExceptionHandler.java)

[Top](#introduction)

### Transaction Context

Handling `PrintJob` entity validation with Transaction Listener is quite similar, we need to:

* Create new `TransactionListener` with CUBA studio.
* Give it a proper class name if needed.
* Specify that `BeforeCommitTransactionListener` need to be implemented in [this bean](listeners-validation/modules/core/src/io/dyakonoff/listenersvalidation/listener/TransactionListener.java).
* Define custom runtime exception: [PrintJobValidationException](listeners-validation/modules/global/src/io/dyakonoff/listenersvalidation/exception/PrintJobValidationException.java).
* Implement validation logic in your transaction listener.

```java
@Component("listenersvalidation_TransactionListener")
public class TransactionListener implements BeforeCommitTransactionListener {

    private Logger log = LoggerFactory.getLogger(TransactionListener.class);

    @Inject
    private PersistenceTools persistenceTools;

    @Inject
    private IpAddressCheckerService ipAddressCheckerService;

    @Override
    public void beforeCommit(EntityManager entityManager, Collection<Entity> managedEntities) {
        for (Entity entity : managedEntities) {
            if (!persistenceTools.isDirty(entity))
                continue;

            if (entity instanceof  PrintJob) {
                PrintJob pj = (PrintJob)entity;
                Printer printer = pj.getPrinter();
                if ((pj.getPrintOnBothSides() != null && pj.getPrintOnBothSides())
                        && (printer.getDuplexSupport() == null || !printer.getDuplexSupport())
                        ) {
                    String msg = "File " + pj.getFile().getName() + " can't be printed on printer " + printer.getName() +
                            ", this printer does not support duplex printing";
                    throw new PrintJobValidationException(msg);

                }
            }
        }
    }
}
```
_[TransactionListener.java](listeners-validation/modules/core/src/io/dyakonoff/listenersvalidation/listener/TransactionListener.java)_

[Top](#introduction)

## Conclusion

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



_**Table 2:** Validation implementation complexity_

|                                            | Elementary | Simple | Average | Complex  |
|--------------------------------------------|:----------:|:------:|:-------:|:--------:|
| _DB level JPA constraints_                 |    yes     |        |         |          |
| _Bean validation (JPA annotations)_        |    yes     |        |         |          |
| _Bean validation (custom annotations_      |            |        |   yes   |          |
| _UI validation (standard Field.Validator_  |    yes     |        |         |          |
| _Custom Field.Validator (Java class)_      |            |        |   yes   |          |
| _Custom Field.Validator (Groovy script)_   |            |  yes   |         |          |
| _Screen controllers validation_            |            |  yes   |         |          |
| _Entity listeners_                         |            |        |         |   yes    |
| _Transaction listeners_                    |            |        |         |   yes    |



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

\* - for complex indexes (`@UniqueConstraint`), see example: [Product.java](validation-with-custom-annotations/modules/global/src/io/dyakonoff/validationannotations/entity/Product.java)



1. Bean validation could use standard and custom annotations. It works on all tiers, so offers the best level of data security. Besides that it is reusable and gives good UI feedback to a user. The limitations of that approach:
    1. It can't be used for validating the whole data graph when you need to check state of more than one entity.
    1. Business logic at middleware level can change the entities directly and they will not be validated by this mechanism even before saving them to DB.
1. Defining custom Validator class and groovy scripts for UI components. Since it works at UI level only, this mechanism offers nice UI integration suppot (highlighting and pretty error messages formatting) but drawbacks are the same as for annotations, plus:
    1. It won't be able to check Generic REST calls.
    1. Groovy scripts are hard to debug.
1. Validation in UI screen controllers - it is the simplest way to do validation if there are no standard annotations or `Validator` classes to do it. However, you get the checks done only at UI level and the code reusability is not at the best level.
1. Entity and Transaction listeners give the best security level. Transaction listeners are capable to check the whole object graph. But this approach requires more coding, has not that good UI integration and doing data validation right before commit might lead to inability to fix the data because it could be too late to do something with data error. Also, complex listeners can degrade the system performance as they happen for **every** data commit.

[Top](#introduction)
