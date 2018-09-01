(ns car-data-clj.rest.cars
  (:require [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.rest.response :refer [json error]]
            [car-data-clj.rest.validation :refer :all]))

(def-db-fns "cars.sql")

(def get-by-id-route "/cars/:id")

(defn get-by-id [{{:keys [id]} :params}]
  (let [[_ err] (validate uuid-str id)]
    (if-not err
      (let [[res err] (get-car db {:id (uuid id)})]
        (if-not err
          (json :ok res)
          (error :unprocessable-entity (str err "Problem retrieving data"))))
      (error :unprocessable-entity (humanize-error err)))))
