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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;
import org.moditect.layrry.LayersBuilder;
import org.moditect.layrry.LocalResolve;
import org.moditect.layrry.RemoteResolve;

public class LayersBuilderImpl implements LayersBuilder {

    private LayerBuilderImpl currentLayer;
    private final Map<String, Component> layers = new LinkedHashMap<>();
    private final Set<PluginsDirectory> pluginsDirectories = new HashSet<>();
    private final List<LocalResolve> localResolvers = new ArrayList<>();
    private final List<RemoteResolve> remoteResolvers = new ArrayList<>();

    @Override
    public LayersBuilder pluginsDirectory(String name, Path directory, List<String> parents) {
        pluginsDirectories.add(new PluginsDirectory(name, directory, parents));
        return this;
    }

    @Override
    public LayerBuilder layer(String name) {
        return layer(name, null);
    }

    @Override
    public LayerBuilder layer(String name, String derivedFrom) {
        if (currentLayer != null) {
            addLayer(currentLayer);
        }
        currentLayer = new LayerBuilderImpl(this, name, derivedFrom);

        return currentLayer;
    }

    private void addLayer(LayerBuilderImpl layer) {
        layers.put(layer.getDerivedFrom() != null ? layer.getDerivedFrom() + "-" + layer.getName() : layer.getName(), Component.fromLayer(layer));
    }

    @Override
    public LayersBuilder resolve(LocalResolve resolve) {
        if (null != resolve)
            localResolvers.add(resolve);
        return this;
    }

    @Override
    public LayersBuilder resolve(RemoteResolve resolve) {
        if (null != resolve)
            remoteResolvers.add(resolve);
        return this;
    }

    @Override
    public Layers build() {
        if (currentLayer != null) {
            addLayer(currentLayer);
        }

        return new LayersImpl(pluginsDirectories, layers, localResolvers, remoteResolvers);
    }
}
