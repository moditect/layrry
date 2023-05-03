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
module org.moditect.layrry.example.links.tournament {
    requires org.moditect.layrry.platform;
    requires org.moditect.layrry.example.links.core;
    requires io.vertx.core;
    requires io.vertx.web;
    provides org.moditect.layrry.example.links.core.spi.RouterContributor
        with org.moditect.layrry.example.links.tournament.internal.TournamentsRouterContributor;
}
