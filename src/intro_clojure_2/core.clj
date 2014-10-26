(ns intro-clojure-2.core
  (:use bakery.core))

(defn error [& args]
  (apply println args)
  :error)

(def pantry-ingredients #{:flour :sugar})
(def fridge-ingredients #{:butter :egg :milk})

(def scooped-ingredients #{:milk :flour :sugar})
(def squeezed-ingredients #{:egg})
(def simple-ingredients #{:butter})

(defn from-pantry? [ingredient]
  (contains? pantry-ingredients ingredient))

(defn from-fridge? [ingredient]
  (contains? fridge-ingredients ingredient))

(defn fetch-from-pantry
  ([ingredient]
    (fetch-from-pantry ingredient 1))
  ([ingredient quantity]
    (if (from-pantry? ingredient)
      (do
        (go-to :pantry)
        (dotimes [count quantity]
          (load-up ingredient))
        (go-to :prep-area)
        (dotimes [count quantity]
          (unload ingredient)))
      (error "This function only works with ingredients that are stored in the pantry. You asked me to fetch" ingredient))))

(defn fetch-from-fridge
  ([ingredient]
    (fetch-from-fridge ingredient 1))
  ([ingredient quantity]
    (if (from-fridge? ingredient)
      (do
        (go-to :fridge)
        (dotimes [count quantity]
          (load-up ingredient))
        (go-to :prep-area)
        (dotimes [count quantity]
          (unload ingredient)))
      (error "This function only works with ingredients that are stored in the fridge. You asked me to fetch" ingredient))))

(defn fetch-ingredient
  ([ingredient]
    (fetch-ingredient ingredient 1))
  ([ingredient quantity]
    (cond
      (from-pantry? ingredient)
      (fetch-from-pantry ingredient quantity)
      (from-fridge? ingredient)
      (fetch-from-fridge ingredient quantity)
      :else
      (error "I don't know where to get" ingredient))))

(defn load-up-amount [ingredient quantity]
  (dotimes [count quantity]
    (load-up ingredient)))

(defn unload-amount [ingredient quantity]
  (dotimes [count quantity]
    (unload ingredient)))

(defn fetch-list [shopping-list]
  (doseq [[location ingredients] {:pantry pantry-ingredients :fridge fridge-ingredients}]
    (go-to location)
    (doseq [ingredient ingredients]
      (load-up-amount ingredient (ingredient shopping-list 0))))

  (go-to :prep-area)
  (doseq [[ingredient amount] shopping-list]
    (unload-amount ingredient amount)))

(defn add-egg []
  (grab :egg)
  (squeeze)
  (add-to-bowl))

(defn add-sugar []
  (grab :cup)
  (scoop :sugar)
  (add-to-bowl)
  (release))

(defn add-flour []
  (grab :cup)
  (scoop :flour)
  (add-to-bowl)
  (release))

(defn add-milk []
  (grab :cup)
  (scoop :milk)
  (add-to-bowl)
  (release))

(defn add-butter []
  (grab :butter)
  (add-to-bowl))

(defn scooped? [ingredient]
  (contains? scooped-ingredients ingredient))

(defn squeezed? [ingredient]
  (contains? squeezed-ingredients ingredient))

(defn simple? [ingredient]
  (contains? simple-ingredients ingredient))

(defn add-eggs [n]
  (dotimes [e n]
    (add-egg)))

(defn add-flour-cups [n]
  (dotimes [e n]
    (add-flour)))

(defn add-milk-cups [n]
  (dotimes [e n]
    (add-milk)))

(defn add-sugar-cups [n]
  (dotimes [e n]
    (add-sugar)))

(defn add-butters [n]
  (dotimes [e n]
    (add-butter)))

(defn add-squeezed
  ([ingredient]
    (add-squeezed ingredient 1))
  ([ingredient amount]
    (if (squeezed? ingredient)
      (dotimes [i amount]
        (grab ingredient)
        (squeeze)
        (add-to-bowl))
      (error "This function only works on squeezed ingredients. You asked me to squeeze" ingredient))))

(defn add-scooped
  ([ingredient]
    (add-scooped ingredient 1))
  ([ingredient amount]
    (if (scooped? ingredient)
      (do
        (grab :cup)
        (dotimes [i amount]
          (scoop ingredient)
          (add-to-bowl))
        (release))
      (println "This function only works on scooped ingredients. You asked me to scoop" ingredient))))

(defn add-simple
  ([ingredient]
    (add-simple ingredient 1))
  ([ingredient amount]
    (if (simple? ingredient)
      (dotimes [i amount]
        (grab ingredient)
        (add-to-bowl))
      (println "This function only works on simple ingredients. You asked me to add" ingredient))))

(defn add
  ([ingredient]
    (add ingredient 1))
  ([ingredient amount]
    (cond
      (squeezed? ingredient)
      (add-squeezed ingredient amount)

      (simple? ingredient)
      (add-simple ingredient amount)

      (scooped? ingredient)
      (add-scooped ingredient amount)

      :else
      (println "I do not have the ingredient" ingredient))))

(defn bake-cake []
  (add :egg 2)
  (add :flour 2)
  (add :milk 1)
  (add :sugar 1)

  (mix)

  (pour-into-pan)
  (bake-pan 25)
  (cool-pan))

(defn bake-cookies []
  (add :egg 1)
  (add :flour 1)
  (add :butter 1)
  (add :sugar 1)

  (mix)

  (pour-into-pan)
  (bake-pan 30)
  (cool-pan))

(defn add-ingredients [ingredient-map-1 ingredient-map-2]
  (merge-with + ingredient-map-1 ingredient-map-2))

(defn day-at-the-bakery []
  (doseq [order (get-morning-orders)]
    (dotimes [count (:cake (:items order) 0)]
      (fetch-list {:egg 2 :flour 2 :milk 1 :sugar 1})
      (delivery {:orderid (:orderid order)
                 :address (:address order)
                 :rackids [(bake-cake)]}))
    (dotimes [count (:cookies (:items order) 0)]
      (fetch-list {:egg 1 :flour 1 :butter 1 :sugar 1})
      (delivery {:orderid (:orderid order)
                 :address (:address order)
                 :rackids [(bake-cookies)]}))))

(defn -main []
  (day-at-the-bakery))