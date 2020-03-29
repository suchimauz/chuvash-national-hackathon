(ns app.components.mail.core
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))


(defn sub? [{:keys [id resourceType]} resources]
  (some (fn [item]
          (and (= id (:id item))
               (= resourceType (:resourceType item))))
        resources))


(defn unsign [{:keys [id resourceType]} resources]
  (remove (fn [item]
            (and (= id (:id item))
                 (= resourceType (:resourceType item))))
          resources))

(defn modal [resource]
  [:modal {:style {:max-width "500px"}
           :body  (let [mail (r/atom "")]
                    (fn []
                      [:div.container
                       [:div.form-group
                        [:label.form-control-label "Ваша почта"]
                        [:input.form-control {:value     @mail
                                              :on-change #(reset! mail (.. % -target -value))}]]
                       [:button.btn.btn-primary {:on-click #(rf/dispatch [::set-sub @mail resource])}
                        "Подписаться"]]))
           :title "Подписка на обновление"}])

(rf/reg-event-fx
 ::set-sub
 [(rf/inject-cofx :storage/get [:subs])]
 (fn [{storage :storage db :db} [_ mail resource]]
   {:db          (-> db
                     (assoc-in [:subs :mail] mail)
                     (update-in [:subs :resources] conj resource))
    :xhr/fetch   {:uri    "/Subscriber"
                  :method "POST"
                  :body   {:mail     mail
                           :resource resource}}
    :storage/set {:mail     mail
                  :subs-res (conj (:subs-res storage) resource)}
    :dispatch    [:modal nil]
    :flash/flash [:success {:msg "Вы успешно подписались на обновления" :title "Успешно!"}]}))


(rf/reg-event-fx
 ::un-sign
 [(rf/inject-cofx :storage/get [:subs])]
 (fn [{storage :storage db :db} [_ {:keys [id resourceType] :as res}]]
   (let [mail (get-in db [:subs :mail])]
     {:db          (-> db (update-in [:subs :resources] unsign))
      :storage/set {:subs-res (unsign res (get storage [:subs-res]))}
      ;; :xhr/fetch   {:uri    "/Subscriber"
      ;;               :method "DELETE"
      ;;               :params {:mail         mail
      ;;                        :id           id
      ;;                        :resourceType resourceType}}
      :flash/info  [:success {:msg "Вы успешно отписались на обновления" :title "Успешно!"}]})))

(rf/reg-event-fx
 ::sign
 (fn [{db :db} [_ resource]]
   (let [mail (get-in db [:subs :mail])]
     (if-not mail
       {:dispatch (modal resource)}
       (when-not (sub? resource (get-in db [:subs :resources]))
         {:dispatch [::set-sub mail resource]})))))

(rf/reg-sub
 :mail/sub?
 (fn [db [_ item]]
   (sub? item (get-in db [:subs :resources]))))

(defn button [resource]
  (r/create-class
   {:reagent-render
    (fn []
      (let [sub? (rf/subscribe [:mail/sub? resource])]
        (fn []
          (if @sub?
            [:a.btn.btn.btn-neutral {:on-click #(rf/dispatch [::un-sign resource])}
             "Отписаться"]
            [:a.btn.btn.btn-neutral {:on-click #(rf/dispatch [::sign resource])}
             "Подписаться"]))))}))
