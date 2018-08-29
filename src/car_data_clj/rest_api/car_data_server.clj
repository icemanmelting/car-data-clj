(ns car-data-clj.rest-api.car-data-server
  (:require [compojure.core :refer :all]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :as json]
            [ring.middleware.params :refer [wrap-params]]
            [car-data-clj.rest-api.response :refer [error]]))

(defn- wrap-json-body [h]
  (json/wrap-json-body h {:keywords? true
                          :malformed-response (error :unprocessable-entity "Wrong JSON format")}))

(defn- wrap-cors [h]
  (cors/wrap-cors h
                  :access-control-allow-origin #".+"
                  :access-control-allow-methods [:get :put :post :delete :options]))

(defroutes web-search-routes
  (GET ws/find-document-by-id-route [] (-> ws/find-document-by-id wrap-params authorize))
  (DELETE ws/delete-document-route [] (-> ws/delete-document wrap-params delete-from-cache authorize))
  (POST ws/find-document-route [] (-> ws/find-document retrieve-from-cache authorize))
  (POST ws/upsert-document-route [] (-> ws/upsert-document wrap-params delete-from-cache authorize)))

(defroutes session-routes
  (GET "/session" [] (authorize sessions/find-one))
  (DELETE "/session" [] (authorize sessions/destroy))
  (POST "/session" [] sessions/create))

(defroutes app
  (-> (routes session-routes web-search-routes) wrap-json-body wrap-cors))