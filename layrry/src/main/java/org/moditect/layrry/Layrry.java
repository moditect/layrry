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

import java.util.Map.Entry;

import org.moditect.layrry.internal.Args;
import org.moditect.layrry.internal.descriptor.Layer;
import org.moditect.layrry.internal.descriptor.LayersConfig;
import org.moditect.layrry.internal.descriptor.LayersConfigParser;

import com.beust.jcommander.JCommander;

public class Layrry {

    public static void main(String[] args) throws Exception {
        Args arguments= new Args();

        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(args);

        if (!arguments.getLayersConfig().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + arguments.getLayersConfig());
        }

        LayersConfig layersConfig = LayersConfigParser.parseLayersConfig(arguments.getLayersConfig().toPath());

        LayerBuilder builder = null;
        for(Entry<String, Layer> layer : layersConfig.getLayers().entrySet()) {
            if (builder == null) {
                builder = Layers.layer(layer.getKey());
            }
            else {
                builder = builder.layer(layer.getKey());
            }

            for (String module : layer.getValue().getModules()) {
                builder.withModule(module);
            }

            for (String parent : layer.getValue().getParents()) {
                builder.withParent(parent);
            }
        }

        Layers layers = builder.build();

        layers.run(layersConfig.getMain().getModule() + "/" + layersConfig.getMain().getClazz(), arguments.getMainArgs().toArray(new String[0]));
    }
}

