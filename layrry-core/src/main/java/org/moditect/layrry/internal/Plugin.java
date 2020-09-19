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
import java.util.List;

/**
 * A plug-in is a layer represented by a file-system directory, containing the
 * layer's modules.
 */
public class Plugin extends Component {

    private final Path layerDir;

    public Plugin(String name, String derivedFrom, Path layerDir, List<String> parents) {
        super(derivedFrom + "-" + name, parents);
        this.layerDir = layerDir;
    }

    public Path getLayerDir() {
        return layerDir;
    }

    @Override
    public boolean isPlugin() {
        return true;
    }

    @Override
    public String toString() {
        return "Plugin [layerDir=" + layerDir + "]";
    }
}
