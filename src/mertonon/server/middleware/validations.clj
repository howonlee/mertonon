(ns mertonon.server.middleware.validations
  "Takes a bunch of validations with their own semantics and applies them all to a request"
  (:require [reitit.core :as r]))