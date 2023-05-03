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
package org.moditect.layrry.example.greeter.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moditect.layrry.launcher.LayrryLauncher;

import static org.junit.Assert.assertTrue;

public class PluginExampleTest {

    private String layersConfig;
    private ByteArrayOutputStream sysOut;
    private PrintStream originalSysOut;

    @Before
    public void setupSysOut() {
        layersConfig = System.getProperty("layersConfig");
        if (layersConfig == null) {
            throw new IllegalStateException("Specify layers.yml file via 'layersConfig' system property");
        }

        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));

        originalSysOut = System.out;
    }

    @After
    public void restoreSysOut() {
        System.setOut(originalSysOut);
    }

    @Test
    public void runLayers() throws Exception {
        String input = "1\nBob";
        InputStream testInput = new ByteArrayInputStream(input.getBytes("UTF-8"));
        System.setIn(testInput);

        LayrryLauncher.launch("--layers-config", layersConfig);

        String output = sysOut.toString();

        assertTrue("Unexpected output: " + output, output.contains("Hi, Bob"));
    }
}
