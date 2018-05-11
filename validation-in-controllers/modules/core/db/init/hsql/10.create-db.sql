-- begin CONTROLLERSVALIDATION_VENDOR
create table CONTROLLERSVALIDATION_VENDOR (
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
    LOGO_ID varchar(36),
    CONTACT_PERSON varchar(255),
    CONTACT_EMAIL varchar(255) not null,
    CONTACT_PHONE varchar(40) not null,
    --
    primary key (ID)
)^
-- end CONTROLLERSVALIDATION_VENDOR
-- begin CONTROLLERSVALIDATION_PRODUCT
create table CONTROLLERSVALIDATION_PRODUCT (
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
    RETAIL boolean not null,
    PRICE decimal(19, 2) not null,
    IMAGE_ID varchar(36),
    VENDOR_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end CONTROLLERSVALIDATION_PRODUCT
