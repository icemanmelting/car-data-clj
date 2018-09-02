(ns car-data-clj.rest.temp-data
  (:require [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.rest.response :refer [json error]]
            [car-data-clj.rest.validation :refer :all]))

(def-db-fns "car_data.sql")

(def get-by-trip-route "/trips/:id/temperature")

(defn get-by-trip [{{:keys [id]} :params}]
  (let [[_ err] (validate uuid-str id)]
    (if-not err
      (let [[res err] (get-temp-data-by-trip db {:id (uuid id)})]
        (if-not err
          (json :ok res)
          (error :unprocessable-entity (str err "Problem retrieving data"))))
      (error :unprocessable-entity (humanize-error err)))))
