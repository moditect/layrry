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

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;
import org.moditect.layrry.LayersBuilder;

public class LayersBuilderImpl implements LayersBuilder {

    private LayerBuilderImpl currentLayer;
    private Map<String, Component> layers = new LinkedHashMap<>();
    private Set<PluginsDirectory> pluginsDirectories = new HashSet<>();

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
    public Layers build() {
        if (currentLayer != null) {
            addLayer(currentLayer);
        }

        return new LayersImpl(pluginsDirectories, layers);
    }
}
