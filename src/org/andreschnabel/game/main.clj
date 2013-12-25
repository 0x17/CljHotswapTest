(ns org.andreschnabel.game.main
  (:import (com.badlogic.gdx Gdx Input Input$Keys Files)
           (com.badlogic.gdx.graphics.glutils ShapeRenderer ShapeRenderer$ShapeType)
           (com.badlogic.gdx.graphics GL10 Color Texture)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.utils Disposable)))

(defn init-game []
  {:sr  (ShapeRenderer.)
   :pos (Vector2. 10 10)
   :sb  (SpriteBatch.)
   :tex (Texture. (.internal Gdx/files "../../../../sprite1.png"))})

(defn render-game [{:keys [pos sr sb tex]}]
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
                        (doseq [x (range 0 640 100)]
                          (doseq [y (range 0 480 100)]
                            (.circle sr x y 100))))

          (draw-square []
                       (.setColor sr Color/YELLOW)
                       (.rect sr (.x pos) (.y pos) 100 100))

          (draw-shapes []
                       (.begin sr ShapeRenderer$ShapeType/Line)
                       (draw-circles)
                       (draw-square)
                       (.end sr))
          (draw-img []
                    (.begin sb)
                    (.draw sb tex (.x pos) (.y pos))
                    (.end sb))]
    (process-input)
    (clear-scr)
    (draw-shapes)
    (draw-img)))

