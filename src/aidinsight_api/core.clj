(ns aidinsight-api.core
  [:require [aidinsight-api.handler :refer [app]]
            [ring.adapter.jetty :refer :all]
            [environ.core :as env]
            [clojure.tools.reader.edn :as edn]
            [clojure.java.io :as io]])

(defn -main [& args]
  (if-let [config-file (first args)]
    (let [config (-> config-file io/file slurp edn/read-string)]
      (run-jetty
        (app config)
        {:port (or (env/env :port) 8080)}))
    (println "Specify EDN config file")))

