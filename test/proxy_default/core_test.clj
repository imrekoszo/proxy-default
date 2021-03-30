(ns proxy-default.core-test
  (:require [clojure.test :as t])
  (:import (org.apache.commons.pool2 PooledObjectFactory)
           (org.apache.commons.pool2.impl DefaultPooledObject GenericObjectPool GenericObjectPoolConfig)
           (pd.java.foo Foo FooCaller)))

(defn ->proxied-factory [destroyed*]
  (proxy [PooledObjectFactory] []
    (makeObject []
      (DefaultPooledObject. (constantly nil)))
    (destroyObject [_]
      (reset! destroyed* true))
    (activateObject [_])))

(defn ->reified-factory [destroyed*]
  (reify PooledObjectFactory
    (makeObject [_]
      (DefaultPooledObject. (constantly nil)))
    (destroyObject [_ _]
      (reset! destroyed* true))
    (validateObject [_ _])
    (activateObject [_ _])
    (passivateObject [_ _])))

(defn object-gets-destroyed? [->factory]
  (let [destroyed* (atom false)
        pool       (GenericObjectPool. (->factory destroyed*) (GenericObjectPoolConfig.))]
    (.returnObject pool (.borrowObject pool))
    (.close pool)
    @destroyed*))

(t/deftest default-method-gets-invoked-proxied
  ;; fails when run with 2.9.0 as the 2-arg arity of destroyObject gets called:
  ;; https://github.com/apache/commons-pool/blob/rel/commons-pool-2.9.0/src/main/java/org/apache/commons/pool2/impl/GenericObjectPool.java#L677
  ;; but that should delegate to the 1-arg arity by default:
  ;; https://github.com/apache/commons-pool/blob/rel/commons-pool-2.9.0/src/main/java/org/apache/commons/pool2/PooledObjectFactory.java#L125
  (t/is (true? (object-gets-destroyed? ->proxied-factory))))

(t/deftest default-method-gets-invoked-reified
  ;; Passes with both 2.8.1 and 2.9.0
  (t/is (true? (object-gets-destroyed? ->reified-factory))))

(defn ->proxied-foo [bar-called*]
  (proxy [Foo] []
    (bar [_]
      (reset! bar-called* true))))

(defn ->reified-foo [bar-called*]
  (reify Foo
    (bar [_ _]
      (reset! bar-called* true))))

(defn bar-called? [->foo call-fn]
  (let [bar-called (atom false)]
    (try
      (call-fn (->foo bar-called))
      @bar-called
      (catch Exception e e))))

(defn call-directly [^Foo foo] (FooCaller/callDirectly foo))

(defn call-default-overload [^Foo foo] (FooCaller/callDefaultOverload foo))

(defn call-other-default [^Foo foo] (FooCaller/callOtherDefault foo))

(t/deftest bar-gets-called-directly-proxied
  ;; passes with both old and new versions
  (t/is (true? (bar-called? ->proxied-foo call-directly))))

(t/deftest bar-gets-called-directly-reified
  ;; passes with both old and new versions
  (t/is (true? (bar-called? ->reified-foo call-directly))))

(t/deftest bar-gets-called-via-default-overload-proxied
  ;; fails with new version
  (t/is (true? (bar-called? ->proxied-foo call-default-overload))))

(t/deftest bar-gets-called-via-default-overload-reified
  ;; passes with both old and new versions
  (t/is (true? (bar-called? ->reified-foo call-default-overload))))

(t/deftest bar-gets-called-via-other-default-proxied
  ;; fails with new version
  (t/is (true? (bar-called? ->proxied-foo call-other-default))))

(t/deftest bar-gets-called-via-other-default-reified
  ;; passes with both old and new versions
  (t/is (true? (bar-called? ->reified-foo call-other-default))))
