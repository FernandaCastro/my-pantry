CREATE TABLE IF NOT EXISTS account.ACCESS_CONTROL(
    CLAZZ VARCHAR(50) NOT NULL,
    CLAZZ_ID BIGINT NOT NULL,
    ACCOUNT_GROUP_ID BIGINT NOT NULL REFERENCES ACCOUNT_GROUP (ID),

    CONSTRAINT ACCESS_CONTROL_UNIQUE UNIQUE ( CLAZZ, CLAZZ_ID )
);

--Copy access-control data from pantry_db to account_db
create extension IF NOT EXISTS dblink;
select dblink_connect('pantry_conn', 'postgresql://pantry:pantry@pantry-db:5432/pantry-db');

insert into account.ACCESS_CONTROL (clazz, clazz_id, account_group_id)
( select clazz, clazz_id, account_group_id
         from dblink('pantry_conn', 'SELECT * FROM access_control')
         AS t(clazz text, clazz_id int, account_group_id int) );

select dblink_disconnect('pantry_conn');
drop extension IF EXISTS dblink;

--Updates on Permissions
UPDATE account.PERMISSION set name = 'CONSUME_PANTRY' where name = 'CONSUME_PANTRY_ITEM';
UPDATE account.PERMISSION set name = 'ANALYSE_PANTRY' where name = 'ANALYSE_PANTRY_ITEM';
INSERT INTO account.PERMISSION ("id", "name") VALUES (15, 'PURCHASE_PANTRY') on conflict do nothing;
INSERT INTO account.ROLE_PERMISSION ("role_id", "permission_id") VALUES (1, 15), (2, 15),(3, 15) on conflict do nothing;;