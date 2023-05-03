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
import java.nio.file.Path;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moditect.layrry.launcher.LayrryLauncher;

import static org.junit.Assert.assertTrue;

public class PluginExampleRemoteTest {

    private static Server server;
    private static String serverUri;
    private ByteArrayOutputStream sysOut;
    private PrintStream originalSysOut;

    @BeforeClass
    public static void startJetty() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase(Path.of("src", "test", "resources").toAbsolutePath().toString());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{ resource_handler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();

        String host = connector.getHost();
        if (host == null) {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        serverUri = String.format("http://%s:%d/", host, port);
    }

    @AfterClass
    public static void stopJetty() throws Exception {
        server.stop();
    }

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
    public void runLayersWithYaml() throws Exception {
        String input = "1\nBob";
        InputStream testInput = new ByteArrayInputStream(input.getBytes("UTF-8"));
        System.setIn(testInput);

        LayrryLauncher.launch("--layers-config", serverUri + "layers-remote.yml", "--basedir", System.getProperty("basedir"));

        String output = sysOut.toString();

        assertTrue("Unexpected output: " + output, output.contains("Hi, Bob"));
    }

    @Test
    public void runLayersWithToml() throws Exception {
        String input = "1\nBob";
        InputStream testInput = new ByteArrayInputStream(input.getBytes("UTF-8"));
        System.setIn(testInput);

        LayrryLauncher.launch("--layers-config", serverUri + "layers-remote.toml", "--basedir", System.getProperty("basedir"));

        String output = sysOut.toString();

        assertTrue("Unexpected output: " + output, output.contains("Hi, Bob"));
    }
}
