(ns proxy-default.core-test
  (:require [clojure.test :as t])
  (:import (org.apache.commons.pool2 PooledObjectFactory)
           (org.apache.commons.pool2.impl DefaultPooledObject GenericObjectPool GenericObjectPoolConfig)))

(t/deftest default-method-gets-invoked
  (let [destroyed* (atom false)
        pool       (GenericObjectPool.
                    (proxy [PooledObjectFactory] []
                      (makeObject []
                        (DefaultPooledObject. (constantly nil)))
                      (destroyObject [_]
                        (reset! destroyed* true))
                      (activateObject [_]))
                    (GenericObjectPoolConfig.))]
    (.returnObject pool (.borrowObject pool))
    (.close pool)

    ;; fails when run with 2.9.0 as the 2-arg arity of destroyObject gets called:
    ;; https://github.com/apache/commons-pool/blob/rel/commons-pool-2.9.0/src/main/java/org/apache/commons/pool2/impl/GenericObjectPool.java#L677
    ;; but that should delegate to the 1-arg arity by default:
    ;; https://github.com/apache/commons-pool/blob/rel/commons-pool-2.9.0/src/main/java/org/apache/commons/pool2/PooledObjectFactory.java#L125

    (t/is (true? @destroyed*))))
