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
package com.example.layrry.links.greenkeeping.internal;

import com.example.layrry.links.core.spi.RouterContributor;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class GreenkeepingRouterContributor implements RouterContributor {

    private GreenkeepingService greenkeepingService = new GreenkeepingService();

    @Override
    public void install(Vertx vertx, RouterContributions contributions) {
        Router router = Router.router(vertx);

        router.get("/:activityID").handler(this::handleGetActivity);
        router.put("/:activityID").handler(this::handleAddActivity);
        router.get("/").handler(this::handleListActivities);

        contributions.add("/greenkeeping-activities", router);
    }

    private void handleGetActivity(RoutingContext routingContext) {
        String activityID = routingContext.request().getParam("activityID");
        HttpServerResponse response = routingContext.response();

        if (activityID == null) {
            sendError(400, response);
        }
        else {
            JsonObject product = greenkeepingService.getActivity(activityID);
            if (product == null) {
                sendError(404, response);
            }
            else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    private void handleAddActivity(RoutingContext routingContext) {
        String activityID = routingContext.request().getParam("activityID");
        HttpServerResponse response = routingContext.response();

        if (activityID == null) {
            sendError(400, response);
        }
        else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            }
            else {
                greenkeepingService.addActivity(activityID, product);
                response.end();
            }
        }
    }

    private void handleListActivities(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(greenkeepingService.getActivities().encodePrettily());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
