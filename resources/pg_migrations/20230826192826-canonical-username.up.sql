ALTER TABLE mertonon.mt_user ADD COLUMN IF NOT EXISTS canonical_username varchar(255) NOT NULL UNIQUE;
