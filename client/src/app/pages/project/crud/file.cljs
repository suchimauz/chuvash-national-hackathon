(ns app.pages.project.crud.file
  (:require [re-frame.core   :as rf]
            [app.helpers     :as h]
            [zframes.flash   :as flash]
            [clojure.string  :as str]))

(def ^:const folder "guide")

(defn file-name [f]
  (or (some-> (.-name f)
              h/transliterate (str/replace #" " "_"))
      "untitled"))

(rf/reg-event-fx
 ::upload
 (fn [_ [_ file {:keys [success]}]]
   (let [folder "project"]
     (letfn [(make-attachment [file]
               {:url         (str "/storage/download/" folder "/" (file-name file))
                :title       (file-name file)
                :size        (.-size file)
                :contentType (when-not (str/blank? (.-type file))
                               (.-type file))})]
       {:dispatch    [::flash/add-flash {:msg "Загрузка..." :status :primary :id :file-load}]
        :file/upload {:uri     "/storage/upload"
                      :method  "POST"
                      :name    (file-name file)
                      :folder  folder
                      :body    file
                      :success {:event  success
                                :params (make-attachment file)}
                      :error   {:event ::error}}}))))

(rf/reg-event-fx
 ::error
 (fn []
   {:dispatch-n [[:zframes.flash/remove-flash :file-load]
                 [:flash/danger {:msg "Файл не загружен"}]]}))
