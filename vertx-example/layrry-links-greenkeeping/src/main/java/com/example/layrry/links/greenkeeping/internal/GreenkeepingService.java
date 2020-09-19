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

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class GreenkeepingService {

    private Map<String, JsonObject> activities = new HashMap<>();

    public GreenkeepingService() {
        setUpInitialData();
    }

    public JsonObject getActivity(String activityId) {
        return activities.get(activityId);
    }

    public void addActivity(String activityId, JsonObject activity) {
        activities.put(activityId, activity);
    }

    public JsonArray getActivities() {
        JsonArray array = new JsonArray();
        activities.forEach((k, v) -> array.add(v));
        return array;
    }

    private void setUpInitialData() {
        addActivity(new JsonObject().put("id", "123").put("name", "Sand Front 9").put("date", "2020-09-17"));
        addActivity(new JsonObject().put("id", "456").put("name", "Mow Roughs ").put("date", "2020-09-18"));
    }

    private void addActivity(JsonObject activity) {
        activities.put(activity.getString("id"), activity);
    }
}
