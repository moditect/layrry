package org.moditect.layrry.internal.descriptor;

import java.util.Map;

public class LayersConfig {

    private Map<String, Layer> layers;
    private Main main;

    public Map<String, Layer> getLayers() {
        return layers;
    }
    public void setLayers(Map<String, Layer> layers) {
        this.layers = layers;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    @Override
    public String toString() {
        return "LayersConfig [layers=" + layers + ", main=" + main + "]";
    }
}
