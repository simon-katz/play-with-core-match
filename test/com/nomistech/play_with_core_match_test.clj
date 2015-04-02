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
  (let [v 1]
    (match [1 'sym 1 1]
      [1 'sym v x] (str "x = " x)))
  => "x = 1")

(fact "Match on a map"
  (let [a :a
        b 2]
    (match {:a 2 2 :a}
      {a b b :a} :this))
  => :this)

(fact "Match on a set"
  (let [a :a
        b 2]
    (match #{:a 2}
      #{a b} :this))
  => :this)

(fact "Match on nested non-scalar values"
  (let [v 1
        a :a
        b 2]
    (match [[1 'sym 1 1] {:a 2 2 :a}]
      [[1 'sym v x] {a b b a}] (str "x = " x)))
  => "x = 1")

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
