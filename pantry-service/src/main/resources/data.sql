INSERT INTO PANTRY (NAME, TYPE, IS_ACTIVE) VALUES ('DESPENSA BASE', 'R', TRUE) ON CONFLICT(NAME) DO NOTHING;
INSERT INTO PRODUCT (CODE, DESCRIPTION, SIZE) VALUES ('SAHNE', 'Sahne zum Kochen', '200g') ON CONFLICT(CODE) DO NOTHING;
INSERT INTO PRODUCT (CODE, DESCRIPTION, SIZE) VALUES ('VOLLMILCH', 'Vollmilch', '1l') ON CONFLICT(CODE) DO NOTHING;
--INSERT INTO PANTRY_ITEM (PANTRY_ID, PRODUCT_ID, IDEAL_QTY, CURRENT_QTY, PROVISIONED_QTY) VALUES (1, 1, 5, 5, 0) ON CONFLICT(PANTRY_ID, PRODUCT_ID) DO NOTHING;
--INSERT INTO PANTRY_ITEM (PANTRY_ID, PRODUCT_ID, IDEAL_QTY, CURRENT_QTY, PROVISIONED_QTY) VALUES (1, 2, 4, 4, 0) ON CONFLICT(PANTRY_ID, PRODUCT_ID) DO NOTHING