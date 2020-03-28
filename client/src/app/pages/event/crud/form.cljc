(ns app.pages.event.crud.form
  (:require [re-frame.core :as rf]
            [zenform.model :as zf]
            [app.form.events :as ze]
            [app.helpers :as h]))

(def path [:form ::form])
(def schema
  {:type   :form
   :fields {:name        {:type :string}
            :id          {:type :string}
            :project     {:type :object}
            :description {:type :string}
            :period      {:type   :form
                          :fields {:start {:type :string}
                                   :end   {:type :string}}}
            :amount      {:type :string}}})

(def object-path [:form ::object])
(def object
  {:type   :form
   :fields {:name   {:type :string}
            :address {:type   :form
                      :fields {:distinct   {:type :string}
                               :city       {:type :string}
                               :street     {:type :string}
                               :house      {:type :string}
                               :appartment {:type :string}}}
            :event  {:type          :object}}})

(rf/reg-event-fx
 ::init
 (fn [coeff [_ {:keys [data]}]]
   {:dispatch [:zf/init path schema data]}))

(defn eval-form [db cb]
  (let [{:keys [errors value form]} (-> db
                                        (get-in path)
                                        zf/eval-form)]
    (merge
     {:db (assoc-in db path form)}
     (if (empty? errors)
       (cb value)
       #?(:clj (println errors)
          :cljs (.warn js/console "Form errors: " (clj->js errors)))))))

(defn eval-object [db cb]
  (let [{:keys [errors value form]} (-> db (get-in object-path) zf/eval-form )]
    (merge
     {:db (assoc-in db object-path form)}
     (when (empty? errors)
       #?(:clj  (println errors)
          :cljs (.warn js/console "Form errors: " (clj->js errors)))))))
