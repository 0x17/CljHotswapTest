(ns org.andreschnabel.hotswap.utils
  (:require [org.andreschnabel.hotswap.globals :as globals])
  (:import (com.badlogic.gdx.math Vector2)
           (java.io File)))

(defn vec->gdxvec [[x y]] (Vector2. x y))
(defn gdxvec->vec [v] [(.x v) (.y v)])

(defn rand-pos []
  [(rand-int globals/scr-w)
   (rand-int globals/scr-h)])

(defn out-of-scr? [[x y]]
  (not (and (< 0 x globals/scr-w)
            (< 0 y globals/scr-h))))

(defmacro assign-from-map [obj m]
  `(do ~@(map (fn [k]
                (let [v (get m k)]
                  `(set! (~k ~obj) ~v)))
              (keys m))))

(defmacro coords [v & body]
  `(let [[~'x ~'y] ~v] ~@body))

(defn file-lst [path extension]
  (filter (fn [f] (.endsWith (.getName f) (str "." extension)))
          (.listFiles (File. path))))
