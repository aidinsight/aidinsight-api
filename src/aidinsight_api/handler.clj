(ns aidinsight-api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [aidinsight-api.discourse :as discourse]))


;;
;; Schemas
;;

(s/defschema ClusterMatch
  {:name       s/Str
   :confidence s/Str})

(s/defschema MatchingClusters
  {:clusters [ClusterMatch]})

(s/defschema ClusterNames
  {:clusters [String]})

(s/defschema MatchingClusters
  {:clusters [ClusterMatch]})

(def cluster-names ["food" "health" "protection"])

(s/defschema NeedMessage
  {:message String})

(s/defschema CreateNeedRequest
  {:title                            String
   :description                      String
   :mobile                           String
   (s/optional-key :clusters)        [(apply s/enum cluster-names)]
   (s/optional-key :originator-name) String
   (s/optional-key :location)        {:lat Number :long Number}})

(s/defschema ReadNeedRequest
  (assoc CreateNeedRequest :id String))

;;

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


(defn uuid [] (.toString (java.util.UUID/randomUUID)))


(defn app [config]
  (api
    {:swagger
     {:ui   "/"
      :spec "/swagger.json"
      :data {:info {:title       "Aid inSight API"
                    :description "APIs to support humanitarian relief coordination"}
             :tags [{:name "api", :description "Aid inSight APIs"}]}}}

    (context "/api" []
      :tags ["api"]

      (POST "/need/categorize" []
        :return MatchingClusters
        :body [body NeedMessage]
        :summary "categorizes a request for help"
        (ok {:clusters (categorize (-> body :message))}))

      (POST "/need" []
        :return ReadNeedRequest
        :body [body CreateNeedRequest]
        :summary "submit a need request"
        (do
          (discourse/send-need-request
            (:discourse config)
            (body :title)
            (body :description)
            (body :clusters))
          (created)))

      (GET "/clusters" []
        :return ClusterNames
        :summary "return recognized cluster names"
        (ok {:clusters cluster-names})))))
