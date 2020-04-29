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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;
import org.moditect.layrry.LayersBuilder;
import org.moditect.layrry.internal.descriptor.Layer;
import org.moditect.layrry.internal.descriptor.LayersConfig;

/**
 * Creates a {@link Layers} instance based on a given configuration.
 */
public class LayersFactory {

    public Layers createLayers(LayersConfig layersConfig, Path layersConfigDir) {
        Map<String, List<String>> layerDirsByName = new HashMap<>();

        LayersBuilder builder = Layers.builder();
        for(Entry<String, Layer> layer : layersConfig.getLayers().entrySet()) {
            if (layer.getValue().getDirectory() != null) {
                List<String> layerNames = handleDirectoryOfLayers(layer, layersConfigDir, builder);
                layerDirsByName.put(layer.getKey(), layerNames);
            }
            else {
                handleLayer(layer, layerDirsByName, builder);
            }
        }

        return builder.build();
    }

    private void handleLayer(Entry<String, Layer> layer, Map<String, List<String>> layerDirsByName,
            LayersBuilder builder) {
        LayerBuilder layerBuilder = builder.layer(layer.getKey());

        for (String module : layer.getValue().getModules()) {
            layerBuilder.withModule(module);
        }

        for (String parent : layer.getValue().getParents()) {
            List<String> layersFromDirectory = layerDirsByName.get(parent);
            if (layersFromDirectory != null && !layersFromDirectory.isEmpty()) {
                for (String layerFromDirectory : layersFromDirectory) {
                    layerBuilder.withParent(layerFromDirectory);
                }
            }
            else {
                layerBuilder.withParent(parent);
            }
        }
    }

    /**
     * Processes a directory of layers, i.e. plug-ins.
     */
    private List<String> handleDirectoryOfLayers(Entry<String, Layer> layer,
            Path layersConfigDir, LayersBuilder builder) {
        Path layersDir = layersConfigDir.resolve(layer.getValue().getDirectory()).normalize();
        if (!Files.isDirectory(layersDir)) {
            throw new IllegalArgumentException("Specified layer directory doesn't exist: " + layersDir);
        }

        ArrayList<String> layerNames = new ArrayList<String>();
        List<Path> layerDirs = getLayerDirs(layersDir);
        for (Path layerDir : layerDirs) {
            LayerBuilder layerBuilder = builder.layer(layerDir.getFileName().toString(), layer.getKey());

            layerBuilder.withModulesIn(layerDir);
            layerNames.add(layerDir.getFileName().toString());
            for (String parent : layer.getValue().getParents()) {
                layerBuilder.withParent(parent);
            }
        }

        return layerNames;
    }

    private List<Path> getLayerDirs(Path layersDir) {
        List<Path> layers;
        try {
            layers = Files.walk(layersDir, 1)
                         .filter(Files::isDirectory)
                         .collect(Collectors.toList());
            layers.remove(0);

            return layers;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
