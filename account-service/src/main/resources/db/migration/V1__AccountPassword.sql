ALTER TABLE account.ACCOUNT ADD COLUMN IF NOT EXISTS PASSWORD VARCHAR(80);
ALTER TABLE account.ACCOUNT ADD COLUMN IF NOT EXISTS PASSWORD_QUESTION VARCHAR(100);
ALTER TABLE account.ACCOUNT ADD COLUMN IF NOT EXISTS PASSWORD_ANSWER VARCHAR(100);
ALTER TABLE account.ACCOUNT DROP COLUMN IF EXISTS EXTERNAL_ID;