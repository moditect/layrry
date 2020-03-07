package org.moditect.layrry;

import org.moditect.layrry.internal.LayerBuilderImpl;
import org.moditect.layrry.internal.LayersBuilder;

public interface Layers {

    static LayerBuilder layer(String name) {
        return new LayerBuilderImpl(new LayersBuilder(), name);
    }

    void run(String main, String... args);
}
