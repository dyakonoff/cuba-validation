-- begin ORDERMAN_CUSTOMER
create table ORDERMAN_CUSTOMER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    EMAIL varchar(255) not null,
    PHONE varchar(30),
    LOGO_IMAGE_ID varchar(36),
    ADDRESS_LINE1 varchar(255) not null,
    ADDRESS_LINE2 varchar(255),
    ADDRESS_LINE3 varchar(255),
    POSTAL_CODE varchar(16) not null,
    --
    primary key (ID)
)^
-- end ORDERMAN_CUSTOMER
-- begin ORDERMAN_PRODUCT
create table ORDERMAN_PRODUCT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    DESCRIPTION longvarchar,
    MEASURE integer not null,
    PRICE_PER_MEASURE decimal(19, 2) not null,
    --
    primary key (ID)
)^
-- end ORDERMAN_PRODUCT
-- begin ORDERMAN_STOCK
create table ORDERMAN_STOCK (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PRODUCT_ID varchar(36) not null,
    IN_STOCK decimal(19, 2),
    OPTIMAL_STOCK_LEVEL decimal(19, 2),
    --
    primary key (ID)
)^
-- end ORDERMAN_STOCK
-- begin ORDERMAN_ORDER_ITEM
create table ORDERMAN_ORDER_ITEM (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PRODUCT_ID varchar(36) not null,
    QUANTITY decimal(19, 2) not null,
    SUB_TOTAL decimal(19, 2) not null,
    ORDER_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end ORDERMAN_ORDER_ITEM
-- begin ORDERMAN_ORDER
create table ORDERMAN_ORDER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CUSTOMER_ID varchar(36) not null,
    DATE_ date not null,
    NUMBER_ varchar(20) not null,
    STATUS integer not null,
    PRICE decimal(19, 2) not null,
    --
    primary key (ID)
)^
-- end ORDERMAN_ORDER
