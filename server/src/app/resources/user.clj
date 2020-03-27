(ns app.resources.user
  (:require [json-schema.core :as schema]
            [app.utils :as u]))

(def table
  (u/new-table
   :User
   (schema/compile
    {:type "object"
     :properties {:email {:type :varchar
                          :required true}
                  :password {:type :varchar
                             :required true}}})))
