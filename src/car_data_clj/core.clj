(ns car-data-clj.core
  (:require [car-data-clj.db :as db :refer :all]
            [clojure.core.async :as a :refer [<! >!! go go-loop thread chan]]))

(def-db-fns "car_logs.sql")
(def-db-fns "car_settings.sql")
(def-db-fns "car_trips.sql")
(def-db-fns "data.sql")

(def ^:private data-buffer (chan 10))

(defn make-request [req]
  (>!! data-buffer req))

(defn- treat-data [rec]
  (let [op-type (:op_type rec)]
    (cond
      (.equals "car_settings_up" op-type) (update-carsettings db (dissoc rec :op_type))
      (.equals "car_log_new" op-type) (create-log db (dissoc rec :op_type))
      (.equals "car_trip_new" op-type) (insert-car-trip db (dissoc rec :op_type))
      (.equals "car_trip_up" op-type) (update-car-trip db (dissoc rec :op_type))
      (.equals "car_speed_new" op-type) (create-speed-data db (dissoc rec :op_type))
      (.equals "car_temp_new" op-type) (create-temperature-data db (dissoc rec :op_type)))))

(go-loop []
  (when-let [rec (<! data-buffer)]
    (treat-data rec))
  (recur))

;(defn- update-data [rec]
;  (let [op-type (:op_type rec)]
;    (cond
;      (.equals "car_settings_up" op-type) (update-carsettings db (dissoc rec :op_type))
;      (.equals "car_trip_up" op-type) (update-car-trip db (dissoc rec :op_type)))))
;
;(defn- insert-data [rec]
;  (let [op-type (:op_type rec)]
;    (cond
;      (.equals "car_log_new" op-type) (create-log db (dissoc rec :op_type))
;      (.equals "car_trip_new" op-type) (insert-car-trip db (dissoc rec :op_type))
;      (.equals "speed_new" op-type) (create-speed-data db (dissoc rec :op_type))
;      (.equals "temp_new" op-type) (create-temperature-data db (dissoc rec :op_type)))))
;
;(go-loop []
;  (insert-data (<! insert-buffer))
;  (recur))
;
;(go-loop []
;  (update-data (<! update-buffer))
;  (recur))




