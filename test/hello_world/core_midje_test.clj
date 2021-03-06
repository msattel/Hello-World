(ns hello-world.core-midje-test
  (:require [hello-world.core :refer :all]
            [org.httpkit.client :as http]
            [clojure.core.async :as async])
  (:use [midje.sweet]
         [ring.adapter.jetty]))


(with-state-changes [(before :facts (println "Test"))
                     (after :facts (println "Facts tested"))] 
  (fact true => true
        false => false))

(with-state-changes [(before :facts (defonce jetty-test (run-jetty #'handler {:port 3000 :join? false })))
                     (after :facts (println "stopped the test"))]
  (fact (let [{:keys [status headers body error] :as resp} @(http/get "http://localhost:3000")]
          (str body)) => "<h1>Hello World!</h1>"
   ))

(with-state-changes [(before :facts (put-hello-to-channel))]
  (fact "The hello-world-channel should return 'Hello World'" 
   (async/<!! (async/go (async/<! hello-world-channel))) => "Hello World"))
