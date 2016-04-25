(ns twijio.api
  "Twilio Rest Api.
  Functions create ring request objects for calls to the Twilio rest api.
  https://www.twilio.com/docs/api/rest"
  (:require
   [clojure.string :as s]
   [twijio.lib :refer :all]))


;; Messages
;; https://www.twilio.com/docs/api/rest/message

(defn send-message
  "https://www.twilio.com/docs/api/rest/sending-messages"
  [{:keys [to from service body media-url
           status-callback application-sid max-price]}]
  {:method :post
   :twilio-uri "Messages"
   :form-params (assoc-if {"To" to} ;; Required
                          "From" from ;; Required if no service
                          "MessagingServiceSid" service ;; Required if no from
                          "Body" body ;; Required is no media-url
                          "MediaUrl" media-url ;; Required if no body
                          "StatusCallback" status-callback
                          "ApplicationSid" application-sid
                          "MaxPrice" max-price)})

(defn get-message
  "https://www.twilio.com/docs/api/rest/message#instance-get"
  [message-sid]
  {:method :get
   :twilio-uri (str "Messages/" message-sid)})

(defn get-messages
  "https://www.twilio.com/docs/api/rest/message#list-get"
  [{:keys [to from date-sent date-sent> date-sent<]}]
  {:method :get
   :twilio-uri "Messages"
   :items :messages
   :query-params
   (assoc-if {}
             "To" to
             "From" from
             "DateSent" date-sent
             "DateSent>" date-sent>
             "DateSent<" date-sent<)})


;; Incoming Phone Numbers
;; https://www.twilio.com/docs/api/rest/incoming-phone-numbers

(defn get-incoming-phone-number
  "https://www.twilio.com/docs/api/rest/incoming-phone-numbers#instance-get"
  [number-sid]
  {:method :get
   :twilio-uri (str "IncomingPhoneNumbers/" number-sid)})

(defn update-incoming-phone-number
  "https://www.twilio.com/docs/api/rest/incoming-phone-numbers#instance-post"
  [number-sid params]
  {:method :put
   :twilio-uri (str "IncomingPhoneNumbers/" number-sid)
   :query-params params})

(defn get-incoming-phone-numbers
  "https://www.twilio.com/docs/api/rest/incoming-phone-numbers#list"
  [{:keys [number friendly-name number-type]}]
  {:method :get
   :twilio-uri (s/join "/" ["IncomingPhoneNumbers" number-type])
   :items :incoming_phone_numbers
   :query-params (assoc-if {}
                           "PhoneNumber" number
                           "FriendlyName" friendly-name)})

(defn buy-incoming-phone-number
  "https://www.twilio.com/docs/api/rest/incoming-phone-numbers#list-post"
  [number]
  {:method :post
   :twilio-uri "IncomingPhoneNumbers"
   :form-params {"PhoneNumber" number}})

(defn release-incoming-phone-number
  "https://www.twilio.com/docs/api/rest/incoming-phone-numbers#instance-delete"
  [number-sid]
  {:method :delete
   :twilio-uri (str "IncomingPhoneNumbers/" number-sid)})


;; Lookup API

(defn lookup
  "https://www.twilio.com/docs/api/lookups"
  [number country-code]
  {:method :get
   :url (str "https://lookups.twilio.com/v1/PhoneNumbers/" (url-encode number))
   :query-params {"CountryCode" country-code}})


;; Paging

(defn next-page [{:keys [next_page_uri] :as body}]
  (when next_page_uri
    {:method :get, :url (str "https://api.twilio.com" next_page_uri)}))
