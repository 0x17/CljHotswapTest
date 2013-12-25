(ns org.andreschnabel.hotswap.runner
  (:require [org.andreschnabel.game.main :as gmain])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input Input$Keys)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication)
           (java.io File)
           (com.badlogic.gdx.utils Disposable)))

(def code-filename "../game/main.clj")

(defn modified-code? [code-file last-reload]
  (> (.lastModified code-file) last-reload))

(def my-listener
  (let [last-reload (atom 0)
        code-file (File. code-filename)
        gstate (atom nil)]
    (proxy [ApplicationListener] []
      (create []
        (reset! gstate (gmain/init-game)))
      (resize [w h])
      (render []
        (let [did-modify? (modified-code? code-file @last-reload)]
          (when did-modify?
            (try
              (load-file code-filename)
              (catch Exception e
                (println "Reload exception:" (.getMessage e))))
            (reset! last-reload (.lastModified code-file)))
          (try
            (gmain/render-game @gstate)
            (catch Exception e
              (when did-modify?
                (println "Render exception:" (.getMessage e))))))
        (when (.isKeyPressed Gdx/input Input$Keys/ENTER)
          (reset! gstate (gmain/init-game))))
      (pause [])
      (resume [])
      (dispose []
        (doseq [obj (vals @gstate)]
          (when (instance? Disposable obj)
            (.dispose obj)))))))

(LwjglApplication. my-listener "Test" 640 480 false)
