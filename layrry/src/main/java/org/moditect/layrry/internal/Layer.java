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
import java.util.Collections;
import java.util.List;

public class Layer {

    private final String name;
    private Path layerDir;
    private final List<String> moduleGavs;
    private final List<String> parents;

    public Layer(String name, Path layerDir, List<String> moduleGavs, List<String> parents) {
        this.name = name;
        this.layerDir = layerDir;
        this.moduleGavs = Collections.unmodifiableList(moduleGavs);
        this.parents = Collections.unmodifiableList(parents);
    }

    public String getName() {
        return name;
    }

    public Path getLayerDir() {
        return layerDir;
    }

    public List<String> getModuleGavs() {
        return moduleGavs;
    }

    public List<String> getParents() {
        return parents;
    }

    @Override
    public String toString() {
        return "Layer [name=" + name + ", layerDir=" + layerDir + ", moduleGavs=" + moduleGavs + ", parents=" + parents
                + "]";
    }
}
