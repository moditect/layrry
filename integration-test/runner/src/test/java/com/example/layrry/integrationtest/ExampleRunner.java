package com.example.layrry.integrationtest;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moditect.layrry.Layers;

public class ExampleRunner {

    private ByteArrayOutputStream sysOut;
    private PrintStream originalSysOut;

    @Before
    public void setupSysOut() {
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));

        originalSysOut = System.out;
    }

    @After
    public void restoreSysOut() {
        System.setOut(originalSysOut);
    }

    @Test
    public void runLayers() {
        Layers layers = Layers.layer("log")
                .withModule("org.apache.logging.log4j:log4j-api:jar:2.13.1")
                .withModule("org.apache.logging.log4j:log4j-core:jar:2.13.1")
                .withModule("com.example:logconfig:1.0.0")
            .layer("foo")
                .withParent("log")
                .withModule("com.example:greeter:1.0.0")
                .withModule("com.example:foo:1.0.0")
            .layer("bar")
                .withParent("log")
                .withModule("com.example:greeter:2.0.0")
                .withModule("com.example:bar:1.0.0")
            .layer("app")
                .withParent("foo")
                .withParent("bar")
                .withModule("com.example:app:1.0.0")
            .build();

        layers.run("com.example.app/com.example.app.App", "Alice");

        String output = sysOut.toString();

        assertTrue(output.contains("com.example.foo.Foo - Hello, Alice from Foo (Greeter 1.0.0)"));
        assertTrue(output.contains("com.example.bar.Bar - Hello, Alice from Bar (Greeter 2.0.0)"));
        assertTrue(output.contains("com.example.bar.Bar - Good bye, Alice from Bar (Greeter 2.0.0)"));
    }
}
