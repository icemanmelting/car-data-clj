(ns car-data-clj.web-setup
  (:require [clojure.test :refer :all]
            [car-data-clj.rest.car-data-server :refer [app]]
            [ring.mock.request :as mock]
            [cheshire.core :as json]))

(def resp (atom nil))

(defn web-run [method uri]
  (reset! resp (app (-> (mock/request method uri)
                        (mock/content-type "application/json; charset=utf-8")))))

(defn extract-body []
  (if-let [body (try (json/decode (:body @resp) keyword)
                     (catch Exception _))]
    body
    (:body @resp)))
