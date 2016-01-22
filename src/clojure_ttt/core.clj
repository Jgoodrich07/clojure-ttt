(ns clojure-ttt.core
  (:require [clojure-ttt.ui :refer :all]
            [clojure-ttt.board :refer :all]
            [clojure.tools.cli :refer :all]))

(defn create-players [number]
  (cond
    (= "1" number) (let [players [{:name (prompt "What is your name?") :marker (prompt "Select a marker (it can be any letter)")}
                                  {:name "TicTacJoe" :marker (prompt "Select a marker for the computer")}]
                       order (prompt "Who will go first? (me/comp)")]
                    (cond
                      (= order "me") players
                      (= order "comp") (reverse players)))

    (= "2" number) (let [players [{:name (prompt "Player 1, what is your name?")
                                   :marker (prompt "Select a marker (it can be any letter)")}
                                  {:name (prompt "Player 2, what is your name?")
                                   :marker (prompt "Select a marker (it can be any letter not chosen by player 1)")}]] players)))
(def cli-options
  [["-p1" "--player1 TYPE" "Player1 type"]
   ["-p2" "--player2 TYPE" "Player2 type"]
;what happens if markers are the same?
    ["-m1" "--marker1 MARKER" "Player1 marker"
    :default "X"]
   ["-m2" "--marker2 MARKER" "Player2 marker"
    :default "O"]
   ["-b"  "--board SIZE" "board size"
    :default 3
    :parse-fn #(Integer/parseInt %)]
   ["-h"  "--help"]])


(defn zip-spaces-and-boards [spaces boards]
 (map #(zipmap [:space :board] %) (map vector spaces boards)))

(defn find-empty-spaces [board]
  (filter number? board))

(defn create-possible-boards [board spaces markers]
  (map #(mark-spot (first markers) % board) spaces))

(defn max-by-score [scored-boards]
 (apply max-key :score scored-boards))

(defn min-by-score [scored-boards]
  (apply min-key :score scored-boards))

(defn ai-config [board markers]
  (let [empty-spaces (find-empty-spaces board)
        possible-boards (create-possible-boards board empty-spaces markers)]
    (zip-spaces-and-boards empty-spaces possible-boards)))

(defn score-board [unscored-board-map markers]
  (let [my-marker (first markers)]

    (loop [board-vector (:board unscored-board-map)
           marker (first markers)]
      (cond
        (and (win-game? board-vector) (= marker my-marker)) (conj unscored-board-map {:score 10})
        (and (win-game? board-vector) (not (= marker my-marker))) (conj unscored-board-map {:score -10})
        (tie-game? board-vector) (conj unscored-board-map {:score 0})
        :else (let [next-node-unscored-boards-map (ai-config board-vector markers)
                    scored-boards-map (map #(score-board % markers) next-node-unscored-boards-map)]
                (if (= marker my-marker)
                  (recur (max-by-score scored-boards-map)(reverse markers))
                  (recur (min-by-score scored-boards-map)(reverse markers))))))))

(defn score-boards [unscored-boards markers]
   (map #(score-board % markers) unscored-boards))

(defn ai-make-move [board markers]
  (let [unscored-boards (ai-config board markers)
        scored-boards-coll (score-boards unscored-boards markers)]
  (:space (max-by-score scored-boards-coll))))

(defn make-move [board players]
  (if (= (:name (first players)) "TicTacJoe")
    (ai-make-move board (map #(:marker %) players))
    (Integer. (prompt "Select a space using the numbers of the spaces above"))))

(defn end-game [board players]
  (cond
    (tie-game? board) (print "Game over! It's a tie!")
    :else (print (str "Game over! " (:name (second players)) " wins!"))))

(defn game-loop [board players]
    (loop [board board
           players players]
      (print-board board)
      (if (game-over? board)
        (end-game board players)
        (recur (mark-spot (:marker (first players))
                                    (make-move board players)
                                    board)
               (reverse players)))))

(defn -main []
  (let [board (clojure-ttt.board/create-board 3)
       players (create-players (prompt "Welcome to TicTacToe! How many humans will be playing?"))]
 (game-loop board players)))


