(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

;; Build checklist
;; 1. Run BE tests, make sure they're green
;; 2. Run FE tests, make sure they're green
;; 3. `cljfmt fix` things
;; 4. Incr app version in this file
;; 5. Incr app version in package.json
;; 6. Iff you futzed with the readme, hit it with the Flesch-Kincaid (using `style` and keep it under 8 for first 20 lines)
;; 7. Futz with changelog
;; 8. Land change PR
;; 9. Change working branch to master
;; 10. Pull from remote master
;; 11. Build frontend release with `yarn release` todo: why does this take 2 stinkin minutes
;; 12. Build this with `clj -T:build-ce uberjar`
;; 13. Upload to github and cut github release
;; 14. Kick off a website build to change download page
;; 15. Upload website

;; Proper ci/cd coming when it's coming
(def lib 'com.github.howonlee/mertonon)
(defn- the-version [patch] (format "0.14.%s" patch))
(def version (the-version (b/git-count-revs nil)))
(def build-folder "target")
(def class-dir (str build-folder "/classes"))

(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file-name (format "%s/%s-mertonon-postprealpha-standalone.jar" build-folder version))

(def uber-file-opts
  {:lib       lib
   :version   version
   :uber-file uber-file-name
   :scm       {:tag (str "v" version)}
   :basis     (b/create-basis {:project "deps.edn"})
   :class-dir class-dir
   :target    "target"
   :src-dirs  ["src"]
   :src-pom   "template/pom.xml"
   :main      'mertonon.core})

;; to run: clj -T:build-ce show-version
(defn show-version [_]
  (println (format "\n%s" version)))

;; to run: clj -T:build-ce clean
(defn clean [_]
  (b/delete {:path build-folder}))

;; to run: clj -T:build-ce uberjar
(defn uberjar [_]
  (clean nil)
  (println "\nWriting pom.xml...")
  (b/write-pom uber-file-opts)
  (println "\nCopying source...")
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (println "\nCompiling source...")
  (b/compile-clj uber-file-opts)
  (println "\nMaking uberjar...")
  (b/uber uber-file-opts)

  (println (format "Uber file created: \"%s\"" uber-file-name)))
