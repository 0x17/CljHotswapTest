(ns org.andreschnabel.game.main
  (:require [org.andreschnabel.hotswap.globals :as globals]
            [org.andreschnabel.hotswap.utils :as utils]
            [org.andreschnabel.game.stars :as stars])
  (:import (com.badlogic.gdx Gdx Input Input$Keys Files)
           (com.badlogic.gdx.graphics.glutils ShapeRenderer ShapeRenderer$ShapeType)
           (com.badlogic.gdx.graphics GL10 Color Texture)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn init-game []
  {:sr    (ShapeRenderer.)
   :pos   (Vector2. 10 10)
   :sb    (SpriteBatch.)
   :tex   (Texture. (.internal Gdx/files "../../../../sprite1.png"))
   :stars (stars/init 128)})

(defn update-game [state]
  (->> state (stars/update)))

(defn render-game [{:keys [sr pos sb tex stars]}]
  (letfn [(key-pressed? [key]
                        (.isKeyPressed Gdx/input key))

          (add-pos [dx dy]
                   (set! (.x pos) (+ (.x pos) dx))
                   (set! (.y pos) (+ (.y pos) dy)))

          (rect-movement []
                         (when (key-pressed? Input$Keys/LEFT)
                           (add-pos -10 0))
                         (when (key-pressed? Input$Keys/RIGHT)
                           (add-pos 10 0))
                         (when (key-pressed? Input$Keys/UP)
                           (add-pos 0 10))
                         (when (key-pressed? Input$Keys/DOWN)
                           (add-pos 0 -10)))

          (process-input []
                         (when (key-pressed? Input$Keys/ESCAPE)
                           (.exit Gdx/app))
                         (when (key-pressed? Input$Keys/W)
                           (println "Pressed W"))
                         (rect-movement))

          (clear-scr []
                     (.glClearColor Gdx/gl 0 0 0 1)
                     (.glClear Gdx/gl GL10/GL_COLOR_BUFFER_BIT))

          (draw-circles []
                        (.setColor sr Color/RED)
                        (doseq [x (range 0 globals/scr-w 100)]
                          (doseq [y (range 0 globals/scr-h 100)]
                            (.circle sr x y 100))))

          (draw-square []
                       (.setColor sr Color/YELLOW)
                       (.rect sr (.x pos) (.y pos) 100 100))

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
                    (.draw sb tex (.x pos) (.y pos))
                    (.end sb))]

    (process-input)
    (clear-scr)
    (draw-shapes)
    (draw-img)))
