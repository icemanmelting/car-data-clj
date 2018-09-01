(ns car-data-clj.rest.car-data-server
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :as json]
            [ring.middleware.params :refer [wrap-params]]
            [car-data-clj.rest.response :refer [error]]
            [car-data-clj.rest.car-logs :as cl]
            [car-data-clj.rest.cars :as cs]
            [car-data-clj.rest.car-trips :as ct]
            [car-data-clj.rest.speed-data :as csd]
            [car-data-clj.rest.temp-data :as ctd]
            [car-data-clj.rest.sessions :refer [authorize] :as sessions]))

(defn- wrap-json-body [h]
  (json/wrap-json-body h {:keywords? true
                          :malformed-response (error :unprocessable-entity "Wrong JSON format")}))

(defn- wrap-cors [h]
  (cors/wrap-cors h
                  :access-control-allow-origin #".+"
                  :access-control-allow-methods [:get :put :post :delete :options]))

(defroutes car-data-routes
  (GET cl/get-by-trip-route [] (-> cl/get-by-trip wrap-params authorize))
  (GET cs/get-by-id-route [] (-> cs/get-by-id wrap-params authorize))
  (GET ct/get-by-id-route [] (-> ct/get-by-id wrap-params authorize))
  (GET csd/get-by-trip-route [] (-> csd/get-by-trip wrap-params authorize))
  (GET ctd/get-by-trip-route [] (-> ctd/get-by-trip wrap-params authorize)))

(defroutes session-routes
  (GET "/session" [] (authorize sessions/find-one))
  (DELETE "/session" [] (authorize sessions/destroy))
  (POST "/session" [] sessions/create))

(defroutes app
  (-> (routes session-routes car-data-routes) wrap-json-body wrap-cors))

(defn start-server []
  (run-server app {:port 8080}))