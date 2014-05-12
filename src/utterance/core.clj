(ns utterance.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [utterance.ops :as ops]
            [utterance.util :refer [exit verbose *verbose*]])
  (:gen-class))

(def cli-options
  [["-f" "--filename FILE" "File name"
    :id :filename]
   ["-v" "--verbose"
    :default false
    :id :verbose?]
   ["-h" "--help"]])

(def commands
  {"init"               ops/init!,
   "clear"              ops/clear!,
   "order"              ops/order,
   "elements"           ops/elements,
   "depth"              ops/depth,
   "find"               ops/find-val,
   "insert"             ops/insert!,
   "insert-url"         ops/insert-url!,
   "insert-all"         ops/insert-all!,
   "remove"             ops/remove!,
   "starts-with"        ops/starts-with,
   "starts-with-remove" ops/starts-with-remove!})

(defn usage
  "Program usage string."
  ([summary]
     (->> ["utterance: a word database using a B+ Tree"
           ""
           "Usage: utterance [--verbose] [--help] [--filename FILE]"
           "                 <command> [<args>]"
           ""
           "Options:"
           summary
           ""
           "Commands:"
           "TODO"]
          (string/join \newline))))


(defn error-msg
  "Error message string."
  ([errors]
     (str "The following errors occurred while parsing your command:\n\n"
          (string/join \newline errors))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [{:keys [options arguments errors summary]}
        (parse-opts args cli-options)
        
        {:keys [help filename verbose?]} options
        
        [command & cmd-args] arguments]
    (binding [*verbose* verbose?]
      (cond
       help (exit 0 (usage summary))
       errors (exit 1 (error-msg errors))
       :default (apply (or (commands command) (exit 1 (usage summary)))
                       filename
                       cmd-args)))))
