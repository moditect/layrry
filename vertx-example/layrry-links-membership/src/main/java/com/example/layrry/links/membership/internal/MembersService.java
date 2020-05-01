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
package com.example.layrry.links.membership.internal;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MembersService {

    private Map<String, JsonObject> members = new HashMap<>();

    public MembersService() {
        setUpInitialData();
    }

    public JsonObject getMember(String memberId) {
        return members.get(memberId);
    }

    public void addMember(String memberId, JsonObject member) {
        members.put(memberId, member);
    }

    public JsonArray getMembers() {
        JsonArray array = new JsonArray();
        members.forEach((k, v) -> array.add(v));
        return array;
    }

    private void setUpInitialData() {
        addMember(new JsonObject().put("id", "123").put("name", "Rudy Rough").put("handicap", 14.1).put("dateOfBirth", "1980-12-03"));
        addMember(new JsonObject().put("id", "456").put("name", "Thessa Tee").put("handicap", 7.8).put("dateOfBirth", "1990-03-14"));
        addMember(new JsonObject().put("id", "789").put("name", "Frieda Fairway").put("handicap", 35.2).put("dateOfBirth", "1970-08-30"));
    }

    private void addMember(JsonObject member) {
        members.put(member.getString("id"), member);
    }
}
