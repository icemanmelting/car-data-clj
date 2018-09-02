(ns car-data-clj.rest.car-trips
  (:require [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.rest.response :refer [json error]]
            [car-data-clj.rest.validation :refer :all]))

(def ^:private trip-fmt {:id uuid-str
                         :car-id uuid-str})

(def-db-fns "car_trips.sql")

(def get-by-id-route "/cars/:car-id/trips/:id")

(defn get-by-id [{{:keys [car-id id] :as params} :params}]
  (let [[_ err] (validate trip-fmt params)]
    (if-not err
      (let [[res err] (select-car-trip db {:id (uuid id) :car_id (uuid car-id)})]
        (if-not err
          (json :ok res)
          (error :unprocessable-entity (str err "Problem retrieving data"))))
      (error :unprocessable-entity (humanize-error err)))))
