(ns com.nomistech.play-with-core-match-test
  (:require [midje.sweet :refer :all]
            [clojure.core.match :refer [match]]
            [com.nomistech.play-with-core-match :refer :all]))

;;;; ___________________________________________________________________________
;;;; Basics

(fact "Match on a literal value"
  (match 1
    1 :this)
  => :this)

(fact "Match using multiple clauses"
  (match 1
    2 :not-this
    1 :this)
  => :this)

(fact "Match on a quoted symbol"
  (match 'sym
    'sym :this)
  => :this)

(fact "Match on the value of a local binding"
  (let [v 1]
    (match 1
      v :this))
  => :this)

(fact "Match on a wildcard, introducing a binding"
  (match 1
    x (str "x = " x))
  => "x = 1")

;;;; ___________________________________________________________________________
;;;; Match on non-scalar values

(fact "Match on a vector"
  (fact "Trivial"
    (let [v1 1
          v2 2])
    (match [1 2]
      [v1 v2] :this)
    => :this)
  (fact "Using lots of gubbins"
    (let [v3 3]
      (match [1 'sym 3 4]
        [1 'sym v3 x4] (str "x4 = " x4)))
    => "x4 = 4"))

(fact "Match on a map"
  (fact "Trivial"
    (let [v1 1]
      (match {:a 1}
        {:a v1} :this))
    => :this)
  (fact "Using lots of gubbins"
    (let [v2 2
          v3 3]
      (match {:a 1
              :b 2
              3 :c
              :d 4}
        {:a 1
         :b v2
         v3 :c
         :d x4}
        (str "x4 = " x4)))
    => "x4 = 4"))

(fact "Match on a set"
  (fact "Trivial"
    (let [v1 1]
      (match #{1}
        #{v1} :this))
    => :this)
  (fact "Using lots of gubbins"
    (let [v3 3]
      (match #{1 'sym 3}
        #{1 'sym v3} :this))
    => :this))

(fact "Match on nested non-scalar values"
  (fact "Trivial"
    (let [v-v1 1
          v-v2 2
          m-v1 1]
      (match [[1 2] {:a 1}]
        [[v-v1 v-v2] {:a m-v1}] :this))
    => :this)
  (fact "Using lots of gubbins"
    (let [v-v3 3
          m-v2 2
          m-v3 3]
      (match [[1 'sym 3 4]
              {:a 1
               :b 2
               3 :c
               :d 4}]
        [[1 'sym v-v3 v-x4]
         {:a 1
          :b m-v2
          m-v3 :c
          :d m-x4}]
        [(str "v-x4 = " v-x4)
         (str "m-x4 = " m-x4)]))
    => ["v-x4 = 4"
        "m-x4 = 4"]))

(fact "About non- clojure.lang.Named in maps"
  ;; Note that numbers and quoted symbols are not instances
  ;; of `clojure.lang.Named`.
  (fact "Such values are allowed as map values"
    (match {:num 1 :q-sym 'sym}
      {:num 1 :q-sym 'sym} :this)
    => :this)
  (fact "Numeric literals are not allowed as map keys in match patterns"
    (macroexpand-1 '(match :whatever
                      {1 :a} :this))
    => (throws java.lang.ClassCastException))
  (fact "Quoted symbols are not allowed as map keys in match patterns"
    (macroexpand-1 '(match :whatever
                      {'sym :a} :this))
    => (throws java.lang.ClassCastException))
  (fact "If they are not literals, numbers are allowed as map keys in match patterns"
    (let [v 1]
      (match {1 :a}
        {v :a} :this))
    => :this)
  (fact "If they are not literals, quoted symbols are allowed as map keys in match patterns"
    (let [v 'sym]
      (match {'sym :b}
        {v :b} :this))
    => :this))

(fact "Cannot create bindings in the key position of a map entry"
  ;; I can't work out how to make a test for this.
  #_(fact
      (clojure.walk/macroexpand-all '(match {:a 1}
                                       {x 1} :this))
      => (throws))
  #_(fact
      (match {:a 1}
        {x 1} :this)
      => (throws)))

(fact "Cannot create bindings in a set"
  ;; I can't work out how to make a test for this.
  #_(fact
      (clojure.walk/macroexpand-all '(match #{:a}
                                       #{x} :this))
      => (throws))
  #_(fact
      (match #{:a}
        #{x} :this)
      => (throws)))
