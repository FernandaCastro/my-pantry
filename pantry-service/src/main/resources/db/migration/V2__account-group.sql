CREATE TABLE IF NOT EXISTS pantry.ACCESS_CONTROL(
    CLAZZ VARCHAR(50) NOT NULL,
    CLAZZ_ID BIGINT NOT NULL,
    ACCOUNT_GROUP_ID BIGINT NOT NULL,

    CONSTRAINT ACCESS_CONTROL_UNIQUE UNIQUE ( CLAZZ, CLAZZ_ID )
);

ALTER TABLE pantry.PANTRY DROP CONSTRAINT IF EXISTS pantry_name_key;