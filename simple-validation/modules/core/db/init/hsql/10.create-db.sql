-- begin SIMPLEVALIDATION_CARGO_BAY
create table SIMPLEVALIDATION_CARGO_BAY (
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
    BAY_NUMBER integer not null,
    MAX_LOAD decimal(19, 2) not null,
    BAY_AREA double precision not null,
    LAST_OPERATION_DATE date not null,
    --
    primary key (ID)
)^
-- end SIMPLEVALIDATION_CARGO_BAY
