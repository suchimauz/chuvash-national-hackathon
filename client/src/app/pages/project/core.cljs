(ns app.pages.project.core
  (:require [re-frame.core           :as rf]
            [app.pages.model         :as page]
            [app.pages.project.model :as model]
            [app.components.chart.doughnut :as doughnut]
            [app.components.breadcrumb.core :as breadcrumb]))

(page/reg-subs-page
 model/show-page
 (fn [{:keys [regionals] {:keys [name img]} :national :as page} {:keys [id]} {:keys [auth?]}]
   [:<>
    [:div.header.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     {:style {:background-image (str "url(http://localhost:8990" img ")")}}
     [:span.mask.bg-gradient-default.opacity-8]
     [:div.container-fluid.d-flex.align-items-center
      [:div.col-lg-7.col-md-9
       [:h1.display-4.text-white "Национальный проект"]
       [:h1.display-2.text-white name]]
      [:div.row
       [:div.card-body.px-lg-7
        {:style {:z-index "100"}}
        [doughnut/component {:key     "123"
                             :type    "doughnut"
                             :_style  {:width  "220px"
                                       :height "220px"}
                             :stacked true
                             :options {:legend {:display false}}
                             :data    {:labels   ["Федеральный" "Региональные"]
                                       :datasets [{:data            [1000 500]
                                                   :backgroundColor ["#fba000" "#3853df"]}
                                                  {:data            [100 500]
                                                   :backgroundColor ["#fdc488" "#8693db"]}]}}]
        [:div.table-responsive.mt-3.card
         [:table.table.align-items-center.text-white
          [:thead.thead-light
           [:tr
            [:th "Бюджет"]
            [:th "План"]
            [:th "Факт"]]]
          [:tbody
           [:tr
            [:th {:style {:color "#fba000" }} "Федеральный"]
            [:td {:style {:color "#fba000" }} "100000"]
            [:td {:style {:color "#fdc488" }} "200000"]]
           [:tr
            [:th {:style {:color "#3853df" }}"Региональные"]
            [:td {:style {:color "#3853df" }} "100000"]
            [:td {:style {:color "#8693db" }} "200000"]]]]]]]]
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
      (fn [idx item]^{:key idx}
        [:a.card {:href (str "#/project/" id "/regional/" (:id item))}

         [:div.card-header [:h5.h3.mb-0 (:name item)]]
         [:div.card-body
          [:div.card-body
           [:div.d-flex.justify-content-between
            [:div.row.align-items-center
             [:div.col-auto
              [:span.avatar.avatar-xl.rounded-circle
               [:img
                {:src "http://localhost:8990/storage/download/test/1.png"
                 :alt "Image placeholder"}]]]
             [:div.col.ml--2
              [:h4.mb-0 "Руководитель проекта " (-> item :author :display)]
              [:p.text-sm.text-muted.mb-0 "министр здравоохранения Краснодарского края "]]]
            [:div.col-aut
             [:div.row
              [:div.col-auto.align-self-center
               [:div
                [:h4 [:span {:style {:color "#fba040"}} "●"] " Федеральные"]
                [:h4 [:span {:style {:color "#5e72e4"}} "●"] " Региональные"]]]
              [:div.col-auto.pb-4
               [doughnut/component {:key     idx
                                    :_style  {:width  "120px"
                                              :height "120px"}
                                    :type    "doughnut"
                                    :stacked true
                                    :options {:legend {:display false}}
                                    :data    {:labels   ["Федеральный" "Региональные"]
                                              :datasets [{:data            [(* 50 (* 2 (inc idx))) 500]
                                                          :label           "Users with address"
                                                          :backgroundColor ["#fba040" "#5e72e4"]}]}}]]]]]]]
         ])
      regionals)]]))

(page/reg-subs-page
 model/show-regional
 (fn [{:keys [project purposes events]} {:keys [id reg-id] :as s} {:keys [auth?]}]
   [:<>
    [:div.header.pt-8.pt-lg-8.pt-lg-9.rounded-bottom
     {:style {:background-image (str "url(http://localhost:8990" (:img project) ")")}}
     [:span.mask.bg-gradient-default.opacity-8]
     [:div.container-fluid.d-flex.align-items-center
      [:div.col-lg-7.col-md-9
       [:h1.display-4.text-white "Региональный проект"]
       [:h1.display-2.text-white (:name project)]
       [:p.text-white.mt-0.mb-5 (:description project)]
       (let [period (:period project)]
         [:b.text-warning.mt-0.mb-5 (:start period) " - " (:end period)])]]
     [:div.container-fluid.d-flex.justify-content-end
      [:div.align-items-end.py-4
       (when auth?
         [:div.col-lg-6.col-5.text-right
          [:a.btn.btn.btn-neutral {:href (str "#/project/" id "/regional/" reg-id "/edit")} "Редактировать"]])]]]
    [:div.container
     [:div
      [:div.card-header.bg-transparent [:h3.mb-0 "Результаты"]]
      [:a.btn {:href (str "#/project/" id "/regional/" reg-id "/purpose/create")} "Добавить показатель"]
      [:div.card-body
       [:div.timeline.timeline-one-side
        (map-indexed
         (fn [idx item] ^{:key idx}
           [:div.timeline-block
            [:span.timeline-step.badge-success [:i.ni.ni-bell-55]]
            [:div.timeline-content
             [:small.text-muted.font-weight-bold "10:30 AM"]
             [:h5.mt-3.mb-0 (:name item)]
             [:p.text-sm.mt-1.mb-0
              "Nullam id dolor id nibh ultricies vehicula ut id elit. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus."]
             [:div.mt-3
              [:span.badge.badge-pill.badge-success "design"]
              [:span.badge.badge-pill.badge-success "system"]
              [:span.badge.badge-pill.badge-success "creative"]]]])
         (range 2))]]]
     [:div
      [:div.card-header.bg-transparent.row.align-items-center
       {:style {:justify-content :space-between}}
       [:h3.mb-0 "Результаты"]
       [:a.btn {:href (str "#/project/" id "/regional/" reg-id "/event/create")} "Добавить результат"]]
      [:div.list-group.list-group-flush
       (map-indexed
        (fn [idx item] ^{:key idx}
          [:div.list-group-item.list-group-item-action.pointer
           {:style {:width "100%"}
            :on-click #(rf/dispatch [:zframes.redirect/redirect {:uri (str "#/project/" id "/regional/" reg-id "/event/" (:id item))}])}
           [:small.text-muted.font-weight-bold (:date item)]
           [:h5.mt-3.mb-0 (:name item)]])
        (range 2))]]
     ]]))
