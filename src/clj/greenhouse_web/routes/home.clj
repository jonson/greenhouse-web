(ns greenhouse-web.routes.home
  (:require [greenhouse-web.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clj-http.client :as client]))

;todo: figure out where config should live!
(def url "http://nas.oscar:8086/query")
(def sensor-mapping (hash-map :basil "a2" :thyme "a0" :rosemary "a1"))
(defn moisture-influx-query [sensor] (format "SELECT last(value) FROM moisture where sensor = '%s'" sensor))
(defn latest-influx-value [sensor] (client/get url {:query-params {"db" "sensors" "q" (moisture-influx-query sensor)} :as :json})) 
(defn latest-value [sensor] (get 
                              (first 
                                  (get
                                    (first 
                                      (get-in (latest-influx-value sensor) [:body :results])) :series)) :values))


(defn home-page []
  (layout/render
    "home.html" {:basil (latest-value (get sensor-mapping :basil))
                 :thyme (latest-value (get sensor-mapping :thyme))
                 :rosemary (latest-value (get sensor-mapping :rosemary))}))                

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page)))


