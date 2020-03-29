(ns app.utils
  (:require [clojure.string :as str]
            [clj-pg.honey :as pg]
            [honeysql.core :as hsql]))

(defn row-to-resource [res]
  (-> res
      (assoc-in [:resource :id] (:id res))
      (assoc-in [:resource :resourceType] (:resource_type res))
      :resource))

(defn reference-type [& enum]
  {:type       :object
   :properties {:id           {:type :integer
                               :required true}
                :resourceType {:type :string
                               :enum enum
                               :required true}
                :display      {:type :string}}})

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
   (str (str/lower-case (name rt)) "." (name key))))

(defn assoc-params [db params rows]
  (if-let [assoc (:assoc params)]
    (map (fn [{table :resourceType id :id :as res}]
           (reduce (fn [res a]
                  (let [path (-> a
                                 (str/split #"\.")
                                 (->> (mapv keyword)))
                        object (get-in res path)
                        assoc-resource (when (and (:id object) (:resourceType object))
                                         (pg/query-first db
                                          {:select [(resource-alias (:resourceType object) :*)]
                                           :from [(keyword (str/lower-case (name (:resourceType object))))]
                                           :where [:= (resource-alias (:resourceType object) :id) (:id object)]}))]
                    (if assoc-resource
                      (assoc-in res (conj path :resource) (row-to-resource assoc-resource)) res)))
                res (str/split assoc #"\,")))
         rows)
    rows))

(defn dot-param? [rt params]
  (let [params (->> params
                    keys
                    (map name)
                    (filterv #(str/starts-with? % "."))
                    (map (fn [s] [:or
                                 [:=
                                  (hsql/call
                                   :cast
                                   (hsql/call :jsonb_path_query_first
                                              (resource-alias rt :resource)
                                              (hsql/raw (str "'$" s "'")))
                                   :text)
                                  (hsql/raw (str "'" (get params s) "'"))]
                                 ["@@"
                                  (resource-alias rt :resource)
                                  (hsql/raw (str "'$" s " == \"" (get params s) "\"'::jsonpath"))]])))]
    (if (> (count params) 1)
      (into [:and] params)
      (when (seq params)
        (first params)))))

(defn join-params [{:keys [table]} {:keys [__project_district __event_district]}]
  (let [_p_d [[:event :event]
              [:= (hsql/call :cast :project.id :text)
               (hsql/raw "event.resource#>>'{project,id}'")]
              [:object :object]
              [:and
               [:= (hsql/call :cast :event.id :text)
                (hsql/raw "object.resource#>>'{event,id}'")]
               ["@@"
                :object.resource
                (hsql/raw (str "'$.address.district == \"" __project_district "\"'"))]]]
        _e_d [[:object :object]
              [:and
               [:= (hsql/call :cast :event.id :text)
                (hsql/raw "object.resource#>>'{event,id}'")]
               ["@@"
                :object.resource
                (hsql/raw (str "'$.address.district == \"" __event_district "\"'"))]]]]
    (cond
      __project_district {:join _p_d}
      __event_district   {:join _e_d})))

(defn where-params [{:keys [table]} {:keys [id ilike] :as params}]
  (let [conds (cond-> []
                id (conj [:= (resource-alias table :id) id])
                ilike (concat (-> ilike
                                  (str/split #"[\s,\+]")
                                  (->> (map (comp hsql/raw
                                               #(str "(" (name (resource-alias table :id)) " || '' || " (name (resource-alias table :resource)) "::text) ilike '%" % "%'"))))))
                (dot-param? table params) (conj (dot-param? table params))
                true vec)]
    (if (> (count conds) 1)
      {:where (into [:and] conds)}
      (when (seq conds)
        {:where (first conds)}))))
