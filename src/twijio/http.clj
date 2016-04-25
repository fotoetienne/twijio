(ns twijio.http
  (:require [cheshire.core :as json]
            [clojure.core.async :as async :refer [<! <!! >! >!! go]]
            [clojure.string :as s]
            [org.httpkit.client :as client]
            [twijio
             [api :as api]
             [lib :refer :all]]))

(defn parse-body
  "Parse json in http response body"
  [response]
  (try
    (if-let [json-body (some-> response :body (json/decode true))]
      (assoc response :body json-body)
      response)
    (catch Exception e
      response)))

(defn wrap-json-params
  [{:keys [json-params] :as request}]
  (if json-params
    (-> request
      (assoc :body (json/encode json-params))
      (assoc-in [:headers "Content-Type"] "application/json"))
    request))

(defn async-request
  "Returns a core.async channel to which an eventual response will be pushed"
  [req]
  (let [resp-chan (async/promise-chan)]
    (-> req
      wrap-json-params
      (client/request #(->> % parse-body (>!! resp-chan))))
    resp-chan))

(defn twilio-url [config & path]
  (let [twilio-base "https://api.twilio.com/2010-04-01/Accounts"
        account (:twilio-account config)]
    (str (s/join "/" (concat [twilio-base account] path)) ".json")))

(defn twilio-auth [request config]
  (assoc request :basic-auth ((juxt :twilio-account :twilio-token) config)))

(defn twilio-request!
  [{:keys [twilio-uri] :as request} config]
  (-> request
    (assoc-if :url (some->> twilio-uri (twilio-url config)))
    (twilio-auth config)
    async-request))

(defn paged-request!
  "Page through results of a twilio request using next_page_uri
  Returns a channel with a stream of results"
  [{:keys [items query-params] :as request} config]
  (let [page-size (or (get query-params "PageSize") 50)
        resp-chan (async/chan (* page-size 2))
        items (or items identity)]
    (async/go-loop [r request]
      (let [{:keys [body]} (<! (twilio-request! r config))]
        (doseq [i (items body)] (>! resp-chan i))
        (if-let [next (api/next-page body)]
          (recur next)
          (async/close! resp-chan))))
    resp-chan))
