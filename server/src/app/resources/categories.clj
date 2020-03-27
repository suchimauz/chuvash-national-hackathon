(ns app.resources.categories
  (:require [json-schema.core :as schema]))

(def schema
  (schema/compile
   {:type       "object"
    :properties {:resourceType {:type "string" :required true :enum ["Categories"]}
                 :name         {:type "string" :required true}}}))

(def table
  {:table     :categories
   :columns   {:id       {:type :serial :primary true :weighti 0}
               :resource {:type :jsonb}
               :tz       {:type :timestamptz}}
   :validator schema})
