(ns zframes.modal
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :modal
 (fn [db [_ modal]]
   (assoc db :modal modal)))

(rf/reg-sub
 :modal
 (fn [db _]
   (:modal db)))

(defn modal []
  (let [modal* (rf/subscribe [:modal])]
    (fn []
      (when-let [modal @modal*]
        [:<>
         [:div.modal-backdrop.fade.show]
         [:div.modal.fade.show.d-block {:style {:z-index "1050"}}
          [:div.modal-dialog.modal-dialog-centered {:style (:style modal)}
           [:div.modal-content
            [:div.modal-header
             [:h6#modal-title-default.modal-title (:title modal)]
             [:button.close {:type "button"
                             :on-click #(do (when-let [close (:close modal)] (close))
                                            (rf/dispatch [:modal nil]))}
              [:span "×"]]]
            [(:body modal)]
            [:div.modal-footer.align-self-start
             (when-let [accept (:accept modal)]
               [:button.btn.btn-primary {:on-click #(do (when-let [accept-fn (:fn accept)] (accept-fn))
                                                        (when-not (:validation modal)
                                                          (rf/dispatch [:modal nil])))}
                (:text accept)])]]]]]))))

(defn confirm-delete [dispatch]
  {:title      "Подтвердите действие"
   :body       "Вы уверены что хотите удалить?"
   :accept     {:text "Да"
                :fn   (fn []
                        (rf/dispatch dispatch))}
   :cancel     {:text "Нет"}
   :persistent true})
