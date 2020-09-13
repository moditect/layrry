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
package org.moditect.layrry.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ServiceLoader;

public class LayersConfigLoader {

    public static LayersConfig loadConfig(Path layersConfigFile) {
        ServiceLoader<LayersConfigParser> parsers = ServiceLoader.load(LayersConfigParser.class, LayersConfigLoader.class.getClassLoader());

        for (LayersConfigParser parser : parsers) {
            if (parser.supports(layersConfigFile)) {
                try (InputStream inputStream = layersConfigFile.toUri().toURL().openStream()) {
                    return parser.parse(inputStream);
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("Unexpected error parsing config file. " + layersConfigFile, e);
                }
            }
        }
        throw new IllegalArgumentException("Unsupported config format. " + layersConfigFile);
    }

}
