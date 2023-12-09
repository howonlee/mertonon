#!/usr/bin/env bb
(require '[babashka.http-client :as http])
(require '[cheshire.core :as json])
(require '[clojure.java.io :as io])
(require '[hiccup2.core :as hc])
(require '[markdown.core :as md])
(require '[selmer.parser :as selmer])

;; ---
;; Partials and templates
;; ---

(defn title []
  [:head
   (hc/raw "<!-- Do not edit the html directly, edit the site.clj file then have it pop out the html-->")
   [:title "Mertonon - Neural Organizational Management"]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:link {:type "text/css"
           :href "https://unpkg.com/tachyons/css/tachyons.min.css"
           :rel "stylesheet"}]
   [:link {:type "text/css"
           :href "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css"
           :rel "stylesheet"}]])

(defn nav []
  [:div.w-100.flex.items-left.justify-left.w-100.bg-black.white.f2
   [:span.w-40.pa1.v-mid [:a.link.dim.white {:href "./index.html"} "Mertonon"]]
   [:span.w-20.pa2]
   [:span.w-40.pa2.tr
    [:a.link.dim.white.f5.v-mid.pa2.ma3 {:href "./blog_index.html"}
     "Blog"]
    [:a.link.dim.white.f5.v-mid.pa2.ma3 {:href "./download.html"}
     "Download"]
    [:a.link.dim.white.f5.v-mid.pa2.ma3 {:href "./contact.html"}
     "Contact Us"]
    [:a.white {:href "https://github.com/howonlee/mertonon"}
     [:i.fa-brands.fa-github]]]])

(defn hero [blurb]
  [:div.v-mid.center.pa4
   [:h1.f1.lh-title blurb]])

(defn md-body
  "This is a very stupid idea for a dynamic site and perfectly OK if and only if we only compile before uploading always"
  [path]
  (let [md-string (slurp (clojure.core/format "./%s" path))]
    (hc/raw (md/md-to-html-string md-string))))

(defn md-body-template
  "md-body with a template"
  [path assns]
  (let [md-string        (slurp (clojure.core/format "./%s" path))
        formatted-md-str (selmer/render md-string assns)]
    (hc/raw (md/md-to-html-string formatted-md-str))))

(defn page [hero body]
  [:html.avenir.bg-mid-gray.white
   (title)
   (nav)
   [:body.w-100
    hero
    [:div.w-60.ma3.center.lh-copy.f4-l.f3-m.f2
     body]]])

(def latest-zip-download-url
  (let [http-res (http/get "https://api.github.com/repos/howonlee/mertonon/releases/latest")
        asset-res   (first ((json/parse-string (http-res :body)) "assets"))
        url         (asset-res "browser_download_url")]
    url))

;; ---
;; Pages
;; ---

(defn blog-index-page []
  (page (hero "Mertonon Blog") (md-body "pages/blog_index.md")))

(defn contact-page []
  (page (hero "Contact Us") (md-body "pages/contact.md")))

(defn download-page []
  (page (hero "Download Mertonon") (md-body-template "pages/download.md" {:dlurl latest-zip-download-url})))

(defn index-page []
  (page (hero "Mertonon - Neural Organizational Management") (md-body "pages/index.md")))

(defn estimation-blog-page []
  (page (hero "") (md-body "blog/estimates.md")))

(def page-map {"blog_index.html"      blog-index-page
               "contact.html"         contact-page
               "download.html"        download-page
               "index.html"           index-page
               "blog_estimation.html" estimation-blog-page})

;; ---
;; Webpage creation
;; ---

(doall (for [[curr-page-name curr-page-fn] page-map]
         (with-open [wrtr (io/writer (clojure.core/format "./to_upload/%s" curr-page-name))]
           (.write wrtr (str (hc/html (curr-page-fn)))))))
