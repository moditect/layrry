package com.example.bar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.greeter.Greeter;

public class Bar {

    private static final Logger LOGGER = LogManager.getLogger(Bar.class);

    public void bar(String name) {
        LOGGER.info(new Greeter().hello(name, "Bar"));
        LOGGER.info(new Greeter().goodBye(name, "Bar"));
    }
}
