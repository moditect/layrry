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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moditect.layrry.launcher.LayrryLauncher;

public class ContentNegotiationRemoteIntegrationTest extends AbstractRemoteIntegrationTestCase {
    @BeforeClass
    public static void startServer() throws Exception {
        startServer(new ContentNegotationResourceHandler());
    }

    private static class ContentNegotationResourceHandler extends ResourceHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String[] paths = request.getQueryString().split("=");
            String extension = paths[paths.length - 1];
            baseRequest.setPathInfo(baseRequest.getPathInfo() + "." + extension);
            response.setHeader("Content-Type", "text/vnd." + ("yml".equals(extension) ? "yaml" : extension));
            super.handle(target, baseRequest, request, response);
        }
    }

    @Test
    public void runLayersFromYaml() throws Exception {
        LayrryLauncher.launch("--layers-config",
                serverUri + "layers?type=yml",
                "Alice");

        assertOutput();
    }

    @Test
    public void runLayersFromToml() throws Exception {
        LayrryLauncher.launch("--layers-config",
                serverUri + "layers?type=toml",
                "Alice");

        assertOutput();
    }
}
