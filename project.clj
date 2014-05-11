(defproject utterance "0.1.1"
  :description "CSC365 Assignment 2: Word lookup using a B+ Tree."
  :url "https://github.com/Rosnec/utterance"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [b-plus-tree "0.1.9"]
                 [url-scraper "0.1.0"]]
  :main utterance.core)
