(ns app.resources.categories
  (:require [json-schema.core :as schema]))

(def schema
  (schema/compile
   {:type       "object"
    :properties {:name         {:type "string" :required true}}}))

(def table
  {:table     :categories
   :columns   {:id            {:type :serial :primary true :weighti 0}
               :resource_type {:type "varchar" :not-null true}
               :resource      {:type :jsonb}
               :cts           {:type :timestamptz :default "CURRENT_TIMESTAMP"}
               :ts            {:type :timestamptz :default "CURRENT_TIMESTAMP"}}
   :validator schema})
