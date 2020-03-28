(ns app.resources.purpose
  (:require [app.utils :as u]))

(def table
  (u/new-table
   :Purpose
   {:type       "object"
    :properties {:name       {:type     :string
                              :required true}
                 :indicators {:type  :array
                              :items {:type       :object
                                      :properties {:year     {:type :string}
                                                   :current  {:type :string}
                                                   :planning {:type :string}}}}
                 :project    (u/reference-type "Project")}}))
