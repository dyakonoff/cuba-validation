-- begin LISTENERSVALIDATION_PRINTER
create table LISTENERSVALIDATION_PRINTER (
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
    LOCATION longvarchar,
    IP_ADDRESS varchar(40) not null,
    PORT integer not null,
    --
    primary key (ID)
)^
-- end LISTENERSVALIDATION_PRINTER
-- begin LISTENERSVALIDATION_PRINT_JOB
create table LISTENERSVALIDATION_PRINT_JOB (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID varchar(36) not null,
    PRINTER_ID varchar(36) not null,
    COPIES_COUNT integer not null,
    PRINT_ON_BOTH_SIDES boolean,
    --
    primary key (ID)
)^
-- end LISTENERSVALIDATION_PRINT_JOB
