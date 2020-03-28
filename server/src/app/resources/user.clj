(ns app.resources.user
  (:require [json-schema.core :as schema]
            [app.utils :as u]))

(def table
  (u/new-table
   :User
   {:type "object"
    :properties {:email {:type :string
                         :required true}
                 :password {:type :string
                            :required true}}}))
