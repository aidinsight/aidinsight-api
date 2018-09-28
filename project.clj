(defproject aidinsight-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [metosin/compojure-api "1.1.11"]
                 [ring/ring-jetty-adapter "1.7.0"]
                 [environ "1.1.0"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.1"]
                 [com.cemerick/url "0.1.1"]]
  :ring {:handler aidinsight-api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins      [[lein-ring "0.12.0"]]}}
  :main aidinsight-api.core)
