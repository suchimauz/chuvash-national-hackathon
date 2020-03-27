(ns app.helpers
  (:require [clojure.string :as str]
            [app.routes :as routes]
            [clojure.walk :as w]
            [re-frame.core :as rf]
            [chrono.core :as ch]
            [chrono.mask :as mask]
            [chrono.util :as util]
            [chrono.now :as now]
            [route-map.core :as route-map]
            [clojure.set :as set]
            [zframes.mapper :as zm]
            #?(:clj [clojure.test :as t])
            #?(:clj  [clojure.pprint]
               :cljs [cljs.pprint])
            #?(:cljs [goog.string])
            #?(:cljs [goog.string.format]))
  #?(:cljs (:import [goog.async Debouncer])))

(def userinfo-path [:xhr :req :zframes.auth/userinfo :data])

(defn format-str [fmt & args]
  (apply
   #?(:clj  clojure.core/format
      :cljs goog.string/format)
   fmt
   args))

(defn pprint [x]
  (#?(:clj  clojure.pprint/pprint
      :cljs cljs.pprint/pprint)
   x))

(defn p
  "Pretty prints arg and returns it.
  Useful for debug, especially in ->> and -> marcros'"
  [arg & meta]
  (when meta (apply println meta))
  (pprint arg)
  arg)

(def trans-table
  {"а" "a",  "б" "b", "в" "v",  "г" "g",  "д" "d",    "е" "e", "ё" "yo", "ж" "zh", "з" "z", "и" "i",  "й" "y"
   "к" "k",  "л" "l", "м" "m",  "н" "n",  "о" "o",    "п" "p", "р" "r",  "с" "s",  "т" "t", "у" "u",  "ф" "f"
   "х" "kh", "ц" "ts" "ч" "ch", "ш" "sh", "щ" "shch", "ь" "",  "ы" "y",  "ъ" "",  "э" "e", "ю" "yu", "я" "ya"
   "А" "A",  "Б" "B", "В" "V",  "Г" "G",  "Д" "D",    "Е" "E", "Ё" "YO", "Ж" "ZH", "З" "Z", "И" "I",  "Й" "Y"
   "К" "K",  "Л" "L", "М" "M",  "Н" "N",  "О" "O",    "П" "P", "Р" "R",  "С" "S",  "Т" "T", "У" "U",  "Ф" "F"
   "Х" "KH", "Ц" "TS" "Ч" "CH", "Ш" "SH", "Щ" "SHCH", "Ь" "",  "Ы" "Y",  "Ъ" "",  "Э" "E", "Ю" "YU", "Я" "YA"})

(defn transliterate [cs]
  (str/replace cs
               (re-pattern (str "(?ui)(?:" (str/join "|" (keys trans-table)) ")"))
               (fn [c] (get trans-table c c))))

(def parseInt #?(:clj  (fn [^String s] (Integer/parseInt s))
                 :cljs (fn [^String s] (js/parseInt s))))

(def parseFloat #?(:clj  (fn [^String s] (Float/parseFloat s))
                   :cljs (fn [^String s] (js/parseFloat s))))

(defn parse-int [s]
  (when-let [x (re-matches #"[-+]?\d+" (str s))]
    (parseInt x)))

(defn parse-float [s]
  (when-let [x (re-matches #"[-+]?\d+(?:\.\d+)?" (str s))]
    (parseFloat x)))

(defn gen-uuid []
  (str #?(:clj (java.util.UUID/randomUUID)
          :cljs (random-uuid))))

(defn hmap-to-items [m]
  (map (fn [[k v]] {:value k :display v}) m))

(defn get-defaults [schema]
  (reduce-kv (fn [acc k v]
               (if-let [v (:default v)]
                 (assoc acc k v)
                 acc))
             {}
             (:fields schema)))

(defn get-in-contained [contained m]
  (->> contained
       (filter (comp #{(:localRef m)} :id))
       first))

(defn abs [x] (Math/abs x))

(defn code-search
  "Finds code in vector of hmaps which is equal to one
   of the values provided with descending prioty"
  [code values coll]
  (some (into {} (map (juxt code identity) coll))
        values))

(defn try-code-search
  "Finds code in vector of hmaps which is equal to one
   of the values provided with descending prioty or
   returns first from the coll"
  [code values coll]
  (or (code-search code values coll)
      (first coll)))

(defn identity-default [default value]
  (if (str/blank? value) default value))

(def identity-default-na
  (partial identity-default "Нет данных"))

(defn address-without-region [str]
  (when str
    (-> str
        (str/replace #"^([0-9]{6}, )?(Чувашия Чувашская Республика -,|Чувашская Республика,|Чувашская Республика - Чувашия, )" "")
        str/trim)))

(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (->> (str/split (str s) #" ")
       (map str/capitalize)
       (str/join " ")))

(defn to-query-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (str/join "&")))

(defn href [& parts]
  (let [params (if (map? (last parts)) (last parts) nil)
        parts  (if params (butlast parts) parts)
        url    (str "/" (str/join "/" (map (fn [x] (if (keyword? x) (name x) (str x))) parts)))]
    (when-not  (route-map/match [:. url] routes/routes)
      (println (str url " is not matches routes")))
    (str "#" url (when params (str "?" (to-query-params params))))))

(def href-coll (partial apply href))

(defmacro when-let*
  [bindings & body]
  `(let ~bindings
     (if (and ~@(take-nth 2 bindings))
       (do ~@body))))

(defn href-add [href & parts]
  (some-> href
          (->> (re-seq #"[^#/\\]+"))
          vec
          (into parts)
          href-coll))

(defn dissoc-in
  [obj path]
  (cond
    (empty? path)
    obj
    (= (count path) 1)
    (dissoc obj (first path))
    :else
    (update-in obj (drop-last path) dissoc (last path))))

(defn vector-to-hash-map [v]
  (if (or (vector? v) (list? v) (map? v))
    (reduce-kv #(assoc %1 %2 (vector-to-hash-map %3)) {} v)
    v))

(defn hash-map-to-vector [v]
  (if (map? v)
    (if (every? number? (keys v))
      (reduce-kv #(conj %1 (hash-map-to-vector %3)) [] v)
      (reduce-kv #(assoc %1 %2 (hash-map-to-vector %3)) {} v))
    v))

(defn format-full-name [given middle family]
  (let [r (remove nil? [family given middle])]
    (when (seq r) (str/join " " r))))

(defn format-short-name [given middle family]
  (let [r (remove nil? [family
                        (when given (str (first given)  "."))
                        (when middle (str (first middle) "."))])]
    (when (seq r) (str/join " " r))))

(defn user-format-full-name [{{:keys [givenName middleName familyName]} :name}]
  (format-full-name givenName middleName familyName))

(defn user-format-short-name [{{:keys [givenName middleName familyName]} :name}]
  (format-short-name givenName middleName familyName))

(defn format-full-human-name [{:keys [family], [given middle] :given}]
  (format-full-name given middle family))

(defn format-short-human-name [{:keys [family], [given middle] :given}]
  (format-short-name given middle family))

(defn display-with-clarification [display clarification]
  (let [r (if-not (str/blank? display)
            (str display
                 (when-not (str/blank? clarification)
                   (str " (" clarification ")")))
            clarification)]
    (when-not (str/blank? r) r)))

(defn fio [name]
  (->> name
       (into {})
       ((juxt :family
              #(get-in % [:given 0])
              #(get-in % [:given 1])))
       (str/join " ")
       capitalize-words
       identity-default-na))

(defn telecom [telecom types]
  (->> telecom
       (code-search :use types)
       :value
       identity-default-na))

(defn distinct-by [fns coll]
  (->> coll
       (group-by (apply juxt fns))
       vals
       (map first)))

(defn reg-event-fx-with-ctx [pid ctxs handler]
  (rf/reg-event-fx
   pid
   (fn [{:keys [db] :as state} args & rest-args]
     (when (every? (comp #{:done} (partial get (:ctx-statuses db))) ctxs)
       (apply handler
              state
              (update args 1 #(if (contains? (set ctxs) %)
                                :init
                                %))
              rest-args)))))

(rf/reg-event-fx
 ::redirect
 (fn [_ [_ uri]]
   {:dispatch [:zframes.redirect/redirect {:uri uri}]}))

(rf/reg-event-db
 :xhr/loaded
 (fn [db [_ {:keys [data]} {:keys [pid key]}]]
   (-> db
       (assoc-in [pid :loading] false)
       (assoc-in [pid key]      data))))

(rf/reg-event-db
 :xhr/failed
 (fn [db [_ _ {:keys [pid]}]]
   (-> db
       (assoc-in [pid :loading] false)
       (assoc-in [pid :status] :error))))

(defn redirect-search
  ([search]
   (rf/dispatch [:zframes.redirect/set-params {:search search}]))
  ([params search]
   (rf/dispatch [:zframes.redirect/set-params (if (str/blank? search)
                                                (dissoc params :search)
                                                (assoc params :search search))])))

(defn patch [schema mappings old-resource new-resource]
  (let [empty-data         {:collection []
                            :string     ""
                            :object     {}}
        empty-form         (into {} (map (fn [[field props]] {field (get empty-data (:type props))})
                                         (:fields schema)))
        form-resource-keys (keys (zm/export empty-form mappings))]
    (merge (apply dissoc old-resource
                  :meta
                  (set/difference (set form-resource-keys)
                                  (set (keys new-resource))))
           new-resource)))

(def date-fmt
  {:iso [:year "-" :month "-" :day]
   :ru  [:day "." :month "." :year]})

(def time-fmt [:hour ":" :min ":" :sec])

(def iso-fmt [:year "-" :month "-" :day "T" :hour ":" :min ":" :sec])

(def ru-fmt-with-time [:day "." :month "." :year " " :hour ":" :min ":" :sec])

(defn now-date-without-time []
  (ch/format (now/local) (:iso date-fmt)))

(defn prev-month-date-without-time []
  (ch/format (ch/+ (select-keys (now/local) [:year :month :day]) {:month -1}) (:iso date-fmt)))

(comment
  #?(:cljs (let [current (js/Date.) ;see next comment block
                 prev-month (js/Date. (.setMonth current (- (.getMonth current) 1)))]
             (some->> (clojure.string/split (.toLocaleDateString prev-month) #"\.")
                      reverse
                      (clojure.string/join "-")))
     :clj (ch/format (ch/+ (select-keys (now/local) [:year :month :day]) {:month -1}) (:iso date-fmt)))

  (ch/format (ch/+ (select-keys (now/local) [:year :month :day]) {:month -1}) (:iso date-fmt))

  (ch/format (ch/+ (now/local) {:month -1}) (:iso date-fmt))

  (= "2019-12-13" (ch/format (ch/+ (select-keys (now/local) [:year :month :day]) {:month -1}) (:iso date-fmt)))
  )

(defn prev-day-date-without-time []
  (ch/format (ch/+ (now/local) {:day -1}) (:iso date-fmt)))

(defn prev-day-date []
  (ch/format (ch/- (now/local) {:day 1}) iso-fmt))

(defn tomorrow-date-without-time []
  (ch/format (ch/+ (now/local) {:day +1}) (:iso date-fmt)))

(defn tomorrow-date []
  (ch/format (update (now/local) :day + 1) (:iso date-fmt)))

(defn ru-fmt-date-with-time []
  (ch/format (now/local) ru-fmt-with-time))

(defn now-date []
  (ch/format (now/local) iso-fmt))

(defn datetime-iso-format [pattern value]
  "Pattern like YYYY-MM-DDTHH:mm:ss"
  (if (or (str/blank? value) (str/blank? pattern))
    nil
    (let [t (ch/parse value iso-fmt)]
      (-> pattern
          (str/replace-first #"YYYY" (str (:year t)))
          (str/replace-first #"MM"   (format-str "%02d" (:month t)))
          (str/replace-first #"DD"   (format-str "%02d" (:day t)))
          (str/replace-first #"HH"   (format-str "%02d" (:hour t)))
          (str/replace-first #"mm"   (format-str "%02d" (:min t)))
          (str/replace-first #"ss"   (format-str "%02d" (:sec t)))))))

(defn date-iso->rus-format [value]
  (when value
    (ch/format (ch/parse value iso-fmt) (:ru date-fmt))))

(defn date-rus->iso-format [value]
  (when value
    (ch/format (ch/parse value (:ru date-fmt)) (:iso date-fmt))))

(defn date-chrono->iso-format [value]
  (when value
    (ch/format value (:iso date-fmt))))

(defn date-iso->chrono-format [value]
  (when value
    (ch/parse value (:iso date-fmt))))

(defn datetime-chrono->iso-format [value]
  (when value
    (ch/format value iso-fmt)))

(defn appointment-day [date]
  (some->> date
           (re-find #"^(\d{4})-(\d{2})-(\d{2})")
           rest
           vec
           (#(update % 0 subs 2 4))
           reverse
           (str/join ".")))

(def data-mask-get date-iso->rus-format)
(def ru-full-date date-iso->rus-format)
(def dmY-Hm date-iso->rus-format)

(defn full-date-post
  [date]
  (when date
    (ch/format (ch/parse  date
                          [:day "." :month "." :year " " :hour ":" :min])
               [:year "-" :month "-" :day "T" :hour ":" :min])))

(defn full-date-get
  [date]
  (when date
    (ch/format (ch/parse  date
                          [:year "-" :month "-" :day "T" :hour ":" :min])
               [:day "." :month "." :year " " :hour ":" :min])))

(defn data-mask-post [date]
  (when date
    (ch/format (ch/parse date (:ru date-fmt)) (:iso date-fmt))))

(defn date-iso->rus-datetime-format [value]
  (ch/format (ch/parse value)
             [:day "." :month "." :year " " :hour ":" :min ":" :sec]))

(defn rus-datetime->date-iso-format [value]
  (ch/format (ch/parse value
                       [:day "." :month "." :year " " :hour ":" :min ":" :sec])
             [:year "-" :month "-" :day "T" :hour ":" :min ":" :sec]))

(defn iso-date [date]
  (ch/format (ch/parse date) (:iso date-fmt)))

(defn day* [date]
  (ch/format (ch/parse date) [:day]))

(defn iso-today []
  (ch/format (now/today) (:iso date-fmt)))

(defn now []
  (ch/format (now/local) [:day \. :month \. :year]))

(defn iso-datetime-today []
  (ch/format (now/local) [:year \- :month \- :day \T :hour \: :min]))

(defn iso-datetime [date]
  (ch/format (assoc (ch/parse date) :sec 0)
             [:year \- :month \- :day \T :hour \: :min \: :sec]))

(defn time* [date]
  (ch/format (or (ch/parse date) (mask/parse date util/iso-fmt)) [:hour \: :min]))

(defn dmY-Hms [date]
  (ch/format (ch/parse date) [:day \. :month \. :year \space :hour \: :min]))

(defn vec->json-path [v]
  (str \{ (str/join \, v) \}))

(do
  #?@(:cljs [(defn ru-date [date]
               (.format (js/moment date) "DD MMMM "))

             (defn ru-date-with-year [date]
               (.format (js/moment date) "DD MMMM YYYY"))

             (defn slot-time [date]
               (.format (js/moment date) "dd. DD.MM в HH:mm"))

             (defn day-of-year [date]
               (.format (js/moment date) "DDD"))

             (defn first-day-of-week []
               (.startOf (js/moment.) "isoweek"))

             (defn month-text
               ([]  (.format (js/moment.) "MMMM YYYY"))
               ([d] (.format d "MMMM YYYY")))

             (defn month
               ([]  (.format (js/moment.) "YYYY MM"))
               ([d] (.format d "YYYY MM")))

             (defn first-day-of-month-on-calendar
               ([]  (.startOf (.startOf (js/moment.) "month") "isoweek"))
               ([d] (.startOf (.startOf d "month") "isoweek")))

             (defn add-interval [from c d]
               (.add (js/moment from) c d))

             (defn remove-interval [from c d]
               (.add (js/moment from) (* -1 c) d))

             (defn next-month-from [from]
               (add-interval from 1 "M"))

             (defn prev-month-from [from]
               (remove-interval from 1 "M"))]))

(defn search-line-format
  [line]
  (some-> line
          (str/replace #"(\d{2})\.(\d{2})\.(\d{4})" "$3-$2-$1")))

(defn deep-merge
  "efficient deep merge"
  [a b]
  (loop [[[k v :as i] & ks] b
         acc a]
    (if (nil? i)
      acc
      (let [av (get a k)]
        (if (= v av)
          (recur ks acc)
          (recur ks (if (and (map? v) (map? av))
                      (assoc acc k (deep-merge av v))
                      (assoc acc k v))))))))

(defn calc-created-resources [old-resmap new-resource-type id new-resource]
  (when-not (get-in old-resmap [new-resource-type id])
    {:request  {:method "POST" :url (str "/" (name new-resource-type))}
     :resource new-resource}))

(defn calc-changed-resources [new-resmap old-resource-type id old-resource]
  (let [match (get-in new-resmap [old-resource-type id])]
    (cond (nil? match)
          {:request {:method "DELETE" :url (str "/" (name old-resource-type) "/" (name id))}}

          (not= match old-resource)
          {:request  {:method "PUT" :url (str "/" (name old-resource-type) "/" (name id))}
           :resource match})))

(defn reduce-resmap [resmap function]
  (reduce-kv (fn [acc resource-type resources]
               (->> resources
                    (reduce-kv (fn [acc id resource]
                                 (if-let [r (function resource-type id resource)]
                                   (conj acc r)
                                   acc))
                               [])
                    (into acc)))
             [] resmap))

(defn resmap-diff [old new]
  (let [changed (reduce-resmap old (partial calc-changed-resources new))
        created (reduce-resmap new (partial calc-created-resources old))]
    (into changed created)))

(defn resmap [{:keys [entry]}]
  (reduce
   (fn [acc {e :resource}]
     (let [e (dissoc e :meta)]
       (cond-> acc
         (:resourceType e)
         (assoc-in [(:resourceType e) (or (:id e) (gen-uuid))] e))))
   {}
   entry))

(defn batchify [old new]
  {:resourceType "Bundle"
   :type "transaction"
   :entry (resmap-diff (resmap old) (resmap new))})

(def search
  #?(:clj redirect-search
     :cljs (let [debouncer (Debouncer. redirect-search 400)]
             (fn [e] (->> e .-target .-value (.fire debouncer))))))


(defn practitionerrole-display [prr]
  (str/join " " (concat [(get-in prr [:derived :name 0 :family])]
                        (get-in prr [:derived :name 0 :given])
                        [(str "(" (get-in prr [:code 0 :text]) ")")]
                        (when-let [emp (get-in prr [:employment :display])]
                          [(str " " emp)]))))

(defn entry [data]
  (get-in data [:data :entry]))

(defn resources [{:keys [data]}]
  (if (= "Bundle" (:resourceType data))
    (map :resource (:entry data))
    (list data)))

(def period-format
  (comp (partial str/join  " - ")
        (partial map identity-default-na)
        (juxt (comp date-iso->rus-format :start)
              (comp date-iso->rus-format :end))))

(defn not-blank
  [attr content]
  (when-not (str/blank? attr)
    content))

(defn row-to-resource [{id :id st :status rt :resource_type cts :cts ts :ts txid :txid resource :resource :as row}]
  (when (and row (map? row) (or resource {}))
    (-> (cond-> resource
          id (assoc :id id)
          rt (assoc :resourceType rt))
        (update :meta (fn [x] (cond-> (or x {})
                               ts    (assoc :lastUpdated (#?(:clj .toString
                                                             :cljs str)
                                                          ts))
                               cts   (assoc :createdAt (#?(:clj .toString
                                                           :cljs str)
                                                        cts))
                               txid (assoc :versionId (str txid))))))))

(defn vec-remove
  "remove elem in coll
  source: https://stackoverflow.com/a/18319708"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn remove-displays
  #?(:clj
     {:test
      #(->> {:x 1, :display :foo
             :xs [{:x 2, :display :foo}
                  {:x 3, :display :foo}]}
            remove-displays
            (= {:x 1 :xs [{:x 2} {:x 3}]})
            t/is)})
  [m]
  (w/postwalk #(cond-> %
                 (and (map? %) (:display %))
                 (dissoc :display))
              m))

(defn get-reg-id [{ids :identifier}]
  (or
   (:value
    (first
     (->> ids
          (filter (fn [x]
                    (= (:system x) "urn:identity:Serial:ServiceRequest"))))))
   "Региональный ID организации не задан"))
