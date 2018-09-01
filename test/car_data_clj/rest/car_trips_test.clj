(ns car-data-clj.rest.car-trips-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private car-id (uuid))

(def ^:private trip-id (uuid))

(def-db-fns "cars.sql")

(def-db-fns "car_trips.sql")

(defn- populate-trips []
  (create-car db {:id car-id :owner owner :cnst_km 3000 :trip_km 10})
  (insert-car-trip db {:id trip-id :car_id car-id :starting_km 0})
  (insert-car-trip db {:id (uuid) :car_id car-id :starting_km 0}))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (populate-trips)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest logs-present

  (testing "correct values returned"

    (do (set-authorized-requests!)
        (web-run :get (str "/car/trips/" trip-id)))

    (let [{:keys [id starting_km]} (extract-body)]

      (are [x y] (= x y)
                 (str trip-id) id
                 0.0 starting_km))))