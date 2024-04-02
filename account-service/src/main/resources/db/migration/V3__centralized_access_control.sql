CREATE TABLE IF NOT EXISTS account.ACCESS_CONTROL(
    CLAZZ VARCHAR(50) NOT NULL,
    CLAZZ_ID BIGINT NOT NULL,
    ACCOUNT_GROUP_ID BIGINT NOT NULL REFERENCES ACCOUNT_GROUP (ID),

    CONSTRAINT ACCESS_CONTROL_UNIQUE UNIQUE ( CLAZZ, CLAZZ_ID )
);

create extension IF NOT EXISTS dblink;
select dblink_connect('pantry_conn', 'postgresql://pantry:pantry@pantry-db:5432/pantry-db');

insert into account.ACCESS_CONTROL (clazz, clazz_id, account_group_id)
( select clazz, clazz_id, account_group_id
         from dblink('pantry_conn', 'SELECT * FROM access_control')
         AS t(clazz text, clazz_id int, account_group_id int) );

select dblink_disconnect('pantry_conn');
drop extension IF EXISTS dblink;