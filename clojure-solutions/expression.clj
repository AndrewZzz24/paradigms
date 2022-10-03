(defn constant [value] (fn [m] value))
(defn variable [variableName] (fn [m] (m (str variableName))))

(defn create-function-operation [operation]
  (fn [& functions] (fn [m] (apply operation (mapv (fn [func] (func m)) functions)))))

(def add (create-function-operation +))
(def subtract (create-function-operation -))
(def multiply (create-function-operation *))
(def divide (create-function-operation (fn [f1 f2] (/ (double f1) (double f2)))))
(def negate subtract)
(def pow (create-function-operation (fn [f1 f2] (Math/pow f1 f2))))
(def log (create-function-operation (fn [f1 f2] (/ (Math/log (Math/abs f2)) (Math/log (Math/abs f1))))))
(def operationsFunctions {'+ add '- subtract '* multiply '/ divide, 'negate negate, 'pow pow, 'log log})

(defn create-function-expression [expr]
  (cond
    (or (list? expr) (vector? expr))
    (apply (operationsFunctions (first expr)) (mapv create-function-expression (rest expr)))
    (number? expr)
    (constant expr)
    :else
    (variable expr)))

(defn parseFunction [expression]
  (let [list (read-string expression)]                      ; :NOTE: let ни к месту
    (create-function-expression list)))


(definterface IOperation
  (^String toString [])
  (^String toSuffixString [])
  (^Number evaluate [vars])
  (^Object diff [expression])
  )

(declare ZERO)
(declare ONE)

(deftype ConstantConstructor [constValue]
  IOperation
  (toString [this] (str (.-constValue this)))
  (toSuffixString [this] (str (.-constValue this)))
  (evaluate [this vars] constValue)
  (diff [this variable] ZERO))

(def ZERO (ConstantConstructor. 0))
(def ONE (ConstantConstructor. 1))

; :NOTE: common constants should be extracted

(deftype VariableConstructor [variableName]
  IOperation
  (toString [this] (.-variableName this))
  (toSuffixString [this] (.-variableName this))
  (evaluate [this vars] (vars (clojure.string/lower-case (subs (.-variableName this) 0 1))))
  (diff [this variable]
    (cond
      (= (clojure.string/lower-case (subs (.-variableName this) 0 1)) variable) ONE
      :else ZERO)))

(def Variable (fn [variableName] (VariableConstructor. variableName)))
(def Constant (fn [constValue] (ConstantConstructor. constValue)))

(deftype OperationProto [functions operation strOperationSymbol diffRule]
  IOperation
  (evaluate [this vars] (apply operation (mapv (fn [f] (.evaluate f vars)) (.-functions this))))
  (toString [this] (str "(" strOperationSymbol (apply str (mapv #(str " " %) (.-functions this))) ")"))
  (toSuffixString [this] (str "(" (apply str (mapv #(str (.toSuffixString %) " ") (.-functions this))) strOperationSymbol ")"))
  (diff [this variable] ((apply diffRule (.-functions this)) variable))
  )

(defn create-operation [operation strOperationSymbol diffRule]
  (fn [& args] (OperationProto. args operation strOperationSymbol diffRule)))

(def Add (create-operation + "+" (fn [f g] (fn [variable] (Add (.diff f variable) (.diff g variable))))))
(def Subtract (create-operation - "-" (fn [f g] (fn [variable] (Subtract (.diff f variable) (.diff g variable))))))
(def Multiply (create-operation * "*" (fn [f g] (fn [variable] (Add (Multiply (.diff f variable) g) (Multiply f (.diff g variable)))))))
(def Divide (create-operation (fn [f1 f2] (/ (double f1) (double f2))) "/" (fn [f g] (fn [variable] (Divide (Subtract (Multiply (.diff f variable) g) (Multiply f (.diff g variable))) (Multiply g g))))))
(def Negate (create-operation - "negate" (fn [f] (fn [variable] (Negate (.diff f variable))))))
(def Log (create-operation (fn [f1 f2] (/ (Math/log (Math/abs f2)) (Math/log (Math/abs f1)))) "log"
                           (fn [g f] (fn [variable] (Divide
                                                      (Subtract
                                                        (Divide
                                                          (Multiply
                                                            (.diff f variable) ; :NOTE:/2 common subexpressions should also not be repeated
                                                            (Log (Constant Math/E) g))
                                                          f)
                                                        (Divide
                                                          (Multiply
                                                            (Log (Constant Math/E) f)
                                                            (.diff g variable))
                                                          g))
                                                      (Multiply (Log (Constant Math/E) g) (Log (Constant Math/E) g)))))))
(def Pow (create-operation (fn [f1 f2] (Math/pow f1 f2)) "pow"
                           (fn [f g] (fn [variable] (Multiply
                                                      (Pow
                                                        f
                                                        (Subtract g (Constant 1)))
                                                      (Add
                                                        (Multiply (.diff f variable) g)
                                                        (Multiply f (.diff g variable) (Log (Constant Math/E) f))
                                                        ))))))

(defn evaluate [expression vars] (.evaluate expression vars))
(defn toString [expression] (.toString expression))

(def operations {'+ Add '- Subtract '* Multiply '/ Divide, 'negate Negate, 'pow Pow, 'log Log})

(defn create-expression [expr]
  (cond
    (or (list? expr) (vector? expr)) (apply (operations (first expr)) (mapv create-expression (rest expr)))
    (number? expr) (Constant expr)
    :else (Variable (str expr))))

(defn parseObject [unparsedExpression] (create-expression (read-string unparsedExpression)))
(defn diff [expression variable] (.diff expression variable))

(load-file "parser.clj")

(def operationMap {"+" Add "-" Subtract "*" Multiply "/" Divide, "negate" Negate})

(def parseObjectSuffix
  (let
    [*digit (_char #(Character/isDigit %))
     *space (_char #(Character/isWhitespace %))
     *ws (+ignore (+star *space))
     *number (+map #(Constant (read-string %)) (+str (+seq (+opt (+char "-")) (+str (+plus *digit)) (+opt (+str (+seq (+char ".") (+plus *digit)))))))
     *operationTypes (+char (apply str "+-*/" (filter #(Character/isLetter %) (mapv char (range 0 128)))))
     *operationVariable (+map
                          #((fn [arg]
                              (if (nil? (operationMap arg))
                                (Variable (str arg))
                                (operationMap arg)))
                            (str %))
                          (+str (+plus *operationTypes)))
     ]
    (letfn [(*argument []
              (+seqn 1 (+char "(") (+plus (+seqn 0 *ws (delay (*value)) *ws)) (+char ")")))
            (*expression []
              (+map #(apply (last %) (butlast %)) (*argument)))
            (*value []
              (+or *number (*expression) *operationVariable))]
      (+parser (+seqn 0 *ws (*value) *ws)))))

(defn toStringSuffix [expr] (.toSuffixString expr))

(def res (parseObjectSuffix "(ZYyyz 4.0 +)"))
(println res)
(println (toString res))
(println (toStringSuffix res))
(println (evaluate res {"z" 0.0, "x" 0.0, "y" 0.0}))