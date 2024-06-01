INSERT INTO account.PERMISSION ("id", "name") VALUES (16, 'LIST_SUPERMARKET') on conflict do nothing;
INSERT INTO account.PERMISSION ("id", "name") VALUES (17, 'CREATE_SUPERMARKET') on conflict do nothing;
INSERT INTO account.PERMISSION ("id", "name") VALUES (18, 'EDIT_SUPERMARKET') on conflict do nothing;
INSERT INTO account.PERMISSION ("id", "name") VALUES (19, 'DELETE_SUPERMARKET') on conflict do nothing;

INSERT INTO account.ROLE_PERMISSION ("role_id", "permission_id") VALUES
(1, 16), (1, 17),(1, 18),(1, 19),
(2, 16), (2, 17),(2, 18),(2, 19),
(3, 16)
on conflict do nothing;

-- ADD TO ROLE-ADMIN:  2-'CREATE_PANTRY', 3-'EDIT_PANTRY', 8-'DELETE_PANTRY_ITEM', 11-'CREATE_PRODUCT', 12-'EDIT_PRODUCT'
INSERT INTO account.ROLE_PERMISSION ("role_id", "permission_id") VALUES
 (2, 2), (2, 3), (2, 8),(2, 11),(2, 12) on conflict do nothing;

