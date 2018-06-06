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
    MANAGER_ID varchar(36) not null,
    --
    primary key (ID)
);
