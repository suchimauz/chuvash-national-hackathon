(ns app.utils
  (:require [clojure.string :as str]
            [honeysql.core :as hsql]))

(defn new-table [resource validator]
  {:table     (keyword (str/lower-case (name resource)))
   :columns   {:id            {:type :serial :primary true :weight 0}
               :resource_type {:type :varchar :not-null true :default (str "'" (name resource) "'")}
               :resource      {:type :jsonb}
               :cts           {:type :timestamptz :default "CURRENT_TIMESTAMP"}
               :ts            {:type :timestamptz :default "CURRENT_TIMESTAMP"}}
   :validator validator})

(defn resource-alias [rt key]
  (keyword
   (str (name rt) "." (name key))))

(defn dot-param? [rt params]
  (let [params (->> params
                    keys
                    (map name)
                    (filterv #(str/starts-with? % "."))
                    (map (fn [s] ["@@"
                                 (resource-alias rt :resource)
                                 (hsql/raw (str "'$" s " == \"" (get params s) "\"'::jsonpath"))])))]
    (if (> (count params) 1)
      (into [:and] params)
      (when (seq params)
        (first params)))))

(defn where-params [{:keys [table]} {:keys [id] :as params}]
  (let [conds (cond-> []
                (:id params) (conj [:= (resource-alias table :id) id])
                (dot-param? table params) (conj (dot-param? table params)))]
    (if (> (count conds) 1)
      {:where (into [:and] conds)}
      (when (seq conds)
        {:where (first conds)}))))
