(ns invoice-item-test
  (:require [clojure.test :refer :all]
            [invoice-item :as ii]))

(deftest subtotal-test
         "Calculates the subtotal of an invoice-item taking a discount-rate into account."

         (testing "Checks for correctness in the subtotal function calculations given different inputs"
                  (is (double?
                        (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price 20 :invoice-item/discount-rate 10}))
                      "Returned value is in decimal format")
                  (is (= 90.0
                         (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price 20 :invoice-item/discount-rate 10}))
                      "Calculates correct subtotal given all parameters")
                  (is (= (* 5 12.34)
                         (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price 12.34 :invoice-item/discount-rate 0}))
                      "Subtotal equals quantity * price if discount rate is 0")
                  (is (= 100.0
                         (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price 20}))
                      "Default discount rate is 0 when not passed in the map"))

         (testing "Checks that exceptions are thrown if it receives invalid input"
           (is (thrown? NullPointerException
                        (ii/subtotal {}))
               "Map keys are required")
           (is (thrown? NullPointerException
                        (ii/subtotal {:invoice-item/precise-quantity 5}))
               "precise-price is required")
           (is (thrown? NullPointerException
                        (ii/subtotal {:invoice-item/precise-price 20}))
               "precise-quantity is required")
           (is (thrown? ClassCastException
                        (ii/subtotal {:invoice-item/precise-quantity "5" :invoice-item/precise-price 20 :invoice-item/discount-rate 10}))
                "precise-quantity should be a number")
           (is (thrown? ClassCastException
                        (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price "20" :invoice-item/discount-rate 10}))
               "precise-price should be a number")
           (is (thrown? ClassCastException
                        (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price 20 :invoice-item/discount-rate "10"}))
               "discount-rate should be a number")
           (is (thrown? Throwable
                        (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price 20 :invoice-item/discount-rate 101}))
               "discount-rate should not be > 100")
           (is (thrown? Throwable
                        (ii/subtotal {:invoice-item/precise-quantity 0 :invoice-item/precise-price 20 :invoice-item/discount-rate 10}))
               "precise-quantity should be > 0")
           (is (thrown? Throwable
                        (ii/subtotal {:invoice-item/precise-quantity 5 :invoice-item/precise-price -1 :invoice-item/discount-rate 10}))
               "precise-price should be >= 0")))
