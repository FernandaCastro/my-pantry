INSERT INTO account.PERMISSION ("id", "name") VALUES (16, 'LIST_SUPERMARKET') on conflict do nothing;
INSERT INTO account.PERMISSION ("id", "name") VALUES (17, 'CREATE_SUPERMARKET') on conflict do nothing;
INSERT INTO account.PERMISSION ("id", "name") VALUES (18, 'EDIT_SUPERMARKET') on conflict do nothing;
INSERT INTO account.PERMISSION ("id", "name") VALUES (19, 'DELETE_SUPERMARKET') on conflict do nothing;

INSERT INTO account.ROLE_PERMISSION ("role_id", "permission_id") VALUES
(1, 16), (1, 17),(1, 18),(1, 19),
(2, 16), (2, 17),(2, 18),(2, 19),
(3, 16)
on conflict do nothing;