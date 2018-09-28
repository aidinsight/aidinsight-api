(ns aidinsight-api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [aidinsight-api.discourse :as discourse]
            [aidinsight-api.watson :as watson])
  (:import (javax.xml.stream Location)))


;;
;; Schemas
;;

(s/defschema ClusterNames
  {:clusters [String]})

(s/defschema MatchingClusters
  {:clusters [String]})

(def cluster-names ["water-sanitation"
                    "coordination"
                    "health"
                    "nutrition"
                    "shelter"
                    "food-security"
                    "education"
                    "protection"
                    "logistics"
                    "emergency-telecommunications"])


(s/defschema NeedMessage
  {:message String})

(s/defschema NeedLocation
  {:name                 String
   (s/optional-key :gps) {:lat Number :long Number}})

(s/defschema CreateNeedRequest
  {:title                            String
   :description                      String
   (s/optional-key :mobile)          String
   (s/optional-key :clusters)        [(apply s/enum cluster-names)]
   (s/optional-key :contact-name) String
   (s/optional-key :location)        NeedLocation})

(s/defschema ReadNeedRequest
  (assoc CreateNeedRequest :id String))

;;

(defn cluster-match [name confidence]
  {:name name :confidence (str confidence)})

(defn regex-classify [text]
  (let [food-match (re-find #"food" text)
        health-match (re-find #"sick" text)
        protection-match (re-find #"gun" text)]
    (cond-> []
            food-match (conj (cluster-match "food" "0.88"))
            health-match (conj (cluster-match "health" "0.84"))
            protection-match (conj (cluster-match "protection" "0.76")))))

(defn categorize [config text]
  (println "TEXT" text)
  (if (:watson config)
    (do
      (println "Using Watson classifier")
      (mapv name (watson/identify-clusters (:watson config) text)))
    (do
      (println "Using regex classifier")
      (regex-classify text))))


(defn uuid [] (.toString (java.util.UUID/randomUUID)))


(defn debug-> [x message]
  (println message x)
  x)


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
        (ok {:clusters (debug-> (categorize config (-> body :message)) "categories")}))

      (POST "/need" []
        :return ReadNeedRequest
        :body [need-request CreateNeedRequest]
        :summary "submit a need request"
        (do
          (discourse/send-need-request
            (:discourse config)
            need-request)
          (created)))

      (GET "/clusters" []
        :return ClusterNames
        :summary "return recognized cluster names"
        (ok {:clusters cluster-names})))))
