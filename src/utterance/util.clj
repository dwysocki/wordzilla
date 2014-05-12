(ns utterance.util
  "Utility functions.")

; Verbose flag
(def ^:dynamic *verbose* false)

(defn print-err
  "Same as print but outputs to stdout."
  ([& more] (binding [*print-readably* nil, *out* *err*] (apply pr more))))

(defn println-err
  "Same as println but outputs to stdout."
  ([& more] (binding [*print-readably* nil, *out* *err*] (apply prn more))))

(defmacro dbg
  "Executes the expression x, then prints the expression and its output to
  stderr, while also returning value.
       example=> (dbg (+ 2 2))
       dbg: (+ 2 2) = 4
       4"
  ([x] `(dbg ~x "dbg:" "="))
  ([x msg] `(dbg ~x ~msg "="))
  ([x msg sep] `(let [x# ~x] (println-err ~msg '~x ~sep x#) x#)))

(defn verbose
  "When *verbose* is true, outputs body to stderr."
  ([& more] (when *verbose* (apply println-err more))))

(defn verbose-stacktrace
  "When *verbose* is true, prints the stacktrace held by the exception."
  ([exception] (when *verbose* (.printStackTrace exception))))

(defn exit
  "Exit the program with the status and message if given, otherwise status 0."
  ([]                         (System/exit 0))
  ([status]                   (System/exit status))
  ([status msg] (println msg) (System/exit status)))
