/*
 *  Copyright 2020 - 2022 The ModiTect authors
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

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moditect.layrry.launcher.LayrryLauncher;

public class LayrryRemoteIntegrationTest extends AbstractRemoteIntegrationTestCase {
    @BeforeClass
    public static void startServer() throws Exception {
        startServer(new ResourceHandler());
    }

    @Test
    public void runLayersFromYaml() throws Exception {
        LayrryLauncher.launch("--layers-config",
                serverUri + "layers.yml",
                "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromToml() throws Exception {
        LayrryLauncher.launch("--layers-config",
                serverUri + "layers.toml",
                "Alice");

        assertOutput();
    }

    @Test
    public void runVersionedLayersFromYaml() throws Exception {
        LayrryLauncher.launch("--layers-config",
                serverUri + "layers-versioned.yml",
                "--properties",
                serverUri + "versions.properties",
                "Alice");

        assertOutput();
    }
}
