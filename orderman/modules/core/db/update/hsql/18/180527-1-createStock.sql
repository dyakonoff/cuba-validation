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
);
