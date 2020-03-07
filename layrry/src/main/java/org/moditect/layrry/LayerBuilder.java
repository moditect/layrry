package org.moditect.layrry;

public interface LayerBuilder {

    LayerBuilder withModule(String moduleGav);

    LayerBuilder withParent(String parent);

    LayerBuilder layer(String name);

    Layers build();
}
