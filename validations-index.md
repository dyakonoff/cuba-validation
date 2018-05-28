# Index of validation methods in sample project

## Links
[Sample project description](order-management.md)
[Sample project code](order-management/)
[Article](README.md)


| Validation method                                         | Abbreviation |
|-----------------------------------------------------------|--------------|
| DB level single-fields JPA                                | JPA-DB       |
| Multi-column indexes                                      | JPA-IND      |
| Bean validation (JPA & Hibernate - standard annotations)  | JPA-STD      |
| Bean validation (custom annotations)                      | BEAN         |
| Generic REST validation                                   | REST         |
| Field.Validator - standard implementations                | FV-STD       |
| Field.Validator - custom Java classes implementations     | FV-JAVA      |
| Field.Validator - Groovy script implementation            | FV-GROOVY    |
| UI screen controllers                                     | UI-CNTR      |
| Entity listeners                                          | ENTLIS       |
| Transaction listeners                                     | TRALIS       |

## Methods index

1. DB level single-fields JPA: @NotNull
    1. [Customer entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Customer.java): `@NotNull, @Column(unique=true), @Column(length=16)`
    1. [Order entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@NotNull`
    1. [OrderItem entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@NotNull`
    1. [Product entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Product.java): `@NotNull`
    1. [Stock entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Stock.java): `@NotNull`
1. Multi-column indexes
    1. [Product entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Product.java)
1. Bean validation (JPA & Hibernate - standard annotations)
    1. [Customer entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Customer.java): `@Email, @Length, @Pattern`
    1. [Order entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@Temporal`
    1. [OrderItem entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@DecimalMin(inclusive = false)`
    1. [Product entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Product.java): `@DecimalMin`
    1. [Stock entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Stock.java): `@DecimalMin`
1. Bean validation (custom annotations)
    1. [Customer entity](orderman/modules/global/src/com/haulmont/dyakonoff/entity/Customer.java): `@UsPhoneNumber`
    1. []
1. Generic REST validation
1. Field.Validator - standard implementations
    1. [Customer edit screen](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/customer/customer-edit.xml): `EmailValidator`
1. Field.Validator - custom Java classes implementations
    1. [Product edit screen](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/product/product-edit.xml): `ProductNameValidator`
1. Field.Validator - Groovy script implementation
1. UI screen controllers
    1. [OrderItemEdit](orderman/modules/web/src/com/haulmont/dyakonoff/orderman/web/orderitem/OrderItemEdit.java): `postValidate(...)`
1. Entity listeners
    1. [OrderEntityListener](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/OrderEntityListener.java)
1. Transaction listeners
    1. [TransactionListener](orderman/modules/core/src/com/haulmont/dyakonoff/orderman/service/TransactionListener.java)

[Back to article](README.md)
