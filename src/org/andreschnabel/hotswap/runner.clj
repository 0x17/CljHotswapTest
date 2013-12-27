(ns org.andreschnabel.hotswap.runner
  (:require [org.andreschnabel.game.main :as gmain]
            [org.andreschnabel.hotswap.globals :as globals]
            [org.andreschnabel.hotswap.utils :as utils])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input Input$Keys)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration)
           (java.io File)
           (com.badlogic.gdx.utils Disposable)))

(def my-listener
  (let [last-reload (atom 0)
        gstate (atom nil)]
    (letfn [(attempt-render [modified?]
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
          (let [files (utils/file-lst globals/code-path "clj")
                max-lastmod (->> files (map #(.lastModified %)) (apply max))
                did-modify? (> max-lastmod @last-reload)]

            (when did-modify?
              (try
                (doseq [f files] (load-file (.getAbsolutePath f)))
                (reset! last-reload max-lastmod)
                (catch Exception e
                  (println "Reload exception:" (.getMessage e)))))

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
