(ns greenhouse-web.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [greenhouse-web.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[greenhouse-web started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[greenhouse-web has shut down successfully]=-"))
   :middleware wrap-dev})
