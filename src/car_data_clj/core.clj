(ns car-data-clj.core
  (:require [car-data-clj.db.postgresql :refer :all]
            [clojure.core.async :as a :refer [<! >!! go go-loop thread chan]]))

(def-db-fns "car_logs.sql")
(def-db-fns "cars.sql")
(def-db-fns "car_trips.sql")
(def-db-fns "car_data.sql")
(def-db-fns "car_positions")

(def ^:private data-buffer (chan 10))

(defn make-request [req]
  (>!! data-buffer req))

(defn read-car [id]
  (let [[car err] (get-car db {:id id})]
    (when-not err
      car)))

(defmulti treat-data (fn [rec] (:op_type rec)))

(defmethod treat-data "car_new" [rec]
  (create-car db (dissoc rec :op_type)))

(defmethod treat-data "car_up" [rec]
  (update-car db (dissoc rec :op_type)))

(defmethod treat-data "car_log_new" [rec]
  (create-log db (dissoc rec :op_type)))

(defmethod treat-data "car_trip_new" [rec]
  (insert-car-trip db (dissoc rec :op_type)))

(defmethod treat-data "car_trip_up" [rec]
  (update-car-trip db (dissoc rec :op_type)))

(defmethod treat-data "car_speed_new" [rec]
  (create-speed-data db (dissoc rec :op_type)))

(defmethod treat-data "car_temp_new" [rec]
  (create-temperature-data db (dissoc rec :op_type)))

(defmethod treat-data "car_pos_new" [rec]
  (create-position db (dissoc rec :op_type)))

(go-loop []
  (when-let [rec (<! data-buffer)]
    (treat-data rec))
  (recur))
