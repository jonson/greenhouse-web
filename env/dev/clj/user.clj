(ns user
  (:require [mount.core :as mount]
            greenhouse-web.core))

(defn start []
  (mount/start-without #'greenhouse-web.core/repl-server))

(defn stop []
  (mount/stop-except #'greenhouse-web.core/repl-server))

(defn restart []
  (stop)
  (start))


