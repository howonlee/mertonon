(ns mertonon.util.migrations
  "Deals with Migratus migrations"
  (:require [mertonon.util.config :as mt-config]
            [migratus.core :as migratus]))

(def config {:store                :database
             :migration-dir        "pg_migrations/"
             :init-script          "pg_init.sql"
             :migration-table-name "mertonon.mt_migrations"
             :db                   (mt-config/db-spec)})
