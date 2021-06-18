(ns mertonon.util.config
  "Configutation dealing")

(defn db-spec
  "Details for DB connection"
  []
  (let [db-type  (or (System/getenv "MT_DB_TYPE") "postgres")
        host     (or (System/getenv "MT_DB_HOST") "localhost")
        port     (Integer/parseInt (or (System/getenv "MT_DB_PORT") "5432"))
        username (or (System/getenv "MT_DB_USER") "postgres")
        password (or (System/getenv "MT_DB_PASS") "")
        ;; we don't actually have customizable schema name right now.
        ;; confusingly, I guess, schema is hardcoded to mertonon
        ;; there are multiple schemas per DB in postgres
        db-name  (or (System/getenv "MT_DB_NAME") "mertonon")
        res      {:dbtype   db-type
                  :host     host
                  :port     port
                  :username username
                  :password password
                  :dbname   db-name}]
    res))

(defn feature-flags
  []
  {})

(defn config
  "Main config. Should refresh only when kicked off manually or if restarted

  TODO: Easy naive cache
  TODO: introspect on whether we're running from jar instead of envvar for whether we're in prod or not"
  []
  (let [mt-env-mode (keyword (or (System/getenv "MT_ENV_MODE") "production"))
        mt-host     (or (System/getenv "MT_HOST") "localhost")
        mt-port     (Integer/parseInt (or (System/getenv "MT_PORT") "5036"))
        res         {:env-mode      mt-env-mode
                     :mt-host       mt-host
                     :mt-port       mt-port
                     :feature-flags (feature-flags)}]
    res))

(defn host-url [curr-config]
  (format "http://%s:%d" (curr-config :mt-host) (curr-config :mt-port)))
