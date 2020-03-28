(ns app.handler
  (:require [reitit.ring :as reitit]
            [app.actions :as action]
            [app.auth :as auth]
            (app.resources
             [user :as user]
             [project :as project]
             [author :as author])))

(def handler
  (reitit/ring-handler
   (reitit/router
    [["/User" {:get  {:handler (partial action/-get    user/table)}
               :post {:handler (partial action/-post   user/table)}}]
     ["/Project" {:get  {:handler (partial action/-get project/table)}
                  :post {:handler (partial action/-post project/table)}}]
     ["/Project/:id" {:get    {:handler (partial action/-get project/table)}
                      :put    {:handler (partial action/-put project/table)}
                      :delete {:handler (partial action/-delete project/table)}}]
     ["/Author" {:get   {:handler (partial action/-get author/table)}
                 :post {:handler (partial action/-post author/table)}}]
     ["/Author/:id" {:get     {:handler (partial action/-get author/table)}
                     :put    {:handler (partial action/-put author/table)}
                     :delete {:handler (partial action/-delete author/table)}}]
     ["/authorize" {:post {:handler (fn [req] (auth/authorize req))}}]])
   (constantly {:status 404, :body {:error {:message "Route not found"}}})))
