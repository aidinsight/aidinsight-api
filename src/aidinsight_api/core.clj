(ns aidinsight-api.core
  [:require [aidinsight-api.handler :refer [app]]
            [ring.adapter.jetty :refer :all]
            [environ.core :as env]])

(defn -main []
  (run-jetty
    app
    {:port (or (env/env :port) 8080)}))

