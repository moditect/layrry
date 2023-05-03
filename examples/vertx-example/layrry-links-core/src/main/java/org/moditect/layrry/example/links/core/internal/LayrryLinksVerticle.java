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
package org.moditect.layrry.example.links.core.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.moditect.layrry.example.links.core.spi.RouterContributor;
import org.moditect.layrry.platform.PluginDescriptor;
import org.moditect.layrry.platform.PluginLifecycleListener;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class LayrryLinksVerticle extends AbstractVerticle {

    private static final String EVENT_BUS_ADDRESS = "routes-updates";

    private static final Logger LOGGER = LogManager.getLogger(LayrryLinksVerticle.class);

    private static Map<String, ModuleLayer> moduleLayers = new ConcurrentHashMap<>();
    private static Map<ModuleLayer, Set<String>> routesByLayer = new ConcurrentHashMap<>();

    private static volatile Router mainRouter;
    private static volatile Vertx vertx;

    @Override
    public void start() {
        vertx = super.vertx;

        mainRouter = Router.router(vertx);
        mainRouter.route().handler(BodyHandler.create());

        registerContributedRoutes(LayrryLinksVerticle.class.getModule().getLayer());

        for (Entry<String, ModuleLayer> layer : moduleLayers.entrySet()) {
            registerContributedRoutes(layer.getValue());
        }

        int port = Integer.getInteger("port", 8080);
        vertx.createHttpServer()
                .requestHandler(mainRouter)
                .listen(port);

        LOGGER.info("Server ready! Browse to http://localhost:{}/routes", port);
    }

    private static void registerContributedRoutes(ModuleLayer layer) {
        ServiceLoader<RouterContributor> contributors = ServiceLoader.load(layer, RouterContributor.class);
        Set<String> routes = new HashSet<>();

        contributors.forEach(contributor -> {
            if (contributor.getClass().getModule().getLayer() == layer) {
                contributor.install(vertx, (path, router) -> {
                    LOGGER.info("Adding router for path: " + path);

                    mainRouter.mountSubRouter(path, router);
                    routes.add(path);
                });
            }
        });

        routesByLayer.put(layer, routes);

        vertx.eventBus().publish(EVENT_BUS_ADDRESS, getRoutesList());
    }

    private static void unregisterContributedRoutes(ModuleLayer layer) {
        mainRouter.getRoutes()
                .stream()
                .filter(route -> route.getPath() != null && startsWithAny(route.getPath(), routesByLayer.get(layer)))
                .forEach(route -> {
                    route.remove();
                    LOGGER.info("Removed router for path: " + route.getPath());
                });

        routesByLayer.remove(layer);

        vertx.eventBus().publish(EVENT_BUS_ADDRESS, getRoutesList());
    }

    private static boolean startsWithAny(String path, Set<String> paths) {
        return paths != null && paths.stream().anyMatch(path::startsWith);
    }

    public static class RoutesPluginLifecycleListener implements PluginLifecycleListener {

        @Override
        public void pluginAdded(PluginDescriptor plugin) {
            LOGGER.info("Adding plug-in: " + plugin);

            moduleLayers.put(plugin.getName(), plugin.getModuleLayer());
            if (mainRouter != null) {
                registerContributedRoutes(plugin.getModuleLayer());
            }
        }

        @Override
        public void pluginRemoved(PluginDescriptor plugin) {
            unregisterContributedRoutes(plugin.getModuleLayer());
            moduleLayers.remove(plugin.getName());
            LOGGER.info("Removed plug-in: " + plugin);
        }
    }

    private static String getRoutesList() {
        return routesByLayer.values()
                .stream()
                .flatMap(routes -> routes.stream())
                .map(route -> "<p><a href=\"" + route + "\">" + route + "<a/></p>")
                .sorted()
                .collect(Collectors.joining());
    }

    public static class RoutesOverviewRouterContributor implements RouterContributor {

        @Override
        public void install(Vertx vertx, RouterContributions contributions) {
            SockJSBridgeOptions options = new SockJSBridgeOptions().addOutboundPermitted(new PermittedOptions().setAddress(EVENT_BUS_ADDRESS));
            SockJSHandler sockJsHandler = SockJSHandler.create(vertx);

            sockJsHandler.bridge(options, event -> {
                if (event.type() == BridgeEventType.SOCKET_CREATED) {
                    LOGGER.info("Socket created");
                }

                event.complete(true);
            });

            Router router = Router.router(vertx);

            router.get("/").handler(this::handleGetRoutesOverview);
            router.route("/eventbus/*").handler(sockJsHandler);

            contributions.add("/routes", router);
        }

        private void handleGetRoutesOverview(RoutingContext routingContext) {
            String index = "<!DOCTYPE html>\n" +
                    "                <html lang=\"en\">\n" +
                    "                  <head>\n" +
                    "                    <title>Layrry Links</title>\n" +
                    "                    <meta charset=\"utf-8\">\n" +
                    "                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                    "\n" +
                    "                    <script src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script>\n" +
                    "                    <script src=\"https://cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js\"></script>\n" +
                    "                    <script src=\"https://cdn.jsdelivr.net/npm/vertx3-eventbus-client@3.9.0/vertx-eventbus.min.js\"></script>\n" +
                    "\n" +
                    "                    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\" integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">\n"
                    +
                    "                  </head>\n" +
                    "                  <body>\n" +
                    "\n" +
                    "                    <div class=\"container\">\n" +
                    "                      <h1>Layrry Links -- Routes</h1>\n" +
                    "                      <div id=\"status\">%s</div>\n" +
                    "                    </div>\n" +
                    "\n" +
                    "                    <script>\n" +
                    "                      var eb = new EventBus(\"http://localhost:8080/routes/eventbus\");\n" +
                    "\n" +
                    "                      eb.onopen = function () {\n" +
                    "                        eb.registerHandler(\"routes-updates\", function (err, msg) {\n" +
                    "                          $('#status').html(msg.body);\n" +
                    "                        });\n" +
                    "                      }\n" +
                    "                    </script>\n" +
                    "                  </body>\n" +
                    "                </html>";

            index = String.format(index, getRoutesList());

            routingContext.response()
                    .putHeader("content-type", "text/html")
                    .end(index);
        }
    }
}
