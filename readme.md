# proxy-default

This repo is to demonstrate an issue I recently had with `proxy` and an interface that recently got a method override with an additional argument, calling the single-arg method by default.

Preparation:

```shell
javac src/java/oldversion/*.java -d classes/oldversion && javac src/java/newversion/*.java -d classes/newversion
```

Passes:

```shell
clojure -Srepro -M:oldversion:test
```

Fails:

```shell
clojure -Srepro -M:newversion:test
```
