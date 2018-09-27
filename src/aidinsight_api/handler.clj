(ns aidinsight-api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))


(s/defschema ClusterMatch
  {:name s/Str
   :confidence s/Str})


(s/defschema MatchingClusters
  {:clusters [ClusterMatch]})

(defn cluster-match [name confidence]
  {:name name :confidence (str confidence)})

(defn categorize [text]
  (let [food-match (re-find #"food" text)
        health-match (re-find #"sick" text)
        protection-match (re-find #"gun" text)]
    (cond-> []
            food-match (conj (cluster-match "food" "0.88"))
            health-match (conj (cluster-match "health" "0.84"))
            protection-match (conj (cluster-match "protection" "0.76")))))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Aid inSight API"
                    :description "APIs to support humanitarian relief coordination"}
             :tags [{:name "api", :description "Aid inSight APIs"}]}}}

    (context "/api" []
      :tags ["api"]

      (POST "/categorize" []
        :return MatchingClusters
        :body [body {:text String}]
        :summary "categorizes a message for help"
        (ok {:clusters (categorize (-> body :text))})))))
