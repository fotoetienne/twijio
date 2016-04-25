(ns twijio.core-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as async :refer [<! <!! >! >!! go]]
            [twijio
             [core :refer :all]
             [http :as http]
             [api :as api]]))

(def test-config
  {:twilio-account (System/getenv "TWILIO_TEST_ACCOUNT")
   :twilio-token (System/getenv "TWILIO_TEST_TOKEN")})

(defn test-request! [req] (<!! (http/twilio-request! req test-config)))

(deftest ^:integration credentials-test
  (testing "Make sure there are credentials in the env"
    (is (some? (:twilio-account test-config)))
    (is (some? (:twilio-token test-config)))))

(deftest ^:integration buy-phone-number-test
  (testing "valid phone number"
    (is (= 201 (-> "+15005550006" api/buy-incoming-phone-number
                 test-request! :status))))
  (testing "invalid phone number"
    (is (= 21421 (-> "+15005550001" api/buy-incoming-phone-number
                   test-request! :body :code))))
  (testing "unavailable phone number"
    (is (= 21422 (-> "+15005550000" api/buy-incoming-phone-number
                     test-request! :body :code)))))

(deftest ^:integration send-message-test
  (testing "valid numbers"
    (is (= 201 (-> {:from "+15005550006" :to "+14108675309" :body "test"}
                 api/send-message test-request! :status))))
  (testing "invalid from number"
    (is (= 21212 (-> {:from "+15005550001" :to "+14108675309" :body "test"}
                   api/send-message test-request! :body :code))))
  (testing "from number can't send"
    (is (= 21606 (-> {:from "+15005550007" :to "+14108675309" :body "test"}
                   api/send-message test-request! :body :code))))
  (testing "to number is invalid"
    (is (= 21211 (-> {:from "+15005550006" :to "+15005550001" :body "test"}
                   api/send-message test-request! :body :code)))))
