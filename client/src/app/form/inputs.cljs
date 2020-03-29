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
        open-dropdown  (fn [dropdown]
                         (rf/dispatch  [(:on-search @node) {:path path :form-path form-path}])
                         (rf/dispatch [:zf/dropdown form-path path (not dropdown)]))]
    (fn [& _]
      (let [{:keys [items display-paths value dropdown]} @node]
        [:div.position-relative #_{:on-blur close-dropdown}
         [:div.input-group {:on-click #(open-dropdown dropdown)}
          [:span.form-control
           (if (empty? value)
             [:span.text-muted placeholder]
             [:span (str/join " " (mapv
                                   (fn [path] (get-in value path))
                                   display-paths))])]]
         (when dropdown
           [:div.position-absolute.w-100 {:style {:z-index "900"}}
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

(defn z-dropdown
  [form-path path & [{:keys [data placeholder]}]]
  (let [node (rf/subscribe [:zf/node form-path path])
        open-dropdown #(rf/dispatch [:zf/dropdown form-path path true])
        on-click (fn [i]
                   (rf/dispatch [:zf/set-value form-path path (:value i)])
                   (rf/dispatch [:zf/dropdown form-path path false])
                   (when (:on-click @node)
                     (rf/dispatch [(:on-click @node) (:value i)])))
        close-dropdown #(rf/dispatch [:zf/dropdown form-path path false])]
    (fn [_ _ & [{:keys [disabled]}]]
      (let [{:keys [value items errors validators dropdown] :as *node} @node
            items (if-let [i (:subscribtion items)] @(rf/subscribe i) items)
            items (get (first items) data items)
            display (when (or (and (= (:type @node) "boolean")
                                   (= false value))
                              value)
                      (some #(when (= value (:value %)) (:display %)) items))]
        [:div.position-relative {:tab-index 0
                                 :on-blur close-dropdown}
         [:div.input-group.pointer {:on-click open-dropdown}
          [:span.form-control
           (if-not display
             [:span.text-muted placeholder]
             [:span display])]
          (when (and value (not disabled))
            [:span {:key "clear-dropdown"
                    :style {:position "absolute"
                            :top "10px"
                            :right "10px"}
                    :on-mouse-down #(on-click {:value nil})}
             [:i.octicon.octicon-x]])]
         (when dropdown
           [:div.position-absolute.w-100 {:style {:z-index "100"}}
            [:div.shadow.items
             (if-not (empty? items)
               (map-indexed
                (fn [idx v] ^{:key idx}
                  [:li.list-group-item.rounded-0.pointer {:on-mouse-down #(on-click v)}
                   (:display v)])
                items)
               [:li.list-group-item.rounded-0 "Нет выбора"])]])]))))
