(ns car-data-clj.config
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def data (let [res (io/resource "config.json")
                env (keyword (or (System/getenv "car-data-clj") "development"))]
            (env (json/read-str (slurp res) :key-fn keyword))))
