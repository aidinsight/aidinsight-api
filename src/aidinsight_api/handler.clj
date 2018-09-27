(ns aidinsight-api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema ClusterMatch
  {:name s/Str
   :confidence s/Int})


(s/defschema MatchingClusters
  {:clusters [ClusterMatch]})

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Aidinsight API"
                    :description "APIs to support humanitarian relief coordination"}
             :tags [{:name "api", :description "Aidinsight APIs"}]}}}

    (context "/api" []
      :tags ["api"]

      (POST "/categorize" []
        :return MatchingClusters
        :body [body {:text String}]
        :summary "categorizes a message for help"
        (ok {})))))
