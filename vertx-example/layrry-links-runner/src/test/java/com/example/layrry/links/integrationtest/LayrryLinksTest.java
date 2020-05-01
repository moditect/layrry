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
package com.example.layrry.links.integrationtest;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.moditect.layrry.Layrry;

public class LayrryLinksTest {


    private String layersConfig;
    private Path pluginDir;
    private Path preparedPluginDir;

    @Before
    public void validateConfig() {
        layersConfig = System.getProperty("layersConfig");
        if (layersConfig == null) {
            throw new IllegalStateException("Specify layers.yml file via 'layersConfig' system property");
        }

        String pluginDirProp = System.getProperty("pluginDir");
        if (pluginDirProp == null) {
            throw new IllegalStateException("Specify plug-in directory via 'pluginDir' system property");
        }
        pluginDir = new File(pluginDirProp).toPath();

        String preparedPluginDirProp = System.getProperty("preparedPluginDir");
        if (preparedPluginDirProp == null) {
            throw new IllegalStateException("Specify plug-in directory via 'preparedPluginDir' system property");
        }
        preparedPluginDir = new File(preparedPluginDirProp).toPath();
    }

    @Test
    public void runLayers() throws Exception {
        FilesHelper.deleteFolder(pluginDir.resolve("tournament"));

        Layrry.main("--layers-config", layersConfig);

        given()
            .when().get("/members/123")
            .then()
                .statusCode(200)
                .body("name", equalTo("Rudy Rough"));

        FilesHelper.copyFolder(preparedPluginDir.resolve("tournament"), pluginDir.resolve("tournament"));

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            return given()
            .when()
                .get("/tournaments")
            .then()
            .extract().statusCode() == 200;
        });

        given()
        .when().get("/tournaments/123")
        .then()
            .statusCode(200)
            .body("name", equalTo("Easter 36"));
    }
}
