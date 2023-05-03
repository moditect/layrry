/*
 *  Copyright 2020 - 2023 The ModiTect authors
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
package org.moditect.layrry.it.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.moditect.layrry.Layers;
import org.moditect.layrry.Resolvers;
import org.moditect.layrry.launcher.LayrryLauncher;

public class LayrryIntegrationIT extends AbstractIntegrationTestCase {
    @Test
    public void runLayersFromApi() {
        Layers layers = Layers.builder()
                .layer("log")
                .withModule("org.apache.logging.log4j:log4j-api:2.20.0")
                .withModule("org.apache.logging.log4j:log4j-core:2.20.0")
                .withModule("org.moditect.layrry.it:it-logconfig:1.0.0")
                .layer("foo")
                .withParent("log")
                .withModule("org.moditect.layrry.it:it-greeter:1.0.0")
                .withModule("org.moditect.layrry.it:it-foo:1.0.0")
                .layer("bar")
                .withParent("log")
                .withModule("org.moditect.layrry.it:it-greeter:2.0.0")
                .withModule("org.moditect.layrry.it:it-bar:1.0.0")
                .layer("app")
                .withParent("foo")
                .withParent("bar")
                .withModule("org.moditect.layrry.it:it-app:1.0.0")
                .build();

        layers.run("org.moditect.layrry.it.app/org.moditect.layrry.it.app.App", "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromApiWithFlatRepository() {
        Layers layers = Layers.builder()
                .resolve(Resolvers.remote().enabled(false))
                .resolve(Resolvers.local()
                        .withLocalRepo("flat", Paths.get("target/repositories/flat").toAbsolutePath(), "flat"))
                .layer("log")
                .withModule("org.apache.logging.log4j:log4j-api:2.20.0")
                .withModule("org.apache.logging.log4j:log4j-core:2.20.0")
                .withModule("org.moditect.layrry.it:it-logconfig:1.0.0")
                .layer("foo")
                .withParent("log")
                .withModule("org.moditect.layrry.it:it-greeter:1.0.0")
                .withModule("org.moditect.layrry.it:it-foo:1.0.0")
                .layer("bar")
                .withParent("log")
                .withModule("org.moditect.layrry.it:it-greeter:2.0.0")
                .withModule("org.moditect.layrry.it:it-bar:1.0.0")
                .layer("app")
                .withParent("foo")
                .withParent("bar")
                .withModule("org.moditect.layrry.it:it-app:1.0.0")
                .build();

        layers.run("org.moditect.layrry.it.app/org.moditect.layrry.it.app.App", "Alice");

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

    @Test
    public void runLayersFromYamlWithFlatRepository() throws Exception {
        LayrryLauncher.launch("--layers-config",
                Path.of("src", "test", "resources", "layers-flat.yml").toAbsolutePath().toString(),
                "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromTomlWithFlatRepository() throws Exception {
        LayrryLauncher.launch("--layers-config",
                Path.of("src", "test", "resources", "layers-flat.toml").toAbsolutePath().toString(),
                "Alice");

        assertOutput();
    }
}
