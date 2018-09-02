(ns car-data-clj.rest.temp-data-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private car-id (uuid))

(def ^:private trip-id-1 (uuid))

(def ^:private trip-id-2 (uuid))

(def-db-fns "cars.sql")

(def-db-fns "car_data.sql")

(def-db-fns "car_trips.sql")

(defn- new-temp-data [id trip-id val]
  (create-temperature-data db {:id id
                               :trip_id trip-id
                               :val val}))

(defn- populate-trip []
  (create-car db {:id car-id :owner owner :cnst_km 3000 :trip_km 10})
  (insert-car-trip db {:id trip-id-1 :car_id car-id :starting_km 0})
  (insert-car-trip db {:id trip-id-2 :car_id car-id :starting_km 0}))

(defn- populate-temp []
  (new-temp-data (uuid) trip-id-1 50)
  (new-temp-data (uuid) trip-id-1 60)
  (new-temp-data (uuid) trip-id-2 120))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (populate-trip)
  (populate-temp)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest logs-present

  (testing "correct values returned trip 1"

    (do (set-authorized-requests!)
        (web-run :get (str "/trips/" trip-id-1 "/temperature")))

    (let [body (extract-body)
          temp-data-count (count body)
          sorted-by-date (sort-by :ts body)]

      (are [x y] (= x y)
                 2 temp-data-count
                 (first body) (first sorted-by-date)
                 (second body) (second sorted-by-date))))

  (testing "correct values returned trip 2"

    (do (set-authorized-requests!)
        (web-run :get (str "/trips/" trip-id-2 "/temperature")))

    (let [body (extract-body)
          temp-data-count (count body)]

      (is (= 1 temp-data-count)))))
