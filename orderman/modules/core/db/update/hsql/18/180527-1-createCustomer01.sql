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
);
