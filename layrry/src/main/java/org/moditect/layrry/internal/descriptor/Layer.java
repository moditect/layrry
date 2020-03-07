package org.moditect.layrry.internal.descriptor;

import java.util.ArrayList;
import java.util.List;

public class Layer {

    private List<String> parents = new ArrayList<>();
    private List<String> modules = new ArrayList<>();

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "Layer [parents=" + parents + ", modules=" + modules + "]";
    }
}
