DROP TABLE IF EXISTS mertonon.mt_session;
--;;
DROP INDEX IF EXISTS mt_user_canonical_username;
--;;
CREATE TABLE IF NOT EXISTS mertonon.session_store (
  session_id VARCHAR(36) PRIMARY KEY, -- not uuid because we're using someone else's jdbc store
  idle_timeout BIGINT,
  absolute_timeout BIGINT,
  value BYTEA
);
--;;
CREATE INDEX IF NOT EXISTS session_store_id ON mertonon.session_store(session_id);
