(defproject clojure_ttt "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC2"][org.clojure/tools.cli "0.3.3"]]
  :profiles {:dev {:dependencies [[speclj "3.3.1"]]}}
  :main clojure-ttt.core
  :plugins [[speclj "3.3.1"]]
  :test-paths ["spec"])

