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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.AfterClass;

public abstract class AbstractRemoteIntegrationTestCase extends AbstractIntegrationTestCase {
    protected static String serverUri;
    private static Server server;

    protected static void startServer(ResourceHandler resource_handler) throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);

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
    public static void stopServer() throws Exception {
        server.stop();
    }
}
