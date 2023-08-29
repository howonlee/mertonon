-- This is not the canonical place for the schema. src/mertonon/generators/ is the canonical place for the schema. If it can't be generated, it doesn't go in Mertonon.

CREATE SCHEMA IF NOT EXISTS mertonon;

CREATE TABLE IF NOT EXISTS mertonon.health_check (
  uuid uuid PRIMARY KEY,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL
);
CREATE INDEX IF NOT EXISTS health_check_uuid ON mertonon.health_check(uuid);

CREATE TABLE IF NOT EXISTS mertonon.grid (
  uuid uuid PRIMARY KEY,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL,
  optimizer_type varchar(255) NOT NULL,
-------------- jsonify
  hyperparams text NOT NULL
);
CREATE INDEX IF NOT EXISTS grid_uuid ON mertonon.grid(uuid);

CREATE TABLE IF NOT EXISTS mertonon.layer (
  uuid uuid PRIMARY KEY,
  grid_uuid uuid REFERENCES mertonon.grid(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL
  );
CREATE INDEX IF NOT EXISTS layer_uuid ON mertonon.layer(uuid);
CREATE TABLE IF NOT EXISTS mertonon.cost_object (
  uuid uuid PRIMARY KEY,
  layer_uuid uuid REFERENCES mertonon.layer(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL,
  activation numeric(14, 4) NOT NULL,
  delta numeric(14, 4) NOT NULL
  );
CREATE INDEX IF NOT EXISTS cost_object_uuid ON mertonon.cost_object(uuid);
CREATE TABLE IF NOT EXISTS mertonon.weightset (
  uuid uuid PRIMARY KEY,
  src_layer_uuid uuid REFERENCES mertonon.layer(uuid) ON DELETE CASCADE,
  tgt_layer_uuid uuid REFERENCES mertonon.layer(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL
  );
CREATE INDEX IF NOT EXISTS weightset_uuid ON mertonon.weightset(uuid);
CREATE TABLE IF NOT EXISTS mertonon.weight (
  uuid uuid PRIMARY KEY,
  weightset_uuid uuid REFERENCES mertonon.weightset(uuid) ON DELETE CASCADE,
  src_cobj_uuid uuid REFERENCES mertonon.cost_object(uuid) ON DELETE CASCADE,
  tgt_cobj_uuid uuid REFERENCES mertonon.cost_object(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  label varchar(65535) NOT NULL,
  type varchar(255) NOT NULL,
  value integer NOT NULL,
  grad numeric(14, 4) NOT NULL
  );

CREATE INDEX IF NOT EXISTS weight_uuid ON mertonon.weight(uuid);
CREATE TABLE IF NOT EXISTS mertonon.loss (
  uuid uuid PRIMARY KEY,
  layer_uuid uuid REFERENCES mertonon.layer(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL,
  type varchar(255) NOT NULL,
-------------- jsonify
  data text NOT NULL
  );
CREATE INDEX IF NOT EXISTS loss_uuid ON mertonon.loss(uuid);

CREATE TABLE IF NOT EXISTS mertonon.input (
  uuid uuid PRIMARY KEY,
  layer_uuid uuid REFERENCES mertonon.layer(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL,
  type varchar(255) NOT NULL,
-------------- jsonify
  data text NOT NULL
  );
CREATE INDEX IF NOT EXISTS input_uuid ON mertonon.input(uuid);


CREATE TABLE IF NOT EXISTS mertonon.entry (
  uuid uuid PRIMARY KEY,
  cobj_uuid uuid REFERENCES mertonon.cost_object(uuid) ON DELETE CASCADE,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  name varchar(255) NOT NULL,
  label varchar(65535) NOT NULL,
  entry_date timestamptz NOT NULL,
  type varchar(255) NOT NULL,
  value numeric(14, 4) NOT NULL
-- There's no spiffy denormalized data json column here because we're absolutely sure we're gonna join on it
-- Therefore we need an entry_metadata table instead. But only when we need it
  );
CREATE INDEX IF NOT EXISTS entry_uuid ON mertonon.entry(uuid);

CREATE TABLE IF NOT EXISTS mertonon.mt_user (
  uuid uuid PRIMARY KEY,
  version integer NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  email varchar(255) NOT NULL,
  username varchar(255) NOT NULL
);
CREATE INDEX IF NOT EXISTS mt_user_uuid ON mertonon.mt_user(uuid);

---- Deprecated August 29
CREATE TABLE IF NOT EXISTS mertonon.session_store (
  session_id VARCHAR(36) PRIMARY KEY, -- not uuid because we're using someone else's jdbc store
  idle_timeout BIGINT,
  absolute_timeout BIGINT,
  value BYTEA
);
CREATE INDEX IF NOT EXISTS session_store_id ON mertonon.session_store(session_id);

---- Remaining bits to spread out into migrations:
---- documents
---- state machines
---- audit log
---- archive (undoable delete bits)
---- buncha tables for rbac, with aforethought to getting it to work w abac
---- search
---- bookmark
