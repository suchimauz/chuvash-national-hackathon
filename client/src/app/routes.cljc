(ns app.routes)

(def routes
  {:.        :app.pages.home.model/index
   "login"   {:. :app.pages.login.model/index}
   "home"    {:. :app.pages.home.model/index
              :breadcrumb "Национальные проекты"}
   "project" {:.       :app.pages.project.model/index
              :breadcrumb "Национальные проекты"
              "create" {:. :app.pages.project.crud.model/create
                        :breadcrumb "Создание"}
              [:id]    {:.         :app.pages.project.model/show
                        :breadcrumb [:app.pages.project.model/breadcrumb]
                        "regional" {"create"       {:. :app.pages.project.crud.model/create-regional
                                                    :breadcrumb "Создание"}
                                    :breadcrumb "Региональные проекты"
                                    [:regional-id] {:.     :app.pages.project.model/show-regional
                                                    "edit" {:. :app.pages.project.crud.model/edit-regional
                                                            :breadcrumb "Редактирование"}
                                                    :breadcrumb [:app.pages.project.model/breadcrumb-regional]}}
                        "edit"     {:. :app.pages.project.crud.model/edit}}}
   "purpose" {:.       :app.pages.purpose.model/index
              "create" {:. :app.pages.purpose.crud.model/create}
              [:id]    {:.     :app.pages.purpose.model/show
                        "edit" {:. :app.pages.purpose.crud.model/edit}}}
   "event"   {:.       :app.pages.event.model/index
              "create" {:. :app.pages.event.crud.model/create}
              [:id]    {:.     :app.pages.event.model/show
                        "edit" {:. :app.pages.event.crud.model/edit}}}})
