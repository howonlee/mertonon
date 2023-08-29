ALTER TABLE mertonon.session_store
DROP COLUMN session_id,
DROP COLUMN idle_timeout,
DROP COLUMN absolute_timeout,
DROP COLUMN value,
ADD COLUMN uuid uuid PRIMARY KEY,
ADD COLUMN version integer NOT NULL,
ADD COLUMN created_at timestamptz NOT NULL,
ADD COLUMN expires_at timestamptz NOT NULL,
ADD COLUMN value text NOT NULL;
