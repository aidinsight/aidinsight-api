(ns aidinsight-api.discourse
  [:require [clj-http.client :as http]
            [clojure.string :as str]
            [cheshire.core :as ch]])

(def ^:private need-request-topic-id 15)

(def base-url "https://discourse.aidinsight.network")

(defn- build-message [message clusters]
  (if (seq clusters)
    (let [cluster-tags (map #(str "@" %) clusters)]
      (str message " " (str/join " " cluster-tags)))
    message))

(defn- with-parsed-body [response]
  (if (some #{(response :status)} [200])
    (update-in response [:body] ch/parse-string true)
    response))


(defn list-categories [config]
  (-> (http/get
        (str base-url "/categories.json?api_username=system&api_key=" (config :api-key)))
      with-parsed-body))

(defn- category-slug->id [slug categories]
  (->> categories
       (filter #(= (:slug %) slug))
       first
       :id))

(defn send-need-request [config title description clusters]
  (println "config" config "title" title "description" description "clusters" clusters)
  (let [categories (-> (list-categories config) :body :category_list :categories)]
    (-> (http/post
          (str base-url "/posts")
          {:headers          {"Content-Type" "multipart/form-data"}
           :form-params      {:category     (category-slug->id "need-requests" categories)
                              :title        (str title ": " description)
                              :raw          (build-message description clusters)
                              :api_key      (config :api-key)
                              :api_username "system"}
           :throw-exceptions false})
        with-parsed-body)))



(comment

  API Key User
  bf4af4aa0ad3df6cea362addcf355c541e0452558beafc52e10f69c09f1c2792

  (def discourse-config {:api-key "bf4af4aa0ad3df6cea362addcf355c541e0452558beafc52e10f69c09f1c2792"})
  (send-need-request discourse-config "Incoming SMS" "Help I need food and water xxasda asdas" ["food"])
  (clojure.pprint/pprint (list-categories discourse-config))
  )