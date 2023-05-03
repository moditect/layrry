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
import java.util.List;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;

public class LayerBuilderImpl implements LayerBuilder {

    private final LayersBuilderImpl layersBuilder;
    private final String name;
    private final String derivedFrom;
    private Path layerDir;
    private final List<String> moduleGavs;
    private final List<String> parents;

    public LayerBuilderImpl(LayersBuilderImpl layersBuilder, String name, String derivedFrom) {
        this.layersBuilder = layersBuilder;
        this.name = name;
        this.derivedFrom = derivedFrom;
        this.moduleGavs = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    @Override
    public LayerBuilder withModulesIn(Path layerDir) {
        this.layerDir = layerDir;
        return this;
    }

    @Override
    public LayerBuilder withModule(String moduleGav) {
        this.moduleGavs.add(moduleGav);
        return this;
    }

    @Override
    public LayerBuilder withParent(String parent) {
        this.parents.add(parent);
        return this;
    }

    @Override
    public LayerBuilder layer(String name) {
        return layersBuilder.layer(name);
    }

    @Override
    public Layers build() {
        return layersBuilder.build();
    }

    public Path getLayerDir() {
        return layerDir;
    }

    public String getName() {
        return name;
    }

    public String getDerivedFrom() {
        return derivedFrom;
    }

    public List<String> getModuleGavs() {
        return moduleGavs;
    }

    public List<String> getParents() {
        return parents;
    }
}
