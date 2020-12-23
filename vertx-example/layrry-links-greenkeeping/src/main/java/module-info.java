import com.example.layrry.links.core.spi.RouterContributor;
import com.example.layrry.links.greenkeeping.internal.GreenkeepingRouterContributor;

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

module com.example.layrry.links.greenkeeping {
    requires org.moditect.layrry.platform;
    requires com.example.layrry.links.core;
    requires io.vertx.core;
    requires io.vertx.web;
    provides RouterContributor with GreenkeepingRouterContributor;
}
