alter table VALIDATIONANNOTATIONS_PRODUCT alter column PRICE_PER_UNIT rename to PRICE_PER_UNIT__U41725 ;
alter table VALIDATIONANNOTATIONS_PRODUCT alter column PRICE_PER_UNIT__U41725 set null ;
alter table VALIDATIONANNOTATIONS_PRODUCT alter column WEIGHT_PER_UNIT rename to WEIGHT_PER_UNIT__U55855 ;
alter table VALIDATIONANNOTATIONS_PRODUCT alter column WEIGHT_PER_UNIT__U55855 set null ;
alter table VALIDATIONANNOTATIONS_PRODUCT add column WEIGHT_PER_MEASURE decimal(19, 2) ^
update VALIDATIONANNOTATIONS_PRODUCT set WEIGHT_PER_MEASURE = 0 where WEIGHT_PER_MEASURE is null ;
alter table VALIDATIONANNOTATIONS_PRODUCT alter column WEIGHT_PER_MEASURE set not null ;
alter table VALIDATIONANNOTATIONS_PRODUCT add column PRICE_PER_MEASURE decimal(19, 2) ^
update VALIDATIONANNOTATIONS_PRODUCT set PRICE_PER_MEASURE = 0 where PRICE_PER_MEASURE is null ;
alter table VALIDATIONANNOTATIONS_PRODUCT alter column PRICE_PER_MEASURE set not null ;