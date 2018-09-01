(ns car-data-clj.web-setup
  (:require [clojure.test :refer :all]
            [car-data-clj.rest.car-data-server :refer [app]]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [hugsql.core :refer [db-run]]
            [car-data-clj.db.postgresql :refer [db]]))

(def resp (atom nil))

(def owner "foo@bar.com")

(def ^:private req-fn (atom nil))

(defn req [method uri body]
  (-> (mock/request method uri)
      (mock/content-type "application/json; charset=utf-8")
      (mock/body (json/generate-string body))))

(defn auth-req [method uri body]
  (mock/header (req method uri body) "Authorization" "Bearer 00000000-0000-0000-0000-000000000000"))

(defn set-authorized-requests! []
  (reset! req-fn auth-req))

(defn set-unauthorized-requests! []
  (reset! req-fn req))

(defn web-run
  ([req-fn method uri body] (reset! resp (app (req-fn method uri body))))
  ([method uri body] (web-run @req-fn method uri body))
  ([method uri] (web-run method uri nil)))

(defn extract-body []
  (if-let [body (try (json/decode (:body @resp) keyword)
                     (catch Exception _))]
    body
    (:body @resp)))

(defn setup-session []
  (db-run db (str "INSERT INTO users (login, password, salt) VALUES"
                  "('" owner "', '3bb95188c01763e81875ce9644f496a4e3f98d9eb181c5f128ba32a01b62b6de', 'bar');"))
  (db-run db (str "INSERT INTO sessions (id, login, seen) VALUES"
                  "('00000000-0000-0000-0000-000000000000', '" owner "', now());")))

(defn clear-users [] (db-run db "TRUNCATE users CASCADE"))

(defn clear-car-trips [] (db-run db "TRUNCATE car_trips CASCADE"))

(defn clear-ks []
  (clear-users)
  (clear-car-trips))