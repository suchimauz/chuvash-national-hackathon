(ns app.handler
  (:require [reitit.ring :as reitit]
            [app.actions :as action]
            (app.resources
             [categories :as categories])))

(def handler
  (reitit/ring-handler
   (reitit/router
    [["/categories" {:get    {:handler (partial action/-get    categories/table)}
                     :post   {:handler (partial action/-post   categories/table)}
                     :delete {:handler (partial action/-delete categories/table)}}]])
   (constantly {:status 404, :body "Not found"})))
