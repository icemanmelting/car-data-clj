(ns car-data-clj.rest.positions-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all])
  (:import (java.util Date)))

(def ^:private car-id (uuid))

(def-db-fns "cars.sql")

(def-db-fns "car_positions.sql")

(defn- populate-positions []
  (create-car db {:id car-id :owner owner :cnst_km 3000 :trip_km 10})
  (create-position db {:id (uuid) :car_id car-id :pos_lat 3.1 :pos_lon 4.4})
  (Thread/sleep 1000)
  (create-position db {:id (uuid) :car_id car-id :pos_lat 3.3 :pos_lon 4.1}))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (populate-positions)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest correct-latest-position

  (testing "correct values returned"

    (do (set-authorized-requests!)
        (web-run :get (str "/cars/" car-id "/positions")))

    (let [{:keys [id pos_lat pos_lon]} (extract-body)]

      (are [x y] (= x y)
                 3.3 pos_lat
                 4.1 pos_lon))))

(deftest last-2-positions

  (testing "correct values returned"

    (let [ts (- (.getTime (Date.)) 3000)]
      (do (set-authorized-requests!)
          (web-run :get (str "/cars/" car-id "/positions/" ts)))

      (let [body (extract-body)]

        (is (= 2 (count body)))))))
