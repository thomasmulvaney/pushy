(ns pushy.test.uri
  (:require-macros
   [cemerick.cljs.test :refer (is deftest done use-fixtures)])
  (:require
   [pushy.uri :as uri]
   [cemerick.cljs.test :as t]))

(deftest domain
  (is (= "kibu.com.au" (uri/domain "http://kibu.com.au")))
  (is (= "kibu.com.au" (uri/domain "http://kibu.com.au/foo")))
  (is (= "kibu.com.au" (uri/domain "http://kibu.com.au/foo?bar"))))
