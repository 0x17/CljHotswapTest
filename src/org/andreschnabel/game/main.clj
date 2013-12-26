(ns org.andreschnabel.game.main
  (:require [org.andreschnabel.hotswap.globals :as globals]
            [org.andreschnabel.hotswap.utils :as utils]
            [org.andreschnabel.game.stars :as stars])
  (:import (com.badlogic.gdx Gdx Input Input$Keys Files)
           (com.badlogic.gdx.graphics.glutils ShapeRenderer ShapeRenderer$ShapeType ImmediateModeRenderer
                                              ImmediateModeRenderer10)
           (com.badlogic.gdx.graphics GL10 Color Texture OrthographicCamera PerspectiveCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn init-game []
  {:sr    (ShapeRenderer.)
   :pos   [10 10]
   :sb    (SpriteBatch.)
   :tex   (Texture. (.internal Gdx/files "../../../../sprite1.png"))
   :stars (stars/init 128)
   :persp-cam (PerspectiveCamera. 60 globals/scr-w globals/scr-h)
   :ortho-cam (OrthographicCamera.)
   :imr (ImmediateModeRenderer10.)
   :angle 0.0})

(defn move [dx dy {:keys [pos] :as state}]
  (utils/coords pos (assoc state :pos [(+ x dx) (+ y dy)])))

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

(defn update-angle [{:keys [angle] :as state}]
  (assoc state :angle (+ angle 5)))

(defn update-game [state]
  (->> state (stars/update) (process-input) (update-angle)))

(defn render-game [{:keys [sr pos sb tex stars ortho-cam persp-cam imr angle]}]
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
                       (utils/coords pos (.rect sr x y 64 64)))

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
                    (utils/coords (map float pos) (.draw sb tex x y))
                    (.end sb))

          (draw-triangle-vertices [[x y z] [w h]]
                     (.begin imr GL10/GL_TRIANGLES)

                     (.color imr 1 1 0 1)
                     (.vertex imr x y z)

                     (.color imr 0 1 0 1)
                     (.vertex imr (+ x w) y z)

                     (.color imr 0 0 1 1)
                     (.vertex imr (+ x w) (+ y h) z)

                     (.end imr))

          (draw-triangle []
                         (.glPushMatrix Gdx/gl10)
                         (.glTranslatef Gdx/gl10 0 0 -10)
                         (.glRotatef Gdx/gl10 angle 0 1 0)
                         (draw-triangle-vertices [0 0 0] [7 4])
                         (.glPopMatrix Gdx/gl10))]

    (clear-scr)

    (.apply persp-cam Gdx/gl10)
    (draw-triangle)

    (.apply ortho-cam Gdx/gl10)
    (draw-shapes)
    (draw-img)))
