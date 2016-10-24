(ns greenhouse-web.routes.home
  (:require [greenhouse-web.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clj-http.client :as client]
            [clj-time.format :as f]
            [clj-time.coerce :as c]))

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



(defn issue-influx-query [query] (client/get url {:query-params {"db" "sensors" "q" query} :as :json})) 
(defn history-influx-query [sensor] (format "SELECT MEAN(value) FROM moisture WHERE sensor = '%s' AND time > now() - 7d GROUP BY sensor,time(1h) fill(none)" sensor))
(defn execute-influx-query [query] (get (first (get (first (get-in (issue-influx-query query) [:body :results])) :series)) :values))

(defn to-d3-format [value] {"x" (/ (c/to-long (f/parse (f/formatter "yyyy-MM-dd'T'HH:mm:ssZ") (first value))) 1000) "y" (nth value 1)})
(defn sensor-history [sensor] (map to-d3-format (execute-influx-query (history-influx-query sensor))))

(defn home-page []
  (layout/render
    "home.html" {:basil (latest-value (get sensor-mapping :basil))
                 :thyme (latest-value (get sensor-mapping :thyme))
                 :rosemary (latest-value (get sensor-mapping :rosemary))}))                

(defn about-page []
  (layout/render "about.html"))

(defn plant-history [plant]
  (layout/render
    "plant.html" {:sensor (get sensor-mapping (keyword plant))
                  :history (sensor-history (get sensor-mapping (keyword plant)))}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/history/:plant" [plant] (plant-history plant)))



