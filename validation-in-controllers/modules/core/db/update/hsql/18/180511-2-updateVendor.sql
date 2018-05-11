update CONTROLLERSVALIDATION_VENDOR set NAME = '' where NAME is null ;
alter table CONTROLLERSVALIDATION_VENDOR alter column NAME set not null ;
