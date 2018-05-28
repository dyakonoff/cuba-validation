-- begin ORDERMAN_CUSTOMER
alter table ORDERMAN_CUSTOMER add constraint FK_ORDERMAN_CUSTOMER_LOGO_IMAGE foreign key (LOGO_IMAGE_ID) references SYS_FILE(ID)^
create unique index IDX_ORDERMAN_CUSTOMER_UNIQ_EMAIL on ORDERMAN_CUSTOMER (EMAIL) ^
create index IDX_ORDERMAN_CUSTOMER_LOGO_IMAGE on ORDERMAN_CUSTOMER (LOGO_IMAGE_ID)^
-- end ORDERMAN_CUSTOMER
-- begin ORDERMAN_PRODUCT
create unique index IDX_ORDERMAN_PRODUCT_UNQ on ORDERMAN_PRODUCT (NAME, MEASURE) ^
-- end ORDERMAN_PRODUCT
-- begin ORDERMAN_STOCK
alter table ORDERMAN_STOCK add constraint FK_ORDERMAN_STOCK_PRODUCT foreign key (PRODUCT_ID) references ORDERMAN_PRODUCT(ID)^
create unique index IDX_ORDERMAN_STOCK_UNIQ_PRODUCT_ID on ORDERMAN_STOCK (PRODUCT_ID) ^
create index IDX_ORDERMAN_STOCK_PRODUCT on ORDERMAN_STOCK (PRODUCT_ID)^
-- end ORDERMAN_STOCK
-- begin ORDERMAN_ORDER_ITEM
alter table ORDERMAN_ORDER_ITEM add constraint FK_ORDERMAN_ORDER_ITEM_PRODUCT foreign key (PRODUCT_ID) references ORDERMAN_PRODUCT(ID)^
alter table ORDERMAN_ORDER_ITEM add constraint FK_ORDERMAN_ORDER_ITEM_ORDER foreign key (ORDER_ID) references ORDERMAN_ORDER(ID)^
create index IDX_ORDERMAN_ORDER_ITEM_PRODUCT on ORDERMAN_ORDER_ITEM (PRODUCT_ID)^
create index IDX_ORDERMAN_ORDER_ITEM_ORDER on ORDERMAN_ORDER_ITEM (ORDER_ID)^
-- end ORDERMAN_ORDER_ITEM
-- begin ORDERMAN_ORDER
alter table ORDERMAN_ORDER add constraint FK_ORDERMAN_ORDER_CUSTOMER foreign key (CUSTOMER_ID) references ORDERMAN_CUSTOMER(ID)^
create index IDX_ORDERMAN_ORDER_CUSTOMER on ORDERMAN_ORDER (CUSTOMER_ID)^
-- end ORDERMAN_ORDER
