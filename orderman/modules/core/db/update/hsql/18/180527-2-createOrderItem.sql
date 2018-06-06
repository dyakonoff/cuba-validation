alter table ORDERMAN_ORDER_ITEM add constraint FK_ORDERMAN_ORDER_ITEM_PRODUCT foreign key (PRODUCT_ID) references ORDERMAN_PRODUCT(ID);
alter table ORDERMAN_ORDER_ITEM add constraint FK_ORDERMAN_ORDER_ITEM_ORDER foreign key (ORDER_ID) references ORDERMAN_ORDER(ID);
create index IDX_ORDERMAN_ORDER_ITEM_PRODUCT on ORDERMAN_ORDER_ITEM (PRODUCT_ID);
create index IDX_ORDERMAN_ORDER_ITEM_ORDER on ORDERMAN_ORDER_ITEM (ORDER_ID);
