# proxy-default

This repo is to demonstrate an issue I recently had with `proxy` and an interface that recently got a method override with an additional argument, calling the single-arg method by default.

Passes:

```shell
clojure -M:pool-2-8-1:test
```

Fails:

```shell
clojure -M:pool-2-9-0:test
```
