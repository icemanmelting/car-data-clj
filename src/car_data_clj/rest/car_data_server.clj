(ns car-data-clj.rest.car-data-server
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :as json]
            [ring.middleware.params :refer [wrap-params]]
            [car-data-clj.rest.response :refer [error]]
            [car-data-clj.rest.car-logs :as cl]))

(defn- wrap-json-body [h]
  (json/wrap-json-body h {:keywords? true
                          :malformed-response (error :unprocessable-entity "Wrong JSON format")}))

(defn- wrap-cors [h]
  (cors/wrap-cors h
                  :access-control-allow-origin #".+"
                  :access-control-allow-methods [:get :put :post :delete :options]))

(defroutes car-data-routes
  (GET cl/get-by-trip-route [] (-> cl/get-by-trip wrap-params)))

(defroutes app
  (-> (routes car-data-routes) wrap-json-body wrap-cors))

(defn start-server []
  (run-server app {:port 8080}))