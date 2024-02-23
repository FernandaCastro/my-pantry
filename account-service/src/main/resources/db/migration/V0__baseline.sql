CREATE SCHEMA IF NOT EXISTS AUTHORIZATION account;

CREATE TABLE IF NOT EXISTS account.ACCOUNT(
    ID BIGSERIAL PRIMARY KEY NOT NULL,
    EXTERNAL_ID VARCHAR(50),
    EXTERNAL_PROVIDER VARCHAR(30),
    NAME VARCHAR(20),
    EMAIL VARCHAR(50),
    PICTURE_URL VARCHAR(100),
    ROLES VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS account.ACCOUNT_GROUP(
    ID BIGSERIAL PRIMARY KEY NOT NULL,
    NAME VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS account.ACCOUNT_GROUP_ACCOUNT(
    ACCOUNT_GROUP_ID BIGSERIAL NOT NULL REFERENCES ACCOUNT_GROUP (ID),
    ACCOUNT_ID BIGSERIAL NOT NULL REFERENCES ACCOUNT (ID),
    ROLE VARCHAR(30) NOT NULL
);




