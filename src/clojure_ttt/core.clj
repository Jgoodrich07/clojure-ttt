(ns clojure-ttt.core
  (:require [clojure-ttt.ui :refer :all]
            [clojure-ttt.board :refer :all]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure-ttt.ai :refer :all]
            [clojure-ttt.config :refer :all]))

(defn exit [status message]
  (println message)
  (System/exit status))

(defn human-make-move [board players]
  (try (Integer. (choose-space players))
    (catch Exception e (do
                         (print (str (.getMessage e) " That's not a number. Let's try that again!\n"))
                         (human-make-move board players)))))

(defn make-move [board players]
  (if (= (:type (first players)) "computer")
    (ai-make-move board (map #(:marker %) players))
    (let [space (human-make-move board players)
          available-moves (find-unmarked-spaces board)
          on-board? (some #(= (Integer. space) %) available-moves)]
      (cond
        (not on-board?) (do
                          (invalid-move)
                          (make-move board players))
        :else (Integer. space)))))

(defn game-loop [board players]
      (print-board board)
      (cond
        (win-game? board) (winner players)
        (tie-game? board) (tie)
        :else (let [marker (:marker(first players))
                    spot (make-move board players)
                    board (mark-spot marker spot board)]
                (game-loop board (reverse players)))))

(defn -main [& args]
  (let [{:keys [options arguments summary errors]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      (= (:player1 options) (:player2 options)) (exit 1 "Markers cannot be the same.")
       errors (exit 1 (error-msg errors)))
    (if (some #(= (first arguments) %) ["me-first" "comp-first" "head-to-head"])
      (let [config (game-config (first arguments) options)
            players [(first config) (second config)]
            board  (nth config 2)] (game-loop board players))
      (exit 1 (usage summary)))))


