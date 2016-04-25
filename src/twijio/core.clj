(ns twijio.core
  (:require
   [clojure.core.async :as async :refer [<! <!! >! >!! go]]
   [twijio
    [lib :refer :all]
    [api :as api]
    [http :refer [twilio-request!] :as http]]))

(defn lookup! [number country-code config]
  (-> (api/lookup number country-code)
      (twilio-request! config)))

(defn send-sms!
  "Send sms via twilio"
  [from to body config]
  (-> (api/send-message {:from from :to to :body body})
      (twilio-request! config)))

(defn get-messages
  "Pages twilio message history; returns a channel containing all messages"
  [config args]
  (http/paged-request! (api/get-messages args) config))

(defn get-messages'
  "Combine results of multiple get-messages queries.
  Returns a combined channel of messages."
  [config args']
  (-> (map (partial get-messages config) args')
    (async/merge 1000)))
