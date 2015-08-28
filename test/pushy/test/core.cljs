(ns pushy.test.core
  (:require-macros
   [cemerick.cljs.test :refer (is deftest done use-fixtures)])
  (:require
   [pushy.core :as pushy]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [cemerick.cljs.test :as t])
  (:import goog.history.Html5History))

;; Taken straight from domina's tests
(defn simulate-click-event
  [el]
  (let [document (.-document js/window)]
    (cond
      (.-click el) (.click el)
      (.-createEvent document) (let [e (.createEvent document "MouseEvent")]
                                 (println "Event: " e)
                                 (.initMouseEvent e "click" true true
                                                  js/window 0 0 0 0 0
                                                  false false false false 0 nil)
                                 (println "Event: " e)
                                 (.dispatchEvent el e))
      :default (throw "Unable to simulate click event"))))
(.log js/console (.-createWebPage phantom))
(secretary/set-config! :prefix "/")
(def test-val (atom false))
(def internal-val (atom false))

(def history
  (pushy/pushy secretary/dispatch!
               (fn [x] 
                 (println "Recieveid " x)
                 (when (secretary/locate-route x) x))
               identity))

(defroute foo-route "/foo" []
  (reset! test-val true))

(defroute bar-route "/bar" []
  (reset! test-val true))


(defroute bar-route "/internal-link" []
  (reset! internal-val true))

(deftest constructing-history
  (is (instance? Html5History (pushy/new-history))))

(deftest constructing-pushy
  (is (satisfies? pushy/IHistory (pushy/pushy (constantly nil) (constantly nil)))))

(deftest supported-browser
  (is (pushy/supported?)))

(deftest set-page-url
  ())
(deftest ^:async click-internal-link
  (let [a (.createElement js/document "a")]
    (set! (.-href a) "foo")
    (println (.-href a))
    (reset! internal-val false)
    (pushy/start! history)
    (println (.-location js/window))
    (simulate-click-event a)
    (println (.-location js/window))
    (js/setTimeout
      (fn []
        (is @internal-val)
        (is (nil? (pushy/stop! history)))
        (is (= "/foo" (pushy/get-token history)))
        (done))
      5000)))

#_(deftest click-external-link
  (let [a (.createElement js/document "a")]
    (set! (.-href a) "http://kibu.com.au/foo")
    (reset! test-val false)
    (pushy/start! history)
    (pushy/replace-token! history "/my-location")
    (simulate-click-event a)
    (js/setTimeout
      (fn []
        (is (false? @test-val))
        (is (nil? (pushy/stop! history)))
        (is (= "/my-location" (pushy/get-token history)))
        (done))
      5000)))

;; event listeners started = dispatch
#_(deftest ^:async push-state-foo-route
  (reset! test-val false)
  (pushy/start! history)
  (pushy/replace-token! history "/foo")
  (js/setTimeout
   (fn []
     (is @test-val)
     (is (nil? (pushy/stop! history)))
     (is (= "/foo" (pushy/get-token history)))
     (done))
   5000))

;; no event listeners started = no dispatch
#_(deftest ^:async push-state-bar-route
  (reset! test-val false)
  (pushy/replace-token! history "/bar")
  (js/setTimeout
   (fn []
     (is (false? @test-val))
     (is (= "/bar" (pushy/get-token history)))
     (done))
   5000))
