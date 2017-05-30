(ns car-data-clj.core
  (:require [car-data-clj.db :as db :refer :all]
            [clojure.core.async :as a :refer [<! >!! go thread chan]]))

(def-db-fns "car_logs.sql")
(def-db-fns "car_settings.sql")
(def-db-fns "car_trips.sql")
(def-db-fns "data.sql")

(def ^:private data-buffer (chan 10))

(defn insert-request [req]
  (>!! data-buffer req))

(thread
  (let [rec (<! data-buffer)]
    (let [op-type (:op_type rec)]
      (cond
        (.equals "car_settings_up" op-type) (update-carsettings db (dissoc rec :op_type))
        (.equals "car_log" op-type) (create-log db (dissoc rec :op_type))
        (.equals "car_trip_new" op-type) (prn (insert-car-trip db (dissoc rec :op_type)))
        (.equals "car_trip_up" op-type) (update-car-trip db (dissoc rec :op_type))
        (.equals "speed_new" op-type) (create-speed-data db (dissoc rec :op_type))
        (.equals "temp_new" op-type) (create-temperature-data db (dissoc rec :op_type)))))
  (recur))

(insert-request {:op_type "car_trip_new"
                 :id (db/uuid)
                 :starting_km 0})

