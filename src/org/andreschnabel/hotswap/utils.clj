(ns org.andreschnabel.hotswap.utils
  (:require [org.andreschnabel.hotswap.globals :as globals])
  (:import (com.badlogic.gdx.math Vector2)))

(defn vec->gdxvec [[x y]] (Vector2. x y))
(defn gdxvec->vec [v] [(.x v) (.y v)])

(defn rand-pos []
  [(rand-int globals/scr-w)
   (rand-int globals/scr-h)])

(defn out-of-scr? [[x y]]
  (or (< x 0) (< y 0) (> x globals/scr-w) (> y globals/scr-h)))
