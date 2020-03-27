(ns app.form.inputs
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

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
