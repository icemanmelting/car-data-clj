(ns car-data-clj.rest.car-logs-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private trip-id (uuid))

(def-db-fns "car_logs.sql")
(def-db-fns "car_trips.sql")

(defn- new-car-log [id trip-id msg log-level]
  (create-log db {:id id
                  :trip_id trip-id
                  :msg msg
                  :log_level log-level}))

(defn- clear-ks []
  (clear-car-trips db))

(defn- populate-trip []
  (insert-car-trip db {:id trip-id :starting_km 0}))

(defn clear-ks-fixture [f]
  (clear-ks)
  (populate-trip)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest logs-present

  (testing "correct values returned"

    (new-car-log (uuid) trip-id "message1" "ERROR")
    (Thread/sleep 1000)
    (new-car-log (uuid) trip-id "message2" "ERROR")
    (new-car-log (uuid) (uuid) "message3" "ERROR")

    (web-run :get (str "/car-logs/trips/" trip-id))

    (let [body (extract-body)
          trips-count (count body)
          sorted-by-date (sort-by :ts body)]

      (are [x y] (= x y)
                 2 trips-count
                 (first body) (first sorted-by-date)
                 (second body) (second sorted-by-date)))))
