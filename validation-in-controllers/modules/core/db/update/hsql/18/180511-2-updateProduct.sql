alter table CONTROLLERSVALIDATION_PRODUCT add column RETAIL boolean ^
update CONTROLLERSVALIDATION_PRODUCT set RETAIL = false where RETAIL is null ;
alter table CONTROLLERSVALIDATION_PRODUCT alter column RETAIL set not null ;
