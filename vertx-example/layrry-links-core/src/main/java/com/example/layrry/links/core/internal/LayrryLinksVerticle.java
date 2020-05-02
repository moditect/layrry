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
package com.example.layrry.links.core.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.moditect.layrry.platform.PluginDescriptor;
import org.moditect.layrry.platform.PluginLifecycleListener;

import com.example.layrry.links.core.spi.RouterContributor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class LayrryLinksVerticle extends AbstractVerticle {

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

        for(Entry<String, ModuleLayer> layer : moduleLayers.entrySet()) {
            registerContributedRoutes(layer.getValue());
        }

        vertx.createHttpServer()
            .requestHandler(mainRouter)
            .listen(8080);
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

    public static class RoutesOverviewRouterContributor implements RouterContributor {

        @Override
        public void install(Vertx vertx, RouterContributions contributions) {
            Router router = Router.router(vertx);

            router.get("/").handler(this::handleGetRoutesOverview);

            contributions.add("/routes", router);
        }

        private void handleGetRoutesOverview(RoutingContext routingContext) {
            HttpServerResponse response = routingContext.response();

            StringBuilder overview = new StringBuilder()
                            .append("<!DOCTYPE html>")
                            .append("<html>")
                            .append("  <head>")
                            .append("    <title>Layrry Links -- Routes</title>")
                            .append("  </head>")
                            .append("  <body>")
                            .append("    <h1>Layrry Links -- Routes</h1>");

            for (Entry<ModuleLayer, Set<String>> layerAndRoutes : routesByLayer.entrySet()) {
                for (String route : layerAndRoutes.getValue()) {
                    overview.append("    <p><a href=\"" + route + "\">" + route + "</a></p>");
                }
            }

                    overview.append("  </body>")
                            .append("</html>");

            response.putHeader("content-type", "text/html").end(overview.toString());
        }
    }
}
