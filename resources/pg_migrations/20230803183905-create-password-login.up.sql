CREATE TABLE IF NOT EXISTS mertonon.password_login (
  uuid uuid PRIMARY KEY,
  mt_user_uuid uuid REFERENCES mertonon.mt_user(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  password_state varchar(255) NOT NULL,
  password_digest varchar(255) NOT NULL,
  recovery_token_hash varchar(255) NOT NULL
);
--;;
CREATE INDEX IF NOT EXISTS mt_user_uuid ON mertonon.mt_user(uuid);
