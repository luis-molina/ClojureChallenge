(ns problem2.core
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [invoice-spec]
            [java-time.api :as jt]))

(defn map-key [key]
  (case key
    "issue_date" :invoice/issue-date
    "customer" :invoice/customer
    "items" :invoice/items
    "price" :invoice-item/price
    "quantity" :invoice-item/quantity
    "sku" :invoice-item/sku
    "taxes" :invoice-item/taxes
    "company_name" :customer/name
    "email" :customer/email
    "tax_category" :tax/category
    "tax_rate" :tax/rate
    (keyword key)))

(defn date-string->instant [date-string]
  (jt/instant (jt/zoned-date-time (jt/local-date "dd/MM/yyyy" date-string) "UTC")))

(defn convert-values [key value]
  (case key
    :tax/category (keyword (.toLowerCase value))
    :tax/rate (double value)
    :invoice/issue-date (date-string->instant value)
    value))

(defn json->map [json-string]
  (json/read-str json-string
                 :value-fn convert-values
                 :key-fn map-key))

(defn file->map [file-name]
  (->> (slurp file-name)
       (json->map)
       (:invoice)))

(defn run []
  (println (file->map "invoice.json"))
  (println (s/valid? :invoice-spec/invoice (file->map "invoice.json"))))

(defn -main [& args]
  (run))
