(ns aidinsight-api.watson
  (:require [cheshire.core :as ch]
            [clj-http.client :as http]
            [cemerick.url :refer [url-encode]]))

(defn- with-parsed-body [response]
  (update-in response [:body] ch/parse-string true))

;; TODO Check that text is within maximum size of 2048 characters
(defn- classify [{:keys [username password api-base-url]} classifier-id text]
  (let [url (str
              api-base-url
              "/v1/classifiers/"
              classifier-id
              "/classify?text="
              (url-encode text))
        _ (println "URL" url)]
    (-> (http/get
          url
          {:basic-auth [username password]})
        with-parsed-body)))

(defn- theme->clusters [theme]
  (println "THEME" theme)
  (let [mapping {"Recovery and Reconstruction"      [:shelter :early-recovery :logistics]
                 "Water Sanitation Hygiene"         [:water-sanitation]
                 "Coordination"                     [:coordination]
                 "Health"                           [:health :nutrition]
                 "Shelter and Non-Food Items",      [:shelter]
                 "Food and Nutrition"               [:food-security :nutrition]
                 "Education"                        [:education]
                 "Safety and Security"              [:protection :logistics]
                 "Logistics and Telecommunications" [:emergency-telecommunications]
                 "Protection and Human Rights"      [:protection]}]
    (get mapping theme [])))

(defn- top-classes [watson-config text]
  (map
    #(-> (classify watson-config % text)
         :body
         :top_class)
    (watson-config :nlc-ids)))

(defn identify-clusters [watson-config text]
  (distinct (mapcat theme->clusters (top-classes watson-config text))))

(comment


  (def temp-config
    {:username     "<username>"
     :password     "<password>"
     :api-base-url "https://gateway.watsonplatform.net/natural-language-classifier/api"})

  (create-classifier temp-config "resources/classifier_1.csv" "ReliefWebClassifier1")
  (create-classifier temp-config "resources/classifier_2.csv" "ReliefWebClassifier2")

  (clojure.pprint/print (list-classifiers temp-config))

  (clojure.pprint/pprint (get-classifier temp-config "f33041x451-nlc-442"))
  (clojure.pprint/pprint (get-classifier temp-config "f33041x451-nlc-450"))

  )

