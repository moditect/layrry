/**
 *  Copyright 2020 The ModiTect authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.example.layrry.integrationtest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moditect.layrry.Layers;
import org.moditect.layrry.launcher.LayrryLauncher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class LayrryIntegrationIT {

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

    private void assertOutput() {
        String output = sysOut.toString();

        assertTrue(output.contains("com.example.foo.Foo - Hello, Alice from Foo (Greeter 1.0.0)"));
        assertTrue(output.contains("com.example.bar.Bar - Hello, Alice from Bar (Greeter 2.0.0)"));
        assertTrue(output.contains("com.example.bar.Bar - Good bye, Alice from Bar (Greeter 2.0.0)"));
    }

    @Test
    public void runLayersFromApi() {
        Layers layers = Layers.builder()
            .layer("log")
                .withModule("org.apache.logging.log4j:log4j-api:2.13.1")
                .withModule("org.apache.logging.log4j:log4j-core:2.13.1")
                .withModule("com.example.it:it-logconfig:1.0.0")
            .layer("foo")
                .withParent("log")
                .withModule("com.example.it:it-greeter:1.0.0")
                .withModule("com.example.it:it-foo:1.0.0")
            .layer("bar")
                .withParent("log")
                .withModule("com.example.it:it-greeter:2.0.0")
                .withModule("com.example.it:it-bar:1.0.0")
            .layer("app")
                .withParent("foo")
                .withParent("bar")
                .withModule("com.example.it:it-app:1.0.0")
            .build();

        layers.run("com.example.app/com.example.app.App", "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromApiWithFlatRepository() {
        Layers layers = Layers.builder()
            .layer("log")
                .withModule("org.apache.logging.log4j:log4j-api:2.13.1")
                .withModule("org.apache.logging.log4j:log4j-core:2.13.1")
                .withModule("com.example.it:it-logconfig:1.0.0")
            .layer("foo")
                .withParent("log")
                .withModule("com.example.it:it-greeter:1.0.0")
                .withModule("com.example.it:it-foo:1.0.0")
            .layer("bar")
                .withParent("log")
                .withModule("com.example.it:it-greeter:2.0.0")
                .withModule("com.example.it:it-bar:1.0.0")
            .layer("app")
                .withParent("foo")
                .withParent("bar")
                .withModule("com.example.it:it-app:1.0.0")
            .build();

        layers.maven().remote()
            .enabled(false);
        layers.maven().local()
            .withLocalRepo("flat", Paths.get("target/repositories/flat").toAbsolutePath(), "flat");

        layers.run("com.example.app/com.example.app.App", "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromYaml() throws Exception {
        LayrryLauncher.launch("--layers-config",
            Path.of("src", "test", "resources", "layers.yml").toAbsolutePath().toString(),
            "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromToml() throws Exception {
        LayrryLauncher.launch("--layers-config",
            Path.of("src", "test", "resources", "layers.toml").toAbsolutePath().toString(),
            "Alice");

        assertOutput();
    }
}
