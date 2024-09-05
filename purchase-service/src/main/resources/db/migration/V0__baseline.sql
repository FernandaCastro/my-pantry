CREATE SCHEMA IF NOT EXISTS purchase;

CREATE TABLE IF NOT EXISTS purchase.PURCHASE(
    ID BIGSERIAL NOT NULL PRIMARY KEY,
    CREATED_AT TIMESTAMP,
    PROCESSED_AT TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchase.PRODUCT(
    ID BIGINT NOT NULL PRIMARY KEY,
    CODE VARCHAR(30) NOT NULL,
    DESCRIPTION VARCHAR(30),
    SIZE VARCHAR(10),
    CATEGORY VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS purchase.PURCHASE_ITEM(
    ID BIGSERIAL NOT NULL PRIMARY KEY,
    PURCHASE_ID BIGINT REFERENCES PURCHASE (ID),
    PRODUCT_ID BIGINT NOT NULL REFERENCES PRODUCT (ID),
    PANTRY_ID INTEGER NOT NULL,
    PANTRY_NAME VARCHAR(30) NOT NULL,
    QTY_PROVISIONED INTEGER,
    QTY_PURCHASED INTEGER
);

CREATE TABLE IF NOT EXISTS purchase.PROPERTIES(
    PROPERTY_KEY VARCHAR(50)  PRIMARY KEY NOT NULL,
    PROPERTY_VALUE JSONB NOT NULL
);

INSERT INTO PROPERTIES VALUES
('product.categories', '["", "Bakery", "Beverages", "Cleaning", "Cookies", "Dairy", "Frozen", "Fruit and vegetables", "Grocery", "Meat", "Personal Hygiene", "Refrigerated"]')
on conflict do nothing;

INSERT INTO PROPERTIES VALUES
('rewe.supermarket.categories',
'["Fruit and vegetables", "Refrigerated", "Meat", "Bakery", "Dairy", "Grocery", "Beverages", "Cookies", "Cleaning", "Frozen", "Personal Hygiene", ""]')
on conflict do nothing;

INSERT INTO PROPERTIES VALUES
('aldi.supermarket.categories',
'["Bakery", "Cookies", "Fruit and vegetables", "Refrigerated", "Meat", "Dairy", "Beverages", "Grocery", "Frozen",  "Cleaning", "Personal Hygiene", ""]')
on conflict do nothing;

