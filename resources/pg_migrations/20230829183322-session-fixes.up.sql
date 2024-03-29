DROP TABLE IF EXISTS mertonon.session_store;
--;;
CREATE TABLE IF NOT EXISTS mertonon.mt_session(
  uuid uuid PRIMARY KEY,
  mt_user_uuid uuid REFERENCES mertonon.mt_user(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  expires_at timestamptz NOT NULL,
  value text NOT NULL
);
--;;
CREATE INDEX IF NOT EXISTS mt_session_uuid ON mertonon.mt_session(uuid);
--;;
CREATE INDEX IF NOT EXISTS mt_user_canonical_username ON mertonon.mt_user(canonical_username);
