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
package com.example.layrry.links.core.spi;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Service interface to be implemented by plug-ins of the Layrry Links
 * application for contributing routes to the web application.
 */
public interface RouterContributor {

    /**
     * Invoked when adding a plug-in with a contributor implementation.
     *
     * @param vertx         The Vertx instance
     * @param contributions Callback for registering one or more routes
     */
    void install(Vertx vertx, RouterContributions contributions);

    interface RouterContributions {
        void add(String path, Router router);
    }
}
