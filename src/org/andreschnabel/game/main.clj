(ns org.andreschnabel.game.main
  (:require [org.andreschnabel.hotswap.globals :as globals]
            [org.andreschnabel.hotswap.utils :as utils]
            [org.andreschnabel.game.stars :as stars])
  (:import (com.badlogic.gdx Gdx Input Input$Keys Files)
           (com.badlogic.gdx.graphics.glutils ShapeRenderer ShapeRenderer$ShapeType)
           (com.badlogic.gdx.graphics GL10 Color Texture)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn init-game []
  {:sr    (ShapeRenderer.)
   :pos   [10 10]
   :sb    (SpriteBatch.)
   :tex   (Texture. (.internal Gdx/files "../../../../sprite1.png"))
   :stars (stars/init 128)})

(defn move [dx dy {:keys [pos] :as state}]
  (let [[x y] pos]
    (assoc state :pos [(+ x dx) (+ y dy)])))

(def key-action-map {Input$Keys/LEFT  (partial move -10 0)
                     Input$Keys/RIGHT (partial move 10 0)
                     Input$Keys/UP    (partial move 0 10)
                     Input$Keys/DOWN  (partial move 0 -10)})

(defn process-input [state]
  (letfn [(key-pressed? [key] (.isKeyPressed Gdx/input key))
          (process-key [acc key]
                       (if (key-pressed? key)
                         ((get key-action-map key) acc)
                         acc))]
    (when (key-pressed? Input$Keys/ESCAPE)
      (.exit Gdx/app))
    (reduce process-key state (keys key-action-map))))

(defn update-game [state]
  (->> state (stars/update) (process-input)))

(defn render-game [{:keys [sr pos sb tex stars]}]
  (letfn [(clear-scr []
                     (.glClearColor Gdx/gl 0 0 0 1)
                     (.glClear Gdx/gl GL10/GL_COLOR_BUFFER_BIT))

          (draw-circles []
                        (.setColor sr Color/RED)
                        (doseq [x (range 0 globals/scr-w 100)]
                          (doseq [y (range 0 globals/scr-h 100)]
                            (.circle sr x y 100))))

          (draw-square []
                       (.setColor sr Color/YELLOW)
                       (let [[x y] pos]
                         (.rect sr x y 100 100)))

          (draw-stars []
                      (.setColor sr Color/WHITE)
                      (.begin sr ShapeRenderer$ShapeType/Filled)
                      (doseq [{[x y] :pos} stars]
                        (.circle sr x y 10))
                      (.end sr))

          (draw-shapes []
                       (.begin sr ShapeRenderer$ShapeType/Line)
                       (draw-circles)
                       (draw-square)
                       (.end sr)
                       (draw-stars))

          (draw-img []
                    (.begin sb)
                    (let [[x y] (map float pos)]
                      (.draw sb tex x y))
                    (.end sb))]

    (clear-scr)
    ;(draw-shapes)
    (draw-img)))
