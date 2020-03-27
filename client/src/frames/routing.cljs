(ns frames.routing
  (:require [re-frame.core  :as rf]
            [clojure.string :as str]))

(defn pathify [path]
  (-> path (str/split #"/") next not-empty))

(defn params-node [node]
  (filter (comp vector? first) node))

(defn match [routes location]
  (loop [node              routes
         [current & other] (pathify location)
         params            {}]
    (if-let [node (get node current)]
      (recur node other params)
      (if current
        (let [[[[k] node]] (params-node node)]
          (recur node other (assoc params k current)))
        {:match (:- node) :params params}))))

(defn parse-query [s]
  (if (str/blank? s)
    {}
    (reduce
     (fn [acc pair]
       (let [[k v] (str/split pair #"=" 2)]
         (assoc acc (keyword k) (js/decodeURIComponent v))))
     {} (-> (str/replace s #"^\?" "")
            (str/split "&")))))

(defn parse-fragment [routes]
  (let [location         (.. js/window -location -hash)
        [fragment query] (str/split location  #"\?")
        route            (match routes fragment)]
    {:path   fragment
     :params (merge (:params route)
                    {:search (parse-query query)})
     :match  (or (:match route) :-)}))

(rf/reg-event-fx
 ::location-changed
 (fn [{db :db} [_ routes]]
   (let [{:keys [match params] :as current} (parse-fragment routes)
         {old-match :match old-params :params} (:routing db)
         page-hook (cond
                     (nil? old-match)       (cond-> [[match :init params]]
                                              (seq params) (conj [match :params params]))
                     (not= old-params params) [[match :params params]]

                     :else [[old-match :deinit old-params]
                            [match :init   params]])]
     {:db         (assoc db :routing current)
      :dispatch-n page-hook})))

(rf/reg-fx
 ::init
 (fn [routes]
   (aset js/window "onhashchange" #(rf/dispatch [::location-changed routes]))
   (rf/dispatch [::location-changed routes])))

(rf/reg-sub
 ::current
 (fn [db]
   (:routing db)))

(rf/reg-sub
 ::fragment-params
 (fn [db]
   (-> db :routing :params)))
