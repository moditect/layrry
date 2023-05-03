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
package org.moditect.layrry;

import java.net.URL;
import java.nio.file.Path;

import org.moditect.layrry.config.LayersConfig;
import org.moditect.layrry.config.LayersConfigLoader;
import org.moditect.layrry.internal.LayersFactory;

/**
 * The main entry point for using Layrry in embedded mode. Expects the layers config file to be passed in:
 * <p>
 * <code>
 * Layrry.run(&lt;path/to/layrry.yml&gt;, &lt;args&gt;)
 * Layrry.run(&lt;path/to/layrry.yml&gt;, &lt;path/to/layrry.properties&gt;&lt;args&gt;)
 * </code>
 */
public final class Layrry {

    public static void run(URL layersConfigUrl, Path basedir, String... args) {
        launch(basedir, LayersConfigLoader.loadConfig(layersConfigUrl), args);
    }

    public static void run(URL layersConfigUrl, Path basedir, Path propertiesFile, String... args) {
        if (!propertiesFile.toFile().exists()) {
            throw new IllegalArgumentException("Specified properties config file doesn't exist: " + propertiesFile);
        }

        launch(basedir, LayersConfigLoader.loadConfig(layersConfigUrl, propertiesFile), args);
    }

    public static void run(URL layersConfigUrl, Path basedir, URL propertiesFileUrl, String... args) {
        launch(basedir, LayersConfigLoader.loadConfig(layersConfigUrl, propertiesFileUrl), args);
    }

    public static void run(Path layersConfigFile, Path basedir, String... args) {
        if (!layersConfigFile.toFile().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + layersConfigFile);
        }

        launch(basedir, LayersConfigLoader.loadConfig(layersConfigFile), args);
    }

    public static void run(Path layersConfigFile, Path basedir, Path propertiesFile, String... args) {
        if (!layersConfigFile.toFile().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + layersConfigFile);
        }
        if (!propertiesFile.toFile().exists()) {
            throw new IllegalArgumentException("Specified properties config file doesn't exist: " + propertiesFile);
        }

        launch(basedir, LayersConfigLoader.loadConfig(layersConfigFile, propertiesFile), args);
    }

    public static void run(Path layersConfigFile, Path basedir, URL propertiesFileUrl, String... args) {
        if (!layersConfigFile.toFile().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + layersConfigFile);
        }

        launch(basedir, LayersConfigLoader.loadConfig(layersConfigFile, propertiesFileUrl), args);
    }

    private static void launch(Path basedir, LayersConfig layersConfig, String... args) {
        Layers layers = new LayersFactory().createLayers(layersConfig, basedir);

        layers.run(layersConfig.getMain().getModule() + "/" + layersConfig.getMain().getClazz(), args);
    }
}
