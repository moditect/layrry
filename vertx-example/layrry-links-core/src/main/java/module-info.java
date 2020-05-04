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

import org.moditect.layrry.platform.PluginLifecycleListener;

import com.example.layrry.links.core.internal.LayrryLinksVerticle.RoutesOverviewRouterContributor;
import com.example.layrry.links.core.internal.LayrryLinksVerticle.RoutesPluginLifecycleListener;
import com.example.layrry.links.core.spi.RouterContributor;

module com.example.layrry.links.core {
    requires org.moditect.layrry.platform;
    requires org.apache.logging.log4j;
    requires transitive vertx.core;
    requires transitive vertx.web;
    requires transitive vertx.bridge.common;

    exports com.example.layrry.links.core;
    exports com.example.layrry.links.core.spi;

    uses RouterContributor;
    provides PluginLifecycleListener with RoutesPluginLifecycleListener;
    provides RouterContributor with RoutesOverviewRouterContributor;
}
