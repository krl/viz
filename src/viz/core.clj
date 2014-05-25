(ns viz.core
  (:use hiccup.core))

(defprotocol Visualize
  (visualize [this]))

(defn- visualize-value [style value]
  (html
   [:head
    [:title "viz"]
    [:style style]]    
   [:body
    (visualize value)]))

(defn- visualize-map [this]
  [:div.map
   [:table
    (map (fn [[key value]]
           [:tr
            [:td (visualize key)]
            [:td " : "]
            [:td (visualize value)]])
         this)]])

(extend-protocol Visualize
  java.lang.Long
  (visualize [this]
    [:span (str this)])

  clojure.lang.Keyword
  (visualize [this]
    [:span.keyword ":" (name this)])

  clojure.lang.PersistentVector
  (visualize [this]
    [:div.vector 
     [:span "[ "]
     (map visualize this)
     [:span "[ "]])
  clojure.lang.PersistentList
  (visualize [this]
    [:div.list
     [:span "( "]
     (map visualize this)
     [:span " )"]])
  clojure.lang.PersistentArrayMap
  (visualize [this] (visualize-map this))

  clojure.lang.IRecord
  (visualize [this]
    [:div.record
     [:span.type (str (type this))]
     (visualize-map this)]))

(defn write-html [path style-path value]
  (spit
   path
   (visualize-value (slurp (or style-path "style.css")) value)))
