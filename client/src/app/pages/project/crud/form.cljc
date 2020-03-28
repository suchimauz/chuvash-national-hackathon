(ns app.pages.project.crud.form
  (:require [re-frame.core :as rf]
            [zenform.model :as zf]))

(def path [:form ::form])
(def schema
  {:type   :form
   :fields {:name        {:type :string}
            :id          {:type :string}
            :description {:type :string}
            :author      {:type      :object
                          :on-search ::search-author}
            :img         {:type :string}
            :project     {:type :string}
            :category    {:type :string}
            :startDate   {:type :string}
            :endDate     {:type :string}}})

(rf/reg-event-fx
 ::set-img
 (fn [_ [_ _ file-meta]]
   {:dispatch [:zf/set-value path [:img] (:url file-meta)]}))

(rf/reg-event-fx
 ::init
 (fn [coeff [_ {:keys [data]}]]
   {:dispatch [:zf/init path schema data]}))

(defn eval-form [db cb]
  (let [{:keys [errors value form]} (-> db (get-in path) zf/eval-form)]
    (merge
     {:db (assoc-in db path form)}
     (if (empty? errors)
       (cb value)
       #?(:clj  (println errors)
          :cljs (.warn js/console "Form errors: " (clj->js errors)))))))
