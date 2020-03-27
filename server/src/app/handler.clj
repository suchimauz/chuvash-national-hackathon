(ns app.handler
  (:require [reitit.ring :as reitit]
            [app.actions :as action]
            [app.auth :as auth]
            (app.resources
             [user :as user])))

(def handler
  (reitit/ring-handler
   (reitit/router
    [["/User" {:get    {:handler (partial action/-get    user/table)}
               :post   {:handler (partial action/-post   user/table)}
               :delete {:handler (partial action/-delete user/table)}}]
     ["/authorize" {:post {:handler (fn [req] (auth/authorize req))}}]])
   (constantly {:status 404, :body {:error {:message "Route not found"}}})))
