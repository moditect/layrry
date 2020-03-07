package com.example.app;

import com.example.bar.Bar;
import com.example.foo.Foo;

/**
 * Hello world!
 */
public class App {

    public static void main(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: com.example.app.App <name>");
        }

        new Foo().foo(args[0]);
        new Bar().bar(args[0]);
    }
}
