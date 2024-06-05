alter table pantry alter column NAME type VARCHAR(50);
alter table pantry drop constraint IF EXISTS pantry_name_key;

alter table product alter column CODE type VARCHAR(50);
alter table product alter column DESCRIPTION type VARCHAR(100);
alter table product alter column CATEGORY type VARCHAR(50);


