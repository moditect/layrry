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
package org.moditect.layrry.internal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;
import org.moditect.layrry.LayersBuilder;
import org.moditect.layrry.LocalResolve;
import org.moditect.layrry.RemoteResolve;
import org.moditect.layrry.Resolvers;
import org.moditect.layrry.config.LayersConfig;
import org.moditect.layrry.config.Resolve;

/**
 * Creates a {@link Layers} instance based on a given configuration.
 */
public class LayersFactory {

    public Layers createLayers(LayersConfig layersConfig, Path layersConfigDir) {
        LayersBuilder builder = Layers.builder();
        for (Entry<String, org.moditect.layrry.config.Layer> layer : layersConfig.getLayers().entrySet()) {
            if (layer.getValue().getDirectory() != null) {
                handlePluginLayer(layer, layersConfigDir, builder);
            }
            else {
                handleLayer(layer, builder);
            }
        }

        return configureResolve(layersConfig, layersConfigDir, builder);
    }

    private Layers configureResolve(LayersConfig layersConfig, Path layersConfigDir, LayersBuilder builder) {
        Resolve resolve = layersConfig.getResolve();
        if (resolve == null) {
            return builder.build();
        }

        RemoteResolve remote = Resolvers.remote();
        remote.enabled(resolve.isRemote());
        remote.workOffline(resolve.isWorkOffline());
        remote.withMavenCentralRepo(resolve.isUseMavenCentral());
        String fromFilePath = resolve.getFromFile();
        if (fromFilePath != null && !fromFilePath.isEmpty()) {
            remote.fromFile(layersConfigDir.resolve(resolve.getFromFile()).toAbsolutePath());
        }
        builder.resolve(remote);

        LocalResolve local = Resolvers.local();
        resolve.getLocalRepositories().forEach((id, repository) -> local.withLocalRepo(id,
                layersConfigDir.resolve(repository.getPath()),
                repository.getLayout()));
        builder.resolve(local);

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
