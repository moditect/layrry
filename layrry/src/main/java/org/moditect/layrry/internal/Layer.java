package org.moditect.layrry.internal;

import java.util.Collections;
import java.util.List;

public class Layer {

    private final String name;
    private final List<String> moduleGavs;
    private final List<String> parents;

    public Layer(String name, List<String> moduleGavs, List<String> parents) {
        this.name = name;
        this.moduleGavs = Collections.unmodifiableList(moduleGavs);
        this.parents = Collections.unmodifiableList(parents);
    }

    public String getName() {
        return name;
    }

    public List<String> getModuleGavs() {
        return moduleGavs;
    }

    public List<String> getParents() {
        return parents;
    }

    @Override
    public String toString() {
        return "Layer [name=" + name + ", moduleGavs=" + moduleGavs + ", parents=" + parents + "]";
    }
}
