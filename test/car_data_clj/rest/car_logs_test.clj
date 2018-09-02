(ns car-data-clj.rest.car-logs-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private car-id (uuid))

(def ^:private trip-id-1 (uuid))

(def ^:private trip-id-2 (uuid))

(def-db-fns "cars.sql")

(def-db-fns "car_logs.sql")

(def-db-fns "car_trips.sql")

(defn- new-car-log [id trip-id msg log-level]
  (create-log db {:id id
                  :trip_id trip-id
                  :msg msg
                  :log_level log-level}))

(defn- populate-trip []
  (create-car db {:id car-id :owner owner :cnst_km 3000 :trip_km 10})
  (insert-car-trip db {:id trip-id-1 :car_id car-id :starting_km 0})
  (insert-car-trip db {:id trip-id-2 :car_id car-id :starting_km 0}))

(defn- populate-logs []
  (new-car-log (uuid) trip-id-1 "message1" "ERROR")
  (new-car-log (uuid) trip-id-1 "message2" "ERROR")
  (new-car-log (uuid) trip-id-2 "message3" "ERROR"))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (populate-trip)
  (populate-logs)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest logs-present

  (testing "correct values returned"

    (do (set-authorized-requests!)
        (web-run :get (str "/trips/" trip-id-1 "/logs")))

    (let [body (extract-body)
          logs-count (count body)
          sorted-by-date (sort-by :ts body)]

      (are [x y] (= x y)
                 2 logs-count
                 (first body) (first sorted-by-date)
                 (second body) (second sorted-by-date))))

  (testing "correct values returned"

    (do (set-authorized-requests!)
        (web-run :get (str "/trips/" trip-id-2 "/logs")))

    (let [body (extract-body)
          logs-count (count body)
          sorted-by-date (sort-by :ts body)]

      (are [x y] (= x y)
                 1 logs-count
                 (first body) (first sorted-by-date)
                 (second body) (second sorted-by-date)))))
