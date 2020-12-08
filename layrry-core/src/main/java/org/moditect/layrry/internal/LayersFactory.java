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
package org.moditect.layrry.internal;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;
import org.moditect.layrry.LayersBuilder;
import org.moditect.layrry.config.LayersConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

/**
 * Creates a {@link Layers} instance based on a given configuration.
 */
public class LayersFactory {

    public Layers createLayers(LayersConfig layersConfig, Path layersConfigDir) {
        LayersBuilder builder = Layers.builder();
        for (Entry<String, org.moditect.layrry.config.Layer> layer : layersConfig.getLayers().entrySet()) {
            if (layer.getValue().getDirectory() != null) {
                handlePluginLayer(layer, layersConfigDir, builder);
            } else {
                handleLayer(layer, builder);
            }
        }

        return builder.build();
    }

    private void handleLayer(Entry<String, org.moditect.layrry.config.Layer> layer,
                             LayersBuilder builder) {
        LayerBuilder layerBuilder = builder.layer(layer.getKey());

        for (String module : layer.getValue().getModules()) {
            layerBuilder.withModule(module);
        }

        for (String parent : layer.getValue().getParents()) {
            layerBuilder.withParent(parent);
        }
    }

    /**
     * Processes a directory of layers, i.e. plug-ins.
     */
    private void handlePluginLayer(Entry<String, org.moditect.layrry.config.Layer> layer,
                                   Path layersConfigDir, LayersBuilder builder) {
        Path pluginDirectory = layersConfigDir.resolve(layer.getValue().getDirectory()).normalize();
        if (!Files.isDirectory(pluginDirectory)) {
            throw new IllegalArgumentException("Specified layer directory doesn't exist: " + pluginDirectory);
        }

        builder.pluginsDirectory(layer.getKey(), pluginDirectory, layer.getValue().getParents());

        LayerBuilder layerBuilder = builder.layer(layer.getKey());
        layerBuilder.withModulesIn(pluginDirectory);

        for (String parent : layer.getValue().getParents()) {
            layerBuilder.withParent(parent);
        }
    }
}
