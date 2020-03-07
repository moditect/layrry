package org.moditect.layrry.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.moditect.layrry.Layers;

public class LayersBuilder {

    private Map<String, Layer> layers = new LinkedHashMap<>();

    public Layers build() {
        return new LayersImpl(layers);
    }

    public void addLayer(String name, List<String> moduleGavs, List<String> parents) {
        layers.put(name, new Layer(name, moduleGavs, parents));
    }
}