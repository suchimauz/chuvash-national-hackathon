(ns app.pages.event.crud.form
  (:require [re-frame.core :as rf]
            [zenform.model :as zf]
            [app.form.events :as ze]))

(def path [:form ::form])
(def schema
  {:type   :form
   :fields {:name        {:type :string}
            :purpose     {:type :object
                          :display-paths [[:display]]
                          :on-search ::ze/search-purpose}
            :description {:type :string}
            :startDate   {:type :string}
            :endDate     {:type :string}
            :amount      {:type :string}}})

(rf/reg-event-fx
 ::init
 (fn [coeff {:keys [data]}]
   {:dispatch [:zf/init path schema data]}))

(defn eval-form [db cb]
  (let [{:keys [errors value form]} (-> db
                                        (get-in path)
                                        zf/eval-form)]))
