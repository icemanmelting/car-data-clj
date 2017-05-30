(ns car-data-clj.db
  (:require [hugsql.core :as hugsql]
            [hugsql.adapter.clojure-java-jdbc :as adp]
            [car-data-clj.config :as config]
            [clojure.data.json :as json])
  (:import (java.util UUID)
           (org.postgresql.util PGobject)))

(defn uuid
  ([] (UUID/randomUUID))
  ([s] (try
         (UUID/fromString s)
         (catch Exception _ nil))))

(def db {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :subname (str "//"
                       (-> config/data :db :hostname)
                       ":"
                       (-> config/data :db :port)
                       "/"
                       (-> config/data :db :database)
                       "?useSSL=false")
         :user (-> config/data :db :user)
         :password (-> config/data :db :pass)})

(deftype AdapterWrapper [adapter]
  hugsql.adapter/HugsqlAdapter

  (execute [this db sqlvec options]
    (.execute adapter db sqlvec options))

  (query [this db sqlvec options]
    (.query adapter db sqlvec options))

  (result-one [this result options]
    [true (.result-one adapter result options)])

  (result-many [this result options]
    [true (.result-many adapter result options)])

  (result-affected [this result options]
    [true (.result-affected adapter result options)])

  (result-raw [this result options]
    [true (.result-raw adapter result options)])

  ;there might be a case where there is no message to be shown, like a nullpointer exception
  (on-exception [this exception]
    [false (.getMessage exception)]))

(hugsql/set-adapter! (AdapterWrapper. (adp/hugsql-adapter-clojure-java-jdbc)))

(defmacro def-db-fns [n]
  `(hugsql/def-db-fns ~n))

(defn db-run
  ([db sql param-data]
   (try
     (hugsql/db-run db sql param-data)
     (catch Exception e
       [false
        (.getMessage e) (.printStackTrace e)])))
  ([db sql param-data command]
   (try
     (hugsql/db-run db sql param-data command)
     (catch Exception e
       [false
        (.getMessage e) (.printStackTrace e)])))
  ([db sql]
   (try
     (hugsql/db-run db sql)
     (catch Exception e [false, (.getMessage e)]))))
