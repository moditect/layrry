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
package com.example.layrry.links.tournament.internal;

import com.example.layrry.links.core.spi.RouterContributor;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class TournamentsRouterContributor implements RouterContributor {

    private TournamentService tournamentService = new TournamentService();

    @Override
    public void install(Vertx vertx, RouterContributions contributions) {
        Router router = Router.router(vertx);

        router.get("/:tournamentID").handler(this::handleGetProduct);
        router.put("/:tournamentID").handler(this::handleAddProduct);
        router.get("/").handler(this::handleListProducts);

        contributions.add("/tournaments", router);
    }

    private void handleGetProduct(RoutingContext routingContext) {
        String tournamentID = routingContext.request().getParam("tournamentID");
        HttpServerResponse response = routingContext.response();

        if (tournamentID == null) {
            sendError(400, response);
        }
        else {
            JsonObject product = tournamentService.getTournament(tournamentID);
            if (product == null) {
                sendError(404, response);
            }
            else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    private void handleAddProduct(RoutingContext routingContext) {
        String tournamentID = routingContext.request().getParam("tournamentID");
        HttpServerResponse response = routingContext.response();

        if (tournamentID == null) {
            sendError(400, response);
        }
        else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            }
            else {
                tournamentService.addTournament(tournamentID, product);
                response.end();
            }
        }
    }

    private void handleListProducts(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(tournamentService.getTournaments().encodePrettily());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
