(ns car-data-clj.rest.speed-data-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private car-id (uuid))

(def ^:private trip-id-1 (uuid))

(def ^:private trip-id-2 (uuid))

(def-db-fns "cars.sql")

(def-db-fns "car_data.sql")

(def-db-fns "car_trips.sql")

(defn- new-speed-data [id trip-id speed rpm gear]
  (create-speed-data db {:id id
                         :trip_id trip-id
                         :speed speed
                         :rpm rpm
                         :gear gear}))

(defn- populate-trip []
  (create-car db {:id car-id :owner owner :cnst_km 3000 :trip_km 10})
  (insert-car-trip db {:id trip-id-1 :car_id car-id :starting_km 0})
  (insert-car-trip db {:id trip-id-2 :car_id car-id :starting_km 0}))

(defn- populate-speed []
  (new-speed-data (uuid) trip-id-1 50 3000 3)
  (new-speed-data (uuid) trip-id-1 60 2400 4)
  (new-speed-data (uuid) trip-id-2 120 3000 5))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (populate-trip)
  (populate-speed)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest logs-present

  (testing "correct values returned trip 1"

    (do (set-authorized-requests!)
        (web-run :get (str "/car/trips/" trip-id-1 "/speed")))

    (let [body (extract-body)
          speed-data-count (count body)
          sorted-by-date (sort-by :ts body)]

      (are [x y] (= x y)
                 2 speed-data-count
                 (first body) (first sorted-by-date)
                 (second body) (second sorted-by-date))))

  (testing "correct values returned trip 2"

    (do (set-authorized-requests!)
        (web-run :get (str "/car/trips/" trip-id-2 "/speed")))

    (let [body (extract-body)
          speed-data-count (count body)]

      (is (= 1 speed-data-count)))))
