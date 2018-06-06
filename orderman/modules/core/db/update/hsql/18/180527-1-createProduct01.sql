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
);
