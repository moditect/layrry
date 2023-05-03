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
package org.moditect.layrry.example.links.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moditect.layrry.launcher.LayrryLauncher;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

// Upgrade to Junit5. Use a condition to skip only on CI
@Ignore("Test is flaky on CI")
public class LayrryLinksTest {
    private static final String TOURNAMENT_PLUGIN_NAME = "tournament";
    private static final String GREENKEEPING_PLUGIN_NAME = "greenkeeping";

    private String layersConfig;
    private Path pluginDir1;
    private Path pluginDir2;
    private Path preparedPluginDir;

    @Before
    public void validateConfig() {
        layersConfig = System.getProperty("layersConfig");
        if (layersConfig == null) {
            throw new IllegalStateException("Specify layers.yml file via 'layersConfig' system property");
        }

        String pluginDir1Prop = System.getProperty("pluginDir1");
        if (pluginDir1Prop == null) {
            throw new IllegalStateException("Specify plug-in directory 1 via 'pluginDir1' system property");
        }
        pluginDir1 = new File(pluginDir1Prop).toPath();

        String pluginDir2Prop = System.getProperty("pluginDir2");
        if (pluginDir2Prop == null) {
            throw new IllegalStateException("Specify plug-in directory 2 via 'pluginDir2' system property");
        }
        pluginDir2 = new File(pluginDir2Prop).toPath();

        String preparedPluginDirProp = System.getProperty("preparedPluginDir");
        if (preparedPluginDirProp == null) {
            throw new IllegalStateException("Specify plug-in directory via 'preparedPluginDir' system property");
        }
        preparedPluginDir = new File(preparedPluginDirProp).toPath();
    }

    @Test
    public void canAddAndRemovePlugin() throws Exception {
        FilesHelper.deleteFolder(preparedPluginDir.resolve(TOURNAMENT_PLUGIN_NAME), pluginDir1);
        Files.createDirectories(pluginDir1);
        Files.createDirectories(pluginDir2);

        LayrryLauncher.launch("--layers-config", layersConfig);

        given()
                .when().get("/members/123")
                .then()
                .statusCode(200)
                .body("name", equalTo("Rudy Rough"));

        FilesHelper.copyFiles(preparedPluginDir.resolve(TOURNAMENT_PLUGIN_NAME), pluginDir1);

        Files.find(pluginDir1,
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .forEach(System.out::println);

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

        FilesHelper.deleteFolder(preparedPluginDir.resolve(TOURNAMENT_PLUGIN_NAME), pluginDir1);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            return given()
                    .when()
                    .get("/tournaments")
                    .then()
                    .extract().statusCode() == 404;
        });
    }

    @Test
    public void canAddPluginToEmptyPluginsDirectory() throws Exception {
        FilesHelper.deleteFolder(preparedPluginDir.resolve(GREENKEEPING_PLUGIN_NAME), pluginDir1);
        Files.createDirectories(pluginDir1);
        Files.createDirectories(pluginDir2);

        LayrryLauncher.main("--layers-config", layersConfig);

        FilesHelper.copyFiles(preparedPluginDir.resolve(GREENKEEPING_PLUGIN_NAME), pluginDir2);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            return given()
                    .when()
                    .get("/greenkeeping-activities")
                    .then()
                    .extract().statusCode() == 200;
        });

        given()
                .when().get("/greenkeeping-activities/123")
                .then()
                .statusCode(200)
                .body("name", equalTo("Sand Front 9"));
    }
}
