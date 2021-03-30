package pd.java.foo;

public class FooCaller {
    public static void callDirectly (Foo foo) {
        foo.bar(new Object());
    }

    public static void callDefaultOverload (Foo foo) {
        foo.bar(new Object(), new Object());
    }

    public static void callOtherDefault (Foo foo) {
        foo.qux(new Object());
    }
}
