(ns cljs.user
  "This is the one where you run the tests in repl"
  (:require [cljs.test]
            [mtfe.events-test]
            [mtfe.nav-test]
            [mtfe.ns-test]))

(comment
  (cljs.test/run-all-tests)
  (cljs.test/run-all-tests #"mtfe.*-test"))
