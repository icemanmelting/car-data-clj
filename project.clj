(defproject car-data-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/core.async "0.3.442"]
                 [org.postgresql/postgresql "42.1.1"]
                 [com.layerware/hugsql "0.4.5"]]
  :target-path "target/%s"
  :uberjar-name "car-cpu-standalone.jar"
  :profiles {:uberjar {:aot :all}})
