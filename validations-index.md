# Index of validation methods in sample project

## Links
[Sample project description](order-management.md)
[Sample project code](order-management/)
[Article](README.md)

## Methods index

1. DB level single-fields JPA: @NotNull
  1. [Customer entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Customer.java): `@NotNull, @Column(unique=true), @Column(length=16)`
  1. [Order entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@NotNull`
  1. [OrderItem entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@NotNull`
  1. [Product entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Product.java): `@NotNull`
  1. [Stock entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Stock.java): `@NotNull`
1. Multi-column indexes
  1. [Product entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Product.java)
1. Bean validation (JPA & Hibernate - standard annotations)
  1. [Customer entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Customer.java): `@Email, @Length, @Pattern`
  1. [Order entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@Temporal`
  1. [OrderItem entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Order.java): `@DecimalMin(inclusive = false)`
  1. [Product entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Product.java): `@DecimalMin`
  1. [Stock entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Stock.java): `@DecimalMin`
1. Bean validation (custom annotations)
  1. [Customer entity](order-management/modules/global/src/com/haulmont/dyakonoff/entity/Customer.java): `@UsPhoneNumber`
1. Generic REST validation
1. Field.Validator - standard implementations
1. Field.Validator - custom Java classes implementations
1. Field.Validator - Groovy script implementation
1. UI screen controllers
1. Entity listeners
1. Transaction listeners

[Back to article](README.md)
