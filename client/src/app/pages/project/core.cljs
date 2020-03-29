(ns app.pages.project.core
  (:require [re-frame.core           :as rf]
            [app.pages.model         :as page]
            [app.styles :as style]
            [app.helpers :as h]
            [app.pages.project.model :as model]
            [app.components.chart.doughnut :as doughnut]
            [app.components.mail.core     :as mail]
            [app.components.breadcrumb.core :as breadcrumb]))

(page/reg-subs-page
 model/show-page
 (fn [{:keys [regionals payments f-payments] {:keys [name img]} :national :as page} {:keys [id]} {:keys [auth?]}]
   [:<>
    [:div.header.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     {:style {:background-image (str "url(http://localhost:8990" img ")")}}
     [:span.mask.bg-gradient-default.opacity-8]
     [:div.container-fluid.d-flex.align-items-center
      [:div.col-lg-7.col-md-9
       [:h1.display-4.text-white "Национальный проект"]
       [:h1.display-2.text-white name]]
      (when (seq payments)
        [:div.row
         {:key (str (hash payments) (hash f-payments))}
         [:div.card-body.px-lg-7
          {:style {:z-index "100"
                   :position "relative"}}
          [:div {:style {:position "absolute"
                         :color "#fff"
                         :margin "auto"
                         :height "45%"
                         :width "30%"
                         :font-size "35px"
                         :font-weight "bold"
                         :text-align "center"
                         :line-height "15px"
                         :top "0" :left "0"
                         :bottom "0" :right "0"}}
           [:span (.toFixed (reduce + (map :total (vals payments))) 1) [:br]
            [:span
             {:style {:font-size "15px"}}
             "млн. руб."]]]
          [doughnut/component {:key     "123"
                               :type    "doughnut"
                               :_style  {:width  "220px"
                                         :height "220px"}
                               :stacked true
                               :options {:legend {:display false}}
                               :data    {:labels   (keys payments)
                                         :datasets [{:data (mapv :total (vals payments))
                                                     :backgroundColor (mapv :color (vals payments))}
                                                    {:data (mapv :total (vals f-payments))
                                                     :backgroundColor (mapv :color (vals f-payments))}]}}]
          [:div.table-responsive.mt-3.card
           [:table.table.align-items-center.text-white
            [:thead.thead-light
             [:tr
              [:th "Бюджет"]
              [:th "План"]
              [:th "Факт"]]]
            [:tbody
             [:tr
              [:th {:style {:color "red"}} "Федеральный"]
              [:td {:style {:color "red"}} (get-in payments ["Федеральный" :total] 0)]
              [:td {:style {:color "red"}} (get-in f-payments ["Федеральный" :total] 0)]]
             [:tr
              [:th {:style {:color "blue"}} "Региональный"]
              [:td {:style {:color "blue"}} (get-in payments ["Региональный" :total] 0)]
              [:td {:style {:color "blue"}} (get-in f-payments ["Региональный" :total] 0)]]
             [:tr
              [:th {:style {:color "purple"}} "Муниципальный"]
              [:td {:style {:color "purple"}} (get-in payments ["Муниципальный" :total] 0)]
              [:td {:style {:color "purple"}} (get-in f-payments ["Муниципальный" :total] 0)]]
             [:tr
              [:th {:style {:color "#fba000"}} "Внебюджет"]
              [:td {:style {:color "#fba000"}} (get-in payments ["Внебюджет" :total] 0)]
              [:td {:style {:color "#fba000"}} (get-in f-payments ["Внебюджет" :total] 0)]]]]]]])]
     [:div.container-fluid
      [:div.row.align-items-center.py-4
       [:div.col-lg-6.col-7
        #_[breadcrumb/component-with-sub]
        ;; exception - :href is not ISeqable
        ]
       (when auth?
         [:div.col-lg-6.col-5.text-right
          [:a.btn.btn.btn-neutral {:href (str "#/project/" id "/edit")} "Редактировать"]])]]]

    [:div.container
     [:div.row.align-items-center.py-4.justify-content-between
      [:h3.display-3 "Региональные проекты"]
      (when auth?
        [:a.btn.btn.btn-neutral {:href (str "#/project/" id "/regional/create")} "Создать региональный проект"])]
     (map-indexed
      (fn [idx {:keys [period payment] :as item}] ^{:key idx}
        [:a.card {:href (str "#/project/" id "/regional/" (:id item))}

         [:div.card-header
          {:style {:justify-content :space-between
                   :display :flex}}
          [:h5.h3.mb-0 (:name item)]
          (when period
            [:h5.h3.mb-0
             (when (and (:start period) (:end period))
               (str (:start period) " - "))
             (when-let [end (:end period)]
               end)])]
         [:div.card-body
          [:div.card-body
           [:div.d-flex.justify-content-between
            [:div.row.align-items-center
             [:div.col-auto
              [:span.avatar.avatar-xl.rounded-circle
               [:img
                {:src (str "http://localhost:8990" (-> item :author :resource :photo))
                 :alt (-> item :author :display)}]]]
             [:div.col.ml--2
              [:small.mb-0 "Руководитель проекта"]
              [:h4.mb-0 (-> item :author :display)]
              [:p.text-sm.text-muted.mb-0 (-> item :author :resource :position)]]]
            [:div.col-aut
             (when (seq payment)
               [:<>
                [:div.row
                 [:div.col-auto.align-self-center
                  [:span.mb-1
                   {:style {:text-align "center"
                            :font-weight :bold
                            :width "100%"}}
                   (.toFixed (reduce + (map :total (vals payment))) 1)
                   " млн.руб."]
                  [:br][:br]
                  [:div
                   (map-indexed
                    (fn [idx [k v]]  ^{:key idx}
                      [:h4 [:span {:style {:color (:color v)}} "●"] (str " " k)])
                    payment)]]
                 [:div.col-auto.pb-4
                  [doughnut/component {:key     idx
                                       :_style  {:width  "120px"
                                                 :height "120px"}
                                       :type    "doughnut"
                                       :stacked true
                                       :options {:legend {:display false}}
                                       :data    {:labels   (keys payment)
                                                 :datasets [{:data (mapv :total (vals payment))
                                                             :backgroundColor (mapv :color (vals payment))}]}}]]]])]]]]])
      regionals)]]))

(page/reg-subs-page
 model/show-regional
 (fn [{:keys [project purposes events]} {:keys [id reg-id] :as s} {:keys [auth?]}]
   [:<>
    [:div.header.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     {:style {:background-image (str "url(http://localhost:8990" (get-in project [:project :resource :img]) ")")}}
     [:span.mask.bg-gradient-default.opacity-8]
     [:div.container-fluid.d-flex.align-items-center
      [:div.col-lg-7.col-md-9
       [:h1.display-4.text-white "Региональный проект"]
       [:h1.display-2.text-white (:name project)]
       [:p.text-white.mt-0.mb-5 (:description project)]
       (let [period (:period project)]
         [:b.text-warning.mt-0.mb-5
          (:start period)
          (when (and (:start period)
                     (:end period))
            " - ")
          (:end period)])]]
     [:div.container-fluid.d-flex.justify-content-end
      [:div.align-items-end.py-4.row
       [:div.col-lg-6.col-5.text-right
        [mail/button {:id reg-id :resourceType "Project"}]]
       (when auth?
         [:div.col-lg-6.col-5.text-right
          [:a.btn.btn.btn-neutral {:href (str "#/project/" id "/regional/" reg-id "/edit")} "Редактировать"]])]]]
    [:div.container
     [:div
      [:div.card-header.bg-transparent.row.align-items-center
       {:style {:justify-content :space-between}}
       [:h3.mb-0 "Показатели"]
       (when auth?
         [:a.btn {:href (str "#/project/" id "/regional/" reg-id "/purpose/create")} "Добавить показатель"])]
      [:div.card-body

       [:div.timeline.timeline-one-side
        (map-indexed
         (fn [idx item] ^{:key idx}
           [:div.timeline-block
            [:span.timeline-step.badge-success
             [:i.ni.ni-bell-55]]
            [:div.timeline-content {:style {:max-width "100%"}}
             [:h3.display-6.font-weight-bold.pb-3 (:name item)]
             (map-indexed
              (fn [idxx ind] ^{:key idxx}
                (let [cur  (h/parseInt (:current ind))
                      plan (h/parseInt (:planning ind))
                      proc (str (* (/ cur plan) 100.0) "%")
                      completed? (= cur plan)]
                  [:div.row.pb-5 {:style {:height "90px"}}
                   [:div.col-xl-3.col-md-6 [:h4.display-4.p-0.m-0 (:year ind)]]
                   [:div.position-relative.w-100.col-xl.col-md-6
                    [:div.progress-bar.position-absolute.rounded.align-items-end
                     {:class (if completed? "bg-success" "bg-orange")
                      :style {:width   proc
                              :z-index "10"
                              :height  "100%"}}
                     (when-not completed?
                       [:h4.m-0.text-dark.pr-3 {:style {:z-index "20"}} cur])]
                    [:div.progress-bar.bg-light.position-absolute.rounded.align-items-end
                     {:style {:width  "100%"
                              :height "100%"}}
                     [:h4.m-0.pr-3 {:style {:z-index "20"}} plan]]]]))
              (:indicators item))]
            (when auth?
              [:div.d-flex.justify-content-end
               [:a.font-weight-bold.p-0.text-muted
                {:href (h/href "project" id "regional" reg-id "purpose" (:id item) "edit")} "Редактировать"]])])
         purposes)]]]
     [:div
      [:div.card-header.bg-transparent.row.align-items-center
       {:style {:justify-content :space-between}}
       [:h3.mb-0 "Результаты"]
       (when auth?
         [:a.btn {:href (str "#/project/" id "/regional/" reg-id "/event/create")} "Добавить результат"])]
      [:div.list-group.list-group-flush.mb-5
       (style/styles
        [:.name
         [:&:hover {:text-decoration "underline"}]])
       (map-indexed
        (fn [idx item]
          (let [percent (.toFixed (* (/ (h/parse-int (get-in item [:task :complete]))
                                        (h/parse-int (get-in item [:task :target]))) 100) 1)]
            [:div.list-group-item.list-group-item-action.pointer.name
             {:key idx
              :style    {:width "100%" :display :flex :justify-content :space-between
                         :background (str "linear-gradient(to right, "
                                          (cond
                                            (< percent 30) "#ff6c6cb3"
                                            (< percent 50) "#ffdb6c"
                                            (< percent 100) "rgb(221, 255, 115)"
                                            :else "rgb(0, 255, 48)") " " percent "%, white 0%)")}
              :on-click #(rf/dispatch [:zframes.redirect/redirect {:uri (str "#/project/" id "/regional/" reg-id "/event/" (:id item))}])}
             [:div
              [:small.text-muted.font-weight-bold (get-in item [:period :end])]
              [:h5.mb-0 (:name item)]]
             [:div
              [:span.font-weight-bold (get-in item [:task :target]) " " (get-in item [:task :unit])]
              [:br]
              [:span.text-muted.font-weight-bold (get-in item [:task :complete]) " " (get-in item [:task :unit])]]]))
        events)]]]]))
