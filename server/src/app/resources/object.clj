(ns app.resources.object
  (:require [json-schema.core :as schema]
            [app.utils :as u]))

(def table
  (u/new-table
   :Object
   {:type       "object"
    :properties {:name    {:type :string}
                 :address {:type       :object
                           :properties {:district  {:type :string}
                                        :city      {:type :string}
                                        :street    {:type :string}
                                        :house     {:type :string}
                                        :apartment {:type :string}}}
                 :event   (u/reference-type "Event")}}))
