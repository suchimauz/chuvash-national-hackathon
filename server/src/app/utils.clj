(ns app.utils
  (:require [clojure.string :as str]))

(defn new-table [resource validator]
  {:table     (keyword (str/lower-case (name resource)))
   :columns   {:id            {:type :serial :primary true :weight 0}
               :resource_type {:type :varchar :not-null true :default (str "'" (name resource) "'")}
               :resource      {:type :jsonb}
               :cts           {:type :timestamptz :default "CURRENT_TIMESTAMP"}
               :ts            {:type :timestamptz :default "CURRENT_TIMESTAMP"}}
   :validator validator})
