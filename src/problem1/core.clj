(ns problem1.core)

(def invoice (clojure.edn/read-string (slurp "invoice.edn")))

(defn get-items [invoice]
  (:invoice/items invoice))

;;Item taxes validity
(defn valid-tax? [tax]
  (and (= (:tax/category tax) :iva) (= (:tax/rate tax) 19)))

(defn get-valid-taxes-count [invoice-item]
  (count (filter valid-tax?
                 (get invoice-item :taxable/taxes))))

(defn has-valid-taxes? [invoice-item]
  (> (get-valid-taxes-count invoice-item) 0))

;;Item retentions validity
(defn valid-retention? [retention]
  (and (= (:retention/category retention) :ret_fuente) (= (:retention/rate retention) 1)))

(defn get-valid-retentions-count [invoice-item]
  (count (filter valid-retention?
                 (get invoice-item :retentionable/retentions))))

(defn has-valid-retentions? [invoice-item]
  (> (get-valid-retentions-count invoice-item) 0))

;;Items validity
(defn condition-xor [condition-a condition-b]
  (or (and condition-a (not condition-b)) (and (not condition-a) condition-b)))

(defn valid-item? [invoice-item]
  "Determines if an invoice item is valid according to problem criteria"
  (condition-xor (has-valid-taxes? invoice-item) (has-valid-retentions? invoice-item)))

(defn filter-valid-items [invoice-items]
  (filter valid-item? invoice-items))

(defn process-invoice [invoice]
  "Process received invoice, returning those items that match desired criteria for taxes and retentions"
  (->> (get-items invoice)
       (filter-valid-items)))

(defn run []
  (println (process-invoice invoice)))

(defn -main [& args]
  (run))
