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
  (match 'x
    'x :this)
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
    (match [1 'x 1 1]
      [1 'x v x] (str "x = " x)))
  => "x = 1")

(fact "Match on a map"
  (let [a :a b :b]
    (match {:a :b}
      {a b} :this))
  => :this)

(fact "Match on a set"
  (let [a :a
        b :b]
    (match #{:a :b}
      #{a b} :this))
  => :this)

(fact "Match on nested non-scalar values"
  (let [v 1
        a :a
        b :b]
    (match [[1 'x 1 1] {:a :b}]
      [[1 'x v x] {a b}] [(str "x = " x) :this]))
  => ["x = 1" :this])

(fact "About numbers in maps"
  (fact "Numeric values are allowed"
    (match {:a 1}
      {:a 1} :this)
    => :this)
  (fact "Numeric literal map keys are not allowed"
    (macroexpand-1 '(match :whatever
                      {1 :b} :this))
    => (throws java.lang.ClassCastException))
  (fact "Numeric map keys are allowed if they are not literals"
    (let [v 1]
      (match {1 :b}
        {v :b} :this))
    => :this))
