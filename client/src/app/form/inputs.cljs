(ns app.form.inputs
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [app.helpers  :as helpers]
            [app.form.mask :as mask]))

(defn input
  [form-path path & [attrs]]
  (let [node  (rf/subscribe [:zf/node form-path path])
        attrs (assoc attrs :on-change #(rf/dispatch [:zf/set-value form-path path (.. % -target -value)]))]
    (fn [& _]
      (let [*node    @node
            required (:validators *node)
            v        (:value *node)
            errs     (:errors *node)]
        [:<>
         [:input.form-control (-> attrs
                                  (assoc :value v)
                                  (update :class (fn [class] (str class (when errs " is-invalid")))))]
         (when required
           [:div.invalid-feedback (str/join ", " (vals errs))])]))))

(defn textarea
  [form-path path & [attrs]]
  (let [node (rf/subscribe [:zf/node form-path path])
        attrs (assoc attrs :on-change #(rf/dispatch [:zf/set-value form-path path (.. % -target -value)]))]
    (fn [& _]
      (let [*node @node
            required (:validators *node)
            v (:value *node)
            errs (:errors *node)]
        [:<>
         [:textarea.form-control (-> attrs
                                     (assoc :value v)
                                     (update :class (fn [class] (str class (when errs " is-invalid")))))]
         (when required
           [:div.invalid-feedback {:style {:display "block"}} (str/join ", " (vals errs))])]))))

(defn time-input [form-path path & [attrs]]
  (let [node  (rf/subscribe [:zf/node form-path path])
        attrs (assoc attrs :on-change
                     #(rf/dispatch [:zf/set-value form-path path
                                    (let [val  (.. % -target -value)
                                          mask (or (:mask attrs) mask/mask-date)]
                                      (mask/mask-resolve mask val))]))]
    (fn [_ _ & [{:keys [disabled]}]]
      (let [*node @node
            v     (:value *node)
            errs  (:errors *node)]
        [:div.z-input
         {:class (when-not (-> attrs :form-group false?) "form-group")}
         [:input.form-control
          (-> attrs
              (assoc :disabled disabled)
              (dissoc :mask)
              (dissoc :form-group)
              (assoc :value v)
              (update :class (fn [class] (str class (when errs " is-invalid")))))]]))))
