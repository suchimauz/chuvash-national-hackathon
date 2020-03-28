(ns app.form.mask
  (:require [jslib.imask]))

(def mask-date
  {:mask   "day.month.year"
   :blocks {:day   {:mask (aget js/IMask "MaskedRange")
                    :from "0"
                    :to   "31"}
            :month {:mask (aget js/IMask "MaskedRange")
                    :from "1"
                    :to   "12"}
            :year  {:mask (aget js/IMask "MaskedRange")
                    :from "1900"
                    :to   "2100"}}})

(def mask-date-time
  {:mask   "day.month.year block60:block60"
   :blocks {:day   {:mask (aget js/IMask "MaskedRange")
                    :from "0"
                    :to   "31"}
            :month {:mask (aget js/IMask "MaskedRange")
                    :from "1"
                    :to   "12"}
            :year  {:mask (aget js/IMask "MaskedRange")
                    :from "1900"
                    :to   "2100"}
            :block60 {:mask (aget js/IMask "MaskedRange")
                      :from "0"
                      :to   "59"}}})

(defn mask-resolve [mask value]
  (when value
    (let [masked (.createMask js/IMask (clj->js mask))]
      (.resolve masked value)
      (aget masked "value"))))
