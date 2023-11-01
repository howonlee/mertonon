(ns mtfe.generators.navs
  "Generate FE navigation things - routes, paths, etc"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mtfe.generators.net-store :as net-store]
            [mtfe.routes :as main-routes]
            [mtfe.sidebars.routes :as sidebar-routes]))

;; ---
;; Generate routes (the vec that defines the routes)
;; ---

(defn allowed-members [all-members]
  (filter
    (fn [[_ data]]
      (not (data :exclude-from-gen?)))
    all-members))

(def gen-nav-route (gen/elements (allowed-members main-routes/main-routes)))

(def gen-sidebar-route (gen/elements (allowed-members sidebar-routes/sidebar-routes)))

;; ---
;; Generate paths (the paths in the routes)
;; ---

(defn replace-path [path param-gen curr-idx]
  (reduce (fn [curr-path [to-replace [table-key table-member-key]]]
            (str/replace curr-path to-replace
                         (-> (@net-store/store table-key)
                             cycle
                             (nth curr-idx)
                             table-member-key)))
          path param-gen))

(def gen-nav-path
  (gen/let [[path params] gen-nav-route
            curr-idx      (gen/choose 0 10)]
    (let [param-gen (params :param-gen)]
      (if (some? param-gen)
        (replace-path path param-gen curr-idx)
        path))))

(def gen-sidebar-path
  (gen/let [[path params] gen-sidebar-route
            curr-idx      (gen/choose 0 10)]
    (let [param-gen (params :param-gen)]
      (if (some? param-gen)
        (replace-path path param-gen curr-idx)
        path))))

(comment
  (try
    (gen/sample gen-sidebar-path 10)
    (catch :default e (cljs.pprint/pprint e))))
