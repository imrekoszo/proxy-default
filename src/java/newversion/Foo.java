package pd.java.foo;

public interface Foo {
    void bar(Object o);
    default void bar(Object o, Object p) {
        bar(o);
    }

    default void qux(Object o) {
        bar(o);
    }
}
