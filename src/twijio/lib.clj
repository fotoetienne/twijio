(ns twijio.lib
  (:import java.time.format.DateTimeFormatter
           java.time.Instant)
  (:require
   [clojure.core.async :as async :refer [<! <!! >! >!! go]]))

(defn assoc-if
  "assoc only if the provided value is true"
  ([coll k v]
   (if v
     (assoc coll k v)
     coll))
  ([coll k v & kvs]
   (assoc-if (apply assoc-if coll kvs)
             k v)))

(defn url-encode [s]
  (some-> s str (java.net.URLEncoder/encode "UTF-8") (.replace "+" "%20")))

(defn chan->seq
  "empty a channel into a sequence. Blocks until channel is closed."
  [channel]
  (take-while some? (repeatedly #(<!! channel))))

(defn chan->vec [channel] (into [] (chan->seq channel)))

(defn add-xform [xform channel] (async/pipe channel (async/chan xform)))

(defn rfc2822->instant [t]
  (->> t (.parse DateTimeFormatter/RFC_1123_DATE_TIME) Instant/from))
