package org.moditect.layrry.internal;

import java.util.ArrayList;
import java.util.List;

import org.moditect.layrry.LayerBuilder;
import org.moditect.layrry.Layers;

public class LayerBuilderImpl implements LayerBuilder {

    private final LayersBuilder layersBuilder;
    private final String name;
    private final List<String> moduleGavs;
    private final List<String> parents;

    public LayerBuilderImpl(LayersBuilder layersBuilder, String name) {
        this.layersBuilder = layersBuilder;
        this.name = name;
        this.moduleGavs = new ArrayList<>();
        this.parents = new ArrayList<>();
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
        layersBuilder.addLayer(this.name, this.moduleGavs, this.parents);
        return new LayerBuilderImpl(layersBuilder, name);
    }

    @Override
    public Layers build() {
        layersBuilder.addLayer(this.name, this.moduleGavs, this.parents);
        return layersBuilder.build();
    }
}
