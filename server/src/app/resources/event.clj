(ns app.resources.event
  (:require [json-schema.core :as schema]
            [app.utils :as u]))

(def table
  (u/new-table
   :Event
   {:type       "object"
    :properties {:name        {:type     :string
                               :required true}
                 :project     (u/reference-type "Project")
                 :description {:type :string}
                 :period      {:type       :object
                               :required   true
                               :properties {:start {:type     :string
                                                    :required true}
                                            :end   {:type :string}}}
                 :amount      {:type :string}}}))
