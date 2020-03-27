(ns app.pages.project.crud.form
  (:require [re-frame.core :as rf]
            [zenform.model :as zf]))

(def path [:form ::form])
(def schema
  {:type   :form
   :fields {:name        {:type :string}
            :description {:type :string}
            :author      {:type      :object
                          :on-search ::search-author}}})

(rf/reg-event-fx
 ::init
 (fn [coeff {:keys [data]}]
   {:dispatch [:zf/init path schema data]}))

(defn eval-form [db cb]
  (let [{:keys [errors value form]} (-> db
                                        (get-in path)
                                        zf/eval-form)]))
