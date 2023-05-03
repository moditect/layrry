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
module org.moditect.layrry.example.links.core {
    requires org.moditect.layrry.platform;
    requires org.apache.logging.log4j;
    requires transitive io.vertx.core;
    requires transitive io.vertx.web;
    requires transitive io.vertx.eventbusbridge.common;

    exports org.moditect.layrry.example.links.core;
    exports org.moditect.layrry.example.links.core.spi;

    uses org.moditect.layrry.example.links.core.spi.RouterContributor;
    provides org.moditect.layrry.platform.PluginLifecycleListener
        with org.moditect.layrry.example.links.core.internal.LayrryLinksVerticle.RoutesPluginLifecycleListener;
    provides org.moditect.layrry.example.links.core.spi.RouterContributor
        with org.moditect.layrry.example.links.core.internal.LayrryLinksVerticle.RoutesOverviewRouterContributor;
}
