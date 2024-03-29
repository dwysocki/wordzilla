(ns wordzilla.ops
  "Operations for interacting with the B+ Tree."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [b-plus-tree.core :as btree-ops]
            [b-plus-tree.io   :as btree-io]
            [url-scraper.core :as url]
            [wordzilla.util :refer [verbose-stacktrace]]))

(defn init!
  "Initialize B+ Tree."
  ([filename & args]
     (try
       (apply btree-io/new-tree filename (map #(Integer/parseInt %) args))
       (catch Exception e
         (println "Failed to initialize B+ Tree.")
         (verbose-stacktrace e)))))

(defn clear!
  "Clear B+ Tree file."
  ([filename]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)
               page-size (:page-size header)
               empty-header (assoc header
                              :count 0
                              :free  page-size
                              :root  -1)]
           (.setLength raf 0)
           (btree-io/write-header empty-header raf)))
       (catch Exception e
         (println "Failed to clear B+ Tree.")
         (verbose-stacktrace e)))))

(defn order
  "Print the order of the B+ Tree."
  ([filename]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)]
           (println (:order header))))
       (catch Exception e
         (println "Failed to count elements in B+ Tree.")
         (verbose-stacktrace e)))))

(defn elements
  "Print the number of elements in the B+ Tree."
  ([filename]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)]
           (println (:count header))))
       (catch Exception e
         (println "Failed to count elements in B+ Tree.")
         (verbose-stacktrace e)))))

(defn depth
  "Print the number of levels in the B+ Tree."
  ([filename]
     (println "Operation not supported.")))

(defn find-val
  "Find the value associated with key in the B+ Tree."
  ([filename key]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)]
           (println (or (first (btree-ops/find-val key raf header))
                        (str "Key not found.")))))
       (catch Exception e
         (println "Failed to search B+ Tree.")
         (verbose-stacktrace e)))))

(defn insert!
  "Insert key and val into the B+ Tree."
  ([filename key val & args]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)
               _ (println "before:" (:count header))
               [header cache] (btree-ops/insert key val raf header)]
           (println "after:" (:count header))
           (btree-io/write-cache cache raf)
           (btree-io/write-header header raf)))
       (catch Exception e
         (println "Failed to insert into B+ Tree.")
         (verbose-stacktrace e)))))

(defn insert-url!
  "Insert all words from the given url into the B+ Tree."
  ([filename url]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [{:keys [key-size val-size]
                :as header}
               (btree-io/read-header raf)
               
               words (url/url->word-set url)
               words (filter #(<= (count %) key-size) words)
               word-map (zipmap words (repeat url))
               
               [header cache]
               (btree-ops/insert-all word-map raf header)]
           (btree-io/write-cache cache raf)
           (btree-io/write-header header raf)))

       (catch Exception e
         (println "Failed to insert into B+ Tree.")
         (verbose-stacktrace e)))))

(defn insert-all!
  "Insert all words from the given url-file into the B+ Tree.
  url-file must list url's separated by newlines."
  ([filename url-file & args]
     (try
       (let [urls (->> url-file
                       slurp
                       string/split-lines
                       (filter (comp not string/blank?)))]
         (doseq [url urls]
           (insert-url! filename url)))

       (catch Exception e
         (println "Failed to read file.")
         (verbose-stacktrace e)))))

(defn remove!
  "Remove the entry associated with key from the B+ Tree."
  ([filename key & args]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)
               [updated-header cache]
               (btree-ops/delete key raf header)]
           (if (= (:count header) (:count updated-header))
             (println "Key not found.")
             (do
               (btree-io/write-cache cache raf)
               (btree-io/write-header updated-header raf)))))

       (catch Exception e
         (println "Failed to remove from B+ Tree.")
         (verbose-stacktrace e)))))

(defn starts-with-remove!
  "Display all keys which start with the given string in the B+ Tree,
  and then removes them."
  ([filename s & args]
     (try
       (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
         (let [header (btree-io/read-header raf)
               [updated-header cache]
               (btree-ops/print-starts-with-remove s raf header)]
           (when-not (= (:count header) (:count updated-header))
             (btree-io/write-cache cache raf)
             (btree-io/write-header updated-header raf))))

       (catch Exception e
         (println "Failed to remove from B+ Tree.")
         (verbose-stacktrace e)))))
