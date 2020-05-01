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

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TournamentService {

    private Map<String, JsonObject> tournaments = new HashMap<>();

    public TournamentService() {
        setUpInitialData();
    }

    public JsonObject getTournament(String tournamentId) {
        return tournaments.get(tournamentId);
    }

    public void addTournament(String tournamentId, JsonObject tournament) {
        tournaments.put(tournamentId, tournament);
    }

    public JsonArray getTournaments() {
        JsonArray array = new JsonArray();
        tournaments.forEach((k, v) -> array.add(v));
        return array;
    }

    private void setUpInitialData() {
        addTournament(new JsonObject().put("id", "123").put("name", "Easter 36").put("holes", 36).put("price", 1000.0));
        addTournament(new JsonObject().put("id", "456").put("name", "Summer Special").put("holes", 18).put("price", 1500.0));
    }

    private void addTournament(JsonObject tournament) {
        tournaments.put(tournament.getString("id"), tournament);
    }
}
