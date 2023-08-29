ALTER TABLE mertonon.session_store
DROP COLUMN uuid,
DROP COLUMN version,
DROP COLUMN created_at,
DROP COLUMN expires_at,
DROP COLUMN value,
ADD COLUMN session_id VARCHAR(36) PRIMARY KEY,
ADD COLUMN idle_timeout BIGINT,
ADD COLUMN absolute_timeout BIGINT,
ADD COLUMN value BYTEA;
