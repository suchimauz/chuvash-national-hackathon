(ns app.form.inputs
  (:require [re-frame.core :as rf]
            [zenform.model :as model]
            [clojure.string :as str]
            [app.helpers  :as helpers]
            [app.form.mask :as mask]))


(rf/reg-event-db
 :zf/form-set-value
 (fn [db [_ form-path path key v]]
   (update-in db form-path
              (fn [form]
                (assoc-in form (conj (model/get-node-path path) key)
                          v)))))

(rf/reg-event-db
 :zf/items
 (fn [db [_ form-path path items]]
   (update-in db form-path
              (fn [form]
                (assoc-in form (conj (model/get-node-path path) :items)
                          items)))))

(rf/reg-event-db
 :zf/dropdown
 (fn [db [_ form-path path open?]]
   (update-in db form-path
              (fn [form]
                (assoc-in form (conj (model/get-node-path path) :dropdown)
                          open?)))))


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
        [:<>
         {:class (when-not (-> attrs :form-group false?) "form-group")}
         [:input.form-control
          (-> attrs
              (assoc :disabled disabled)
              (dissoc :mask)
              (dissoc :form-group)
              (assoc :value v)
              (update :class (fn [class] (str class (when errs " is-invalid")))))]]))))


(defn *combobox
  [form-path path & [{:keys [placeholder]}]]
  (let [node           (rf/subscribe [:zf/node form-path path])
        on-change      #(rf/dispatch [(:on-search @node) {:q % :path path :form-path form-path}])
        on-click       (fn [value]
                         (when-let [click (:on-click @node)]
                           (rf/dispatch [click value]))
                         (rf/dispatch [:zf/dropdown form-path path false])
                         (rf/dispatch [:zf/set-value form-path path value]))
        open-dropdown  (fn []
                         (rf/dispatch  [(:on-search @node) {:path path :form-path form-path}])
                         (rf/dispatch [:zf/dropdown form-path path true]))]
    (fn [& _]
      (let [{:keys [items display-paths value dropdown]} @node]
        [:div.position-relative #_{:on-blur close-dropdown}
         [:div.input-group {:on-click open-dropdown}
          [:span.form-control
           (if (empty? value)
             [:span.text-muted placeholder]
             [:span (str/join " " (mapv
                                   (fn [path] (get-in value path))
                                   display-paths))])]]
         (when dropdown
           [:div.position-absolute.w-100 {:style {:z-index "100"}}
            [:input.form-control.rounded-0 {:placeholder "Поиск..."
                                            :ref         #(when (and % dropdown) (.focus %))
                                            :on-change   #(on-change (.. % -target -value))}]
            [:div.shadow.items
             (if-not (empty? items)
               (map-indexed
                (fn [idx {v :value d :display}] ^{:key idx}
                  [:li.list-group-item.rounded-0 {:on-click #(on-click v)}
                   d])
                items)
               [:li.list-group-item.rounded-0 "Ничего не найдено"])]])]))))

(defn combobox
  [form-path path & [attrs]]
  (let [node (rf/subscribe [:zf/node form-path path])]
    (fn [& _]
      (if @node
        [*combobox form-path path attrs]
        [:input.form-control]))))
