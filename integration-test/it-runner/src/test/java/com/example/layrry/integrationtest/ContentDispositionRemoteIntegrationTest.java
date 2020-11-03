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

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

// Tests remote layers with "Content-Disposition" header
public class ContentDispositionRemoteIntegrationTest {

    private static Server server;
    private static String serverUri;
    private ByteArrayOutputStream sysOut;
    private PrintStream originalSysOut;

    private static class MyResourceHandler extends ResourceHandler{
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String[] paths = request.getPathInfo().split("/");
            response.setHeader("Content-Disposition","attachment; filename="+paths[paths.length-1]);
            super.handle(target, baseRequest, request, response);
        }
    }

    @BeforeClass
    public static void startJetty() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);

        ResourceHandler resource_handler = new MyResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase(Path.of("src", "test", "resources").toAbsolutePath().toString());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
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

    private void assertOutput() {
        String output = sysOut.toString();

        assertTrue(output.contains("com.example.foo.Foo - Hello, Alice from Foo (Greeter 1.0.0)"));
        assertTrue(output.contains("com.example.bar.Bar - Hello, Alice from Bar (Greeter 2.0.0)"));
        assertTrue(output.contains("com.example.bar.Bar - Good bye, Alice from Bar (Greeter 2.0.0)"));
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
}
