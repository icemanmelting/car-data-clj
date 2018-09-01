(ns car-data-clj.rest.cars-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private car-id (uuid))

(def-db-fns "cars.sql")

(defn- new-settings [id cnst-km trip-km]
  (create-car db {:id id
                  :cnst_km cnst-km
                  :trip_km trip-km}))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest car-present

  (testing "correct values returned"

    (new-settings car-id 99999 1000)

    (do (set-authorized-requests!)
        (web-run :get (str "/cars/" car-id)))

    (let [{:keys [id constant_kilometers trip_kilometers] :as body} (extract-body)]

      (are [x y] (= x y)
                 id (str car-id)
                 trip_kilometers 1000.0
                 constant_kilometers 99999.0))))
