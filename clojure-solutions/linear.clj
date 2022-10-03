;; :FIXED: а можно ли обойтись без явной итерации через цикл? например, через apply
(defn make-operation [operation]
  (fn [v1 v2] (vec (apply map operation [v1 v2]))))

(defn make-multiply-operation [operation]
  (fn [m value] (mapv #(operation % value) m )))

(def v+ (make-operation +))
(def v- (make-operation -))
(def v* (make-operation *))
(def vd (make-operation /))

(def scalar
  (fn [v1 v2]
    (apply + ((make-operation (fn [a b] (* a b))) v1 v2))))

(defn vect [v1 v2] [(- (* (nth v1 1) (nth v2 2)) (* (nth v1 2) (nth v2 1)))
                    (- (* (nth v1 2) (nth v2 0)) (* (nth v1 0) (nth v2 2)))
                    (- (* (nth v1 0) (nth v2 1)) (* (nth v1 1) (nth v2 0)))])

(def v*s (make-multiply-operation *) )
(def m*s (make-multiply-operation v*s))
(def m*v (make-multiply-operation scalar))
(def m+ (make-operation v+))
(def m- (make-operation v-))
(def m* (make-operation v*))
(def md (make-operation vd))

(defn transpose [m]
  (vec (apply map vector m)))

(defn m*m [m1 m2]
  (let [columns (vec (apply mapv vector m2))]
  (mapv (fn [v1] (mapv #(scalar v1 %) columns)) m1)))

(def c+ (make-operation m+))
(def c- (make-operation m-))
(def c* (make-operation m*))
(def cd (make-operation md))