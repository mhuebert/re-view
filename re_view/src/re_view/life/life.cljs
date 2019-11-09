(ns chia.life
  (:refer-clojure :exclude [key]))
  
  ;; a computation with `life` will evaluate one or more times.
  ;; it is born, it accumulates and forgets memories,
  ;; it forges relationships, it dies.

  ;; each life starts with a formula, in code, which replays each time
  ;; it wakes up. the code can be affected by its relationships
  ;; and its memory.
  
  ;; life is open to new relations by anything that knows how
  ;; to make an impression on life (by implementing its protocols).
  
  ;; a life can be woken up by any of the relations it has formed,
  ;; or by its memories.

(defonce ^:private known-domains #js{})

(defn ^:private get-domain [domain-key] 
  (j/unchecked-get known-domains domain-key))  

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API for implementing domains (things that life can relate to)  


(defprotocol IDomain
  (key [domain] "string key for domain")
  (process-memory [domain life long-term short-term]
    "commits short-term memories to long-term memory,
     which are returned. may have side-effects 
     (eg. setting up or removing listeners)")
  (handle-death [domain life long-term-mem]
    "disposes of listeners & references to a life."))
    
(defn register-domain! 
  "Each domain must be registered once."
  [domain]
  (j/unchecked-set known-domains (key domain) domain))  
  
(defn assoc-short-term! 
  "Replace short-term memory for `domain` with `x`"
  [life domain x]
  (j/assoc-in! (memory life) [.-short-term (key domain)] x)
  life)
  
(defn update-short-term! 
  "Update short-term memory for `domain` with `f`"
  [life domain f]
  (j/update-in! (memory life) [.-short-term (key domain)] f)
  life)
  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API for implementing living things

(defprotocol ILife  
  (play! [life] 
    "(re)plays life's code")
  (memory [life]
    "javascript object for life's memories"))

(def ^:dynamic *current*
  "Must be bound for each evaluation of a life."
  nil)

(defn active-domains [life]
  (let [mem (memory life)
        short-term (.-short-term mem) 
        domain-keys (js-keys short-term)]
    (doseq [domain-key (.-long-term mem)
            :when (not (j/contains? short-term domain-key))]
      (.push domain-keys domain-key))
    domain-keys))
    
(defn birth [life]
  (j/unchecked-set (memory life) (j/obj .-short-term #js{} .-long-term #js{})))    
                
(defn before-eval 
  "Must be called before each evaluation of a life.
   Initializes state."
  [life]
  (j/unchecked-set (memory life) .-short-term #js{}))

(defn after-eval
  "Called after each evaluation of a life.
   Handles accumulation of memories."
  [life]
  (let [state (memory life)
        long-term (.-long-term state)
        short-term (.-short-term state)]
    (doseq [domain-key (active-domains life)
            :let [domain (get-domain domain-key)
                  long-term (j/unchecked-get long-term domain-key)
                  short-term (j/unchecked-get short-term domain-key)]]
      (j/push! processed domain-key)            
      (if-some [new-long-term (process-memories domain life long-term short-term)]
        (j/unchecked-set! long-term domain-key new-long-term)
        (js-delete long-term domain-key))))
    
(defn die! [life]
  (let [mem (memory life)
        long-term (.-long-term mem)
        short-term (.-short-term mem)]
    (doseq [domain-key (active-domains life)
            :let [domain (get-domain domain-key)]]
      (handle-death domain life 
                    (j/unchecked-get long-term domain-key)
                    (j/unchecked-get short-term domain-key)))))

(extend-type react/Component   
  ILife
  (wake-up! [this] (view/schedule-render this)))
  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;  
;; how to participate in life
;; 
;; 1. bind *current* to instance of life while it evaluates
;; 2. call `before-eval` before each evaluation
;; 2. call `after-eval` after each evaluation 


;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; relationships/influences
;;
;; To start a relationship with the *current*, one can:
;; 1. Call `assoc-short-term!` or `update-short-term!` to affect
;;    the current-life's short-term memory. Memories are 
;;    partitioned by domain.
;; 2. Register a memory-handler for your chosen domain. This will
;;    be called each time a life that has interacted with your
;;    domain is evaluated (until it returns nil).