package com.example.foo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.greeter.Greeter;

/**
 * Hello world!
 *
 */
public class Foo {

    // private static final Logger LOGGER = LoggerFactory.getLogger(Foo.class);
    private static final Logger LOGGER = LogManager.getLogger(Foo.class);

    public void foo(String name) {
        LOGGER.info(new Greeter().greet(name, "Foo"));
    }
}
