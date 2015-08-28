(ns pushy.uri
  (:import goog.Uri))

(defn new-uri 
  "Given a string return a Goog.Uri object"
  [string]
  (Uri. string))

(defprotocol IUri
  (path             [u])
  (path-and-after   [u])
  (query            [u])
  (domain           [u])
  (host             [u]))

(extend-protocol IUri
  Uri
  (path             [u] (.getPath           u))
  (path-and-after   [u] (.getPathAndAfter   u))
  (query            [u] (.getQuery          u))
  (domain           [u] (.getDomain         u))
  (host             [u] (.getHost           u))
  
  string
  (path   [s] (path   (Uri. s)))
  (query  [s] (query  (Uri. s)))
  (domain [s] (domain (Uri. s)))
  (host   [s] (host   (Uri. s))))

(defprotocol ILocation
  (href [l]))

(extend-protocol ILocation
;  js/window
;  (href [w] (.-href (.-location w)))
  
  js/Element
  (href [e] (.-href e)))
