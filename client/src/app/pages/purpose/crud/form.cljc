(ns app.pages.purpose.crud.form
  (:require [re-frame.core :as rf]
            [app.helpers :as h]
            [zenform.model :as zf]))

(def path [:form ::form])
(def schema
  {:type   :form
   :fields {:name       {:type :string}
            :id         {:type :string}
            :indicators {:type :collection
                         :item {:type   :form
                                :fields {:year     {:type :string}
                                         :current  {:type :string}
                                         :planning {:type :string}}}}
            :project    {:type :object}}})

(rf/reg-event-fx
 ::init
 (fn [_ [_ {project :data} {purpose :data}]]
   (if project
     {:dispatch-n [[:zf/init path schema {:project {:id           (:id project)
                                                    :display      (:name project)
                                                    :resourceType (:resourceType project)}}]
                   [:zf/add-collection-item path [:indicators] {}]]}
     {:dispatch [:zf/init path schema purpose]})))

(defn eval-form [db cb]
  (let [{:keys [errors value form]} (-> db (get-in path) zf/eval-form)]
    (merge
     {:db (assoc-in db path form)}
     (if (empty? errors)
       (cb (update-in value [:project :id] h/parseInt))
       #?(:clj  (println errors)
          :cljs (.warn js/console "Form errors: " (clj->js errors)))))))
