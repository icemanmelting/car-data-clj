(ns car-data-clj.rest.positions
  (:require [car-data-clj.db.postgresql :refer [db def-db-fns uuid]]
            [car-data-clj.rest.response :refer [json error]]
            [car-data-clj.rest.validation :refer :all])
  (:import (java.sql Timestamp)))

(def ^:private positions-fmt {:id uuid-str
                              :ts non-empty-str})

(def-db-fns "car_positions.sql")

(def get-latest-route "/cars/:id/positions")

(defn get-latest [{{:keys [id]} :params}]
  (let [[_ err] (validate uuid-str id)]
    (if-not err
      (let [[res err] (get-latest-position db {:car_id (uuid id)})]
        (if-not err
          (json :ok res)
          (error :unprocessable-entity (str err "Problem retrieving data"))))
      (error :unprocessable-entity (humanize-error err)))))

(def get-last-positions-route "/cars/:id/positions/:ts")

(defn get-last [{{:keys [id ts] :as params} :params}]
  (let [[_ err] (validate positions-fmt params)]
    (if-not err
      (let [^Long ts (try (Long/parseLong ts) (catch Exception _ 0))
            [res err] (get-last-positions db {:car_id (uuid id)
                                              :created (Timestamp. ts)})]
        (if-not err
          (json :ok res)
          (error :unprocessable-entity (str err "Problem retrieving data"))))
      (error :unprocessable-entity (humanize-error err)))))
