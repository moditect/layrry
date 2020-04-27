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

import java.io.File;

import org.moditect.layrry.internal.Args;
import org.moditect.layrry.internal.LayersFactory;
import org.moditect.layrry.internal.descriptor.LayersConfig;
import org.moditect.layrry.internal.descriptor.LayersConfigParser;

import com.beust.jcommander.JCommander;

/**
 * The main entry point for using Layrry. Expects the layers config file to be passed in:
 * <p>
 * <code>
 * Layrry --layers-config &lt;path/to/layrry.yml&gt;
 * </code>
 */
public class Layrry {

    public static void main(String... args) throws Exception {
        Args arguments= new Args();

        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(args);

        File layersConfigFile = arguments.getLayersConfig().getAbsoluteFile();

        if (!layersConfigFile.exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + layersConfigFile);
        }

        LayersConfig layersConfig = LayersConfigParser.parseLayersConfig(layersConfigFile.toPath());
        Layers layers = new LayersFactory().createLayers(layersConfig, layersConfigFile.toPath().getParent());

        layers.run(
                layersConfig.getMain().getModule() + "/" + layersConfig.getMain().getClazz(),
                arguments.getMainArgs().toArray(new String[0])
        );
    }
}
