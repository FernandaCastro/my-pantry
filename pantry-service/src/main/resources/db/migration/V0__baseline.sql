CREATE SCHEMA IF NOT EXISTS pantry;

CREATE TABLE IF NOT EXISTS pantry.PRODUCT(
    ID BIGSERIAL PRIMARY KEY,
    CODE VARCHAR(30) NOT NULL,
    DESCRIPTION VARCHAR(30),
    SIZE VARCHAR(10),
    CATEGORY VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS pantry.PANTRY(
    ID BIGSERIAL PRIMARY KEY,
    NAME VARCHAR(30) NOT NULL UNIQUE,
    TYPE CHAR(1),
    IS_ACTIVE BOOLEAN
);

CREATE TABLE IF NOT EXISTS pantry.PANTRY_ITEM(
    PANTRY_ID BIGINT NOT NULL REFERENCES PANTRY (ID),
    PRODUCT_ID BIGINT NOT NULL REFERENCES PRODUCT (ID),
    IDEAL_QTY INTEGER,
    CURRENT_QTY INTEGER,
    PROVISIONED_QTY INTEGER,
    LAST_PROVISIONING TIMESTAMP,

    CONSTRAINT PANTRY_PRODUCT_UNIQUE UNIQUE ( PANTRY_ID, PRODUCT_ID )
);


