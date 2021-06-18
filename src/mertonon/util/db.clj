(ns mertonon.util.db
  "Deal with DB stuff"
  (:require [honey.sql :as sql]
            [next.jdbc :as jdbc]
            ;; Just requiring date-time namespace mutates param protocols to extend
            [next.jdbc.date-time :as date-time]
            [next.jdbc.connection :as connection]
            [next.jdbc.sql :as jdbc-sql]
            [mertonon.util.config :as mt-config])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource PooledDataSource)))

(defn env-connpool
  []
  (let [db-spec  (mt-config/db-spec)
        pool     (connection/->pool com.mchange.v2.c3p0.ComboPooledDataSource db-spec)
        ;; side-effecting validation check
        ;; otherwise, invalid connection spec will get caught lazily, not eagerly
        check!   (with-open [conn (jdbc/get-connection pool)]
                   (.close conn))]
    pool))

(defonce ^:private connpool-singleton*
  (atom (env-connpool)))

(defn connpool-singleton "Gets the only connection pool instance" []
  @connpool-singleton*)

(def ^:dynamic *defined-connection*
  "Bindable variable for defining a specific connection to use for querying

  Do not bind this willy-nilly. Try to bind this for testing only"
  nil)

(defn query [q]
  (if (some? *defined-connection*)
    (jdbc-sql/query *defined-connection* (sql/format q))
    (with-open [conn (jdbc/get-connection (connpool-singleton))]
      (jdbc-sql/query conn (sql/format q)))))
