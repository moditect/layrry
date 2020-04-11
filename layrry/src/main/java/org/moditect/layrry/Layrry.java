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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.moditect.layrry.internal.Args;
import org.moditect.layrry.internal.descriptor.Layer;
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

        if (!arguments.getLayersConfig().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + arguments.getLayersConfig());
        }

        LayersConfig layersConfig = LayersConfigParser.parseLayersConfig(arguments.getLayersConfig().toPath());

        Map<String, List<String>> layerDirsByName = new HashMap<>();

        LayersBuilder builder = Layers.builder();
        for(Entry<String, Layer> layer : layersConfig.getLayers().entrySet()) {
            if (layer.getValue().getDirectory() != null) {
                Path layersConfigDir = arguments.getLayersConfig().toPath().getParent();
                List<String> layerNames = handleDirectoryOfLayers(layer.getValue(), layersConfigDir, builder);
                layerDirsByName.put(layer.getKey(), layerNames);
            }
            else {
                handleLayer(layer, layerDirsByName, builder);
            }
        }

        Layers layers = builder.build();

        layers.run(layersConfig.getMain().getModule() + "/" + layersConfig.getMain().getClazz(), arguments.getMainArgs().toArray(new String[0]));
    }

    private static void handleLayer(Entry<String, Layer> layer, Map<String, List<String>> layerDirsByName,
            LayersBuilder builder) {
        LayerBuilder layerBuilder = builder.layer(layer.getKey());

        for (String module : layer.getValue().getModules()) {
            layerBuilder.withModule(module);
        }

        for (String parent : layer.getValue().getParents()) {
            List<String> layersFromDirectory = layerDirsByName.get(parent);
            if (!layersFromDirectory.isEmpty()) {
                for (String layerFromDirectory : layersFromDirectory) {
                    layerBuilder.withParent(layerFromDirectory);
                }
            }
            else {
                layerBuilder.withParent(parent);
            }
        }
    }

    private static List<String> handleDirectoryOfLayers(Layer layer,
            Path layersConfigDir, LayersBuilder builder) {
        Path layersDir = layersConfigDir.resolve(layer.getDirectory());
        if (!Files.isDirectory(layersDir)) {
            throw new IllegalArgumentException("Specified layer directory doesn't exist: " + layersDir);
        }

        ArrayList<String> layerNames = new ArrayList<String>();
        List<Path> layerDirs = getLayerDirs(layersDir);
        for (Path layerDir : layerDirs) {
            LayerBuilder layerBuilder = builder.layer(layerDir.getFileName().toString());

            layerBuilder.withModulesIn(layerDir);
            layerNames.add(layerDir.getFileName().toString());
            for (String parent : layer.getParents()) {
                layerBuilder.withParent(parent);
            }
        }

        return layerNames;
    }

    private static List<Path> getLayerDirs(Path layersDir) {
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
