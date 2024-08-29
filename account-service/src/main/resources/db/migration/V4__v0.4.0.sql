--1 - Replace Primary Key in table ROLE_PERMISSION by ROLE_NAME and PERMISSION_NAME
--2 - Replace Foreign Key in table ACCOUNT_GROUP_MEMBER. Replace ROLE.ID by ROLE.NAME
--3 - Recreate Primary Key in tables ROLE and PERMISSION. Replacing column name ID to NAME.
--4 - Rename column NAME to ID in tables ACCOUNT_GROUP_MEMBER and ROLE_PERMISSION

--1
ALTER TABLE account.ROLE_PERMISSION
ADD COLUMN ROLE_NAME VARCHAR(30) REFERENCES ROLE(NAME),
ADD COLUMN PERMISSION_NAME VARCHAR(30) REFERENCES PERMISSION(NAME);

UPDATE account.ROLE_PERMISSION
SET ROLE_NAME = (select r.NAME from ROLE r where ROLE_ID = r.id),
    PERMISSION_NAME = (select p.NAME from PERMISSION p where PERMISSION_ID = p.id);

ALTER TABLE account.ROLE_PERMISSION
	DROP CONSTRAINT ROLE_PERMISSION_PK RESTRICT,
	ADD CONSTRAINT ROLE_PERMISSION_PK PRIMARY KEY ( ROLE_NAME, PERMISSION_NAME ),
	DROP COLUMN ROLE_ID,
    DROP COLUMN PERMISSION_ID;

--2
ALTER TABLE account.ACCOUNT_GROUP_MEMBER
ADD COLUMN ROLE_NAME VARCHAR(30) REFERENCES ROLE(NAME);

UPDATE account.ACCOUNT_GROUP_MEMBER
SET ROLE_NAME = (select r.NAME from ROLE r where ROLE_ID = r.id);

ALTER TABLE account.ACCOUNT_GROUP_MEMBER
	DROP CONSTRAINT ACCOUNT_GROUP_MEMBER_ROLE_ID_FKEY RESTRICT,
	DROP COLUMN ROLE_ID;

--3
ALTER TABLE account.ROLE
	DROP CONSTRAINT IF EXISTS ROLE_PKEY,
	DROP COLUMN IF EXISTS ID;

ALTER TABLE account.ROLE
	RENAME COLUMN NAME TO ID;

ALTER TABLE account.ROLE
	ADD CONSTRAINT ROLE_PKEY PRIMARY KEY ( ID );

ALTER TABLE account.PERMISSION
    DROP CONSTRAINT IF EXISTS PERMISSION_PKEY,
    DROP COLUMN IF EXISTS ID;

ALTER TABLE account.PERMISSION
	RENAME COLUMN NAME TO ID;

ALTER TABLE account.PERMISSION
    ADD CONSTRAINT PERMISSION_PKEY PRIMARY KEY ( ID );

--4
ALTER TABLE account.ACCOUNT_GROUP_MEMBER
	RENAME COLUMN ROLE_NAME TO ROLE_ID;

ALTER TABLE account.ROLE_PERMISSION
	RENAME COLUMN ROLE_NAME TO ROLE_ID;

ALTER TABLE account.ROLE_PERMISSION
	RENAME COLUMN PERMISSION_NAME TO PERMISSION_ID;