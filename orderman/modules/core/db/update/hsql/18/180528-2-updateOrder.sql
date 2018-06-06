alter table ORDERMAN_ORDER alter column MANAGER_ID rename to MANAGER_ID__U26861 ^
alter table ORDERMAN_ORDER alter column MANAGER_ID__U26861 set null ;
drop index IDX_ORDERMAN_ORDER_MANAGER ;
alter table ORDERMAN_ORDER drop constraint FK_ORDERMAN_ORDER_MANAGER ;
