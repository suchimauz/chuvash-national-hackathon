(ns app.form.events
  (:require [zenform.model :as zf]
            [re-frame.core :as rf]
            [app.helpers  :as helpers]
            [clojure.string :as str]))

(rf/reg-event-fx
 ::search-author
 (fn [_ [_ & [{:keys [form-path path q]}]]]
   {:xhr/fetch {:uri    "/Author"
                :success  {:event ::subjects-loaded
                           :params {:path path
                                    :form-path form-path}}}
    :dispatch [:zf/form-set-value form-path path :status :loading]}))

(rf/reg-event-fx
 ::subjects-loaded
 (fn [{db :db} [_ {data :data} {:keys [form-path path]}]]
   (let [items (map
                (fn [item]
                  (let [fio (helpers/fio (:name item))]
                    {:value   {:id           (:id item)
                               :display      fio
                               :resourceType (:resourceType item)}
                     :display [:div.row.align-items-center
                                 [:div.col-auto
                                  [:a.avatar.rounded-circle
                                   [:img
                                    {:src (str "http://localhost:8990" (:photo item)),}]]]
                                 [:div.col.ml--2
                                  [:h4.mb-0 fio]
                                  [:small (:position item)]]]}))
                data)]
     {:db       (assoc-in db (conj (into form-path (zf/get-node-path path)) :items) items)
      :dispatch [:zf/form-set-value form-path path :status :loaded]})))
