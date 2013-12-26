(ns org.andreschnabel.hotswap.runner
  (:require [org.andreschnabel.game.main :as gmain]
            [org.andreschnabel.hotswap.globals :as globals]
            [org.andreschnabel.hotswap.utils :as utils])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input Input$Keys)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration)
           (java.io File)
           (com.badlogic.gdx.utils Disposable)))

(defn- modified-code? [code-file last-reload]
  (> (.lastModified code-file) last-reload))

(def my-listener
  (let [last-reload (atom 0)
        code-file (File. globals/code-filename)
        gstate (atom nil)]
    (letfn [(attempt-reload []
                            (try
                              (load-file globals/code-filename)
                              (catch Exception e
                                (println "Reload exception:" (.getMessage e))))
                            (reset! last-reload (.lastModified code-file)))
            (attempt-render [modified?]
                            (try
                              (reset! gstate (gmain/update-game @gstate))
                              (gmain/render-game @gstate)
                              (catch Exception e
                                (when modified?
                                  (println "Render exception:" (.getMessage e))))))]
      (proxy [ApplicationListener] []
        (create []
          (reset! gstate (gmain/init-game)))
        (resize [w h])
        (render []
          (let [did-modify? (modified-code? code-file @last-reload)]
            (when did-modify? (attempt-reload))
            (attempt-render did-modify?))
          (when (.isKeyPressed Gdx/input Input$Keys/ENTER)
            (reset! gstate (gmain/init-game))))
        (pause [])
        (resume [])
        (dispose []
          (doseq [obj (vals @gstate)]
            (when (instance? Disposable obj)
              (.dispose obj))))))))

(def cfg (LwjglApplicationConfiguration.))
(utils/assign-from-map cfg {.useGL20      false
                            .title        globals/caption
                            .width        globals/scr-w
                            .height       globals/scr-h
                            .vSyncEnabled true})

(LwjglApplication. my-listener cfg)
