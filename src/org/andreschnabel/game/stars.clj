(ns org.andreschnabel.game.stars
  (:require [org.andreschnabel.hotswap.utils :as utils]
            [org.andreschnabel.hotswap.globals :as globals]))

(defn init [num]
  (->> (range num) (map (fn [i] {:pos (utils/rand-pos)}))))

(defn update [{:keys [stars] :as state}]
  (letfn [(update-star [{:keys [pos] :as star}]
                       (let [moved-pos (map + pos [0 -2])
                             final-pos (if (utils/out-of-scr? moved-pos)
                                         [(first moved-pos) globals/scr-h] moved-pos)]
                         (assoc star :pos final-pos)))]
    (assoc state :stars (map update-star stars))))
