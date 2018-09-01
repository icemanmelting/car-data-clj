(ns car-data-clj.core-test
  (:require [clojure.test :refer :all]
            [car-data-clj.core :refer :all]
            [car-data-clj.db.postgresql :refer :all]
            [car-data-clj.web-setup :refer [clear-ks setup-session owner]]))

(def ^:private car-id (uuid))

(defn- setup-car []
  (create-car db {:id car-id :owner owner :cnst_km 3000 :trip_km 10}))

(defn clear-ks-fixture [f]
  (clear-ks)
  (setup-session)
  (setup-car)
  (f))

(use-fixtures :each clear-ks-fixture)

(deftest test-trip-insertion-update
  (testing "insert trip"
    (let [trip-id (uuid)]
      (is (make-request {:op_type "car_trip_new"
                         :id trip-id
                         :car_id car-id
                         :starting_km 0}))
      (Thread/sleep 1000)
      (let [[res _] (select-car-trip db {:id trip-id})]
        (is (= trip-id (:id res))))))
  (testing "update trip"
    (let [trip-id (uuid)]
      (is (make-request {:op_type "car_trip_new"
                         :id trip-id
                         :car_id car-id
                         :starting_km 0}))
      (is (make-request {:op_type "car_trip_up"
                         :id trip-id
                         :ending_km 200.0}))
      (Thread/sleep 1000)
      (let [[get-res _] (select-car-trip db {:id trip-id})]
        (is (= 200.0 (:ending_km get-res)))))))

(deftest test-car-log-insertion
  (testing "create log"
    (let [trip-id (uuid)
          log-id (uuid)]
      (is (make-request {:op_type "car_trip_new"
                         :id trip-id
                         :car_id car-id
                         :starting_km 0}))
      (Thread/sleep 1000)
      (is (make-request {:op_type "car_log_new"
                         :id log-id
                         :trip_id trip-id
                         :msg "message"
                         :log_level "ERROR"}))
      (Thread/sleep 1000)
      (let [[res _] (select-log db {:id log-id})]
        (is (= trip-id (:trip_id res)))
        (is (= "message" (:message res)))
        (is (= "ERROR" (:log_level res)))))))

(deftest test-car-speed-insertion
  (testing "create speed data"
    (let [trip-id (uuid)
          speed-id (uuid)]
      (is (make-request {:op_type "car_trip_new"
                         :id trip-id
                         :car_id car-id
                         :starting_km 0}))
      (Thread/sleep 1000)
      (is (make-request {:op_type "car_speed_new"
                         :id speed-id
                         :trip_id trip-id
                         :speed 0
                         :rpm 4000
                         :gear 0}))
      (Thread/sleep 1000)
      (let [[res _] (get-speed-data db {:id speed-id})]
        (is (= trip-id (:trip_id res)))
        (is (= 0.0 (:speed res)))
        (is (= 4000.0 (:rpm res)))
        (is (= 0 (:gear res)))))))

(deftest test-car-temperature-insertion
  (testing "create temperature data"
    (let [trip-id (uuid)
          temp-id (uuid)]
      (is (make-request {:op_type "car_trip_new"
                         :id trip-id
                         :car_id car-id
                         :starting_km 0}))
      (Thread/sleep 1000)
      (is (make-request {:op_type "car_temp_new"
                         :id temp-id
                         :trip_id trip-id
                         :val 50.0}))
      (Thread/sleep 1000)
      (let [[res _] (get-temp-data db {:id temp-id})]
        (is (= trip-id (:trip_id res)))
        (is (= 50.0 (:value res)))))))
