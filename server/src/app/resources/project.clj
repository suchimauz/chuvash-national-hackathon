(ns app.resources.project
  (:require [json-schema.core :as schema]
            [app.utils :as u]))

(def table
  (u/new-table
   :Project
   {:type       "object"
    :properties {:category    {:type :string
                               :enum ["national" "regional"]}
                 :author      {:type :string}
                 :name        {:type :string :required true}
                 :description {:type :string}
                 :project     {:type :string}
                 :period      {:type       :object
                               :properties {:start {:type :string}
                                            :end   {:type :string}}}}}))
