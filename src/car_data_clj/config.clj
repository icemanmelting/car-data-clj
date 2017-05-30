(ns car-data-clj.config
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def data (let [res (io/resource "config.json")
                env (keyword (or (System/getenv "DMP_API_UI_ENV") "development"))]
            (env (json/read-str (slurp res) :key-fn keyword))))
