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
package org.moditect.layrry;

import org.moditect.layrry.config.LayersConfig;
import org.moditect.layrry.config.LayersConfigLoader;
import org.moditect.layrry.internal.LayersFactory;

import java.nio.file.Path;

/**
 * The main entry point for using Layrry in embedded mode. Expects the layers config file to be passed in:
 * <p>
 * <code>
 * Layrry.run(&lt;path/to/layrry.yml&gt;, &lt;args&gt;)
 * </code>
 */
public final class Layrry {

    public static void run(Path layersConfigFile, String... args) {
        if (!layersConfigFile.toFile().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + layersConfigFile);
        }

        LayersConfig layersConfig = LayersConfigLoader.loadConfig(layersConfigFile);
        Layers layers = new LayersFactory().createLayers(layersConfig, layersConfigFile.getParent());

        layers.run(layersConfig.getMain().getModule() + "/" + layersConfig.getMain().getClazz(), args);
    }
}
