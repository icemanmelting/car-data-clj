(ns car-data-clj.rest.car-settings-test
  (:require [clojure.test :refer :all]
            [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.web-setup :refer :all]))

(def ^:private settings-id (uuid))

(def-db-fns "car_settings.sql")

(defn- new-settings [id cnst-km trip-km]
  (create-settings db {:id id
                       :cnst_km cnst-km
                       :trip_km trip-km}))

(defn- clear-ks []
  (clear-car-settings db))

(defn clear-ks-fixture [f]
  (clear-ks)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest settings-present

  (testing "correct values returned"

    (new-settings settings-id 99999 1000)

    (web-run :get (str "/car/car-settings/" settings-id))

    (let [{:keys [id constant_kilometers trip_kilometers] :as body} (extract-body)]
      (prn @resp)
      (are [x y] (= x y)
                 id (str settings-id)
                 trip_kilometers 1000.0
                 constant_kilometers 99999.0))))
