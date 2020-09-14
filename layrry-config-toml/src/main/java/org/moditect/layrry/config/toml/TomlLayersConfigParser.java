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
package org.moditect.layrry.config.toml;

import com.github.jezza.Toml;
import com.github.jezza.TomlTable;
import org.moditect.layrry.config.Layer;
import org.moditect.layrry.config.LayersConfig;
import org.moditect.layrry.config.LayersConfigParser;
import org.moditect.layrry.config.Main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TomlLayersConfigParser implements LayersConfigParser {

    @Override
    public boolean supports(Path layersConfigFile) {
        return layersConfigFile.getFileName().toString().endsWith(".toml");
    }

    @Override
    public LayersConfig parse(InputStream inputStream) throws IOException {
        TomlTable toml = Toml.from(inputStream);

        LayersConfig config = new LayersConfig();

        readLayers(config, (TomlTable) toml.get("layers"));
        readMain(config, (TomlTable) toml.get("main"));

        return config;
    }

    private static LayersConfig readFromToml(TomlTable toml) {
        LayersConfig config = new LayersConfig();

        readLayers(config, (TomlTable) toml.get("layers"));
        readMain(config, (TomlTable) toml.get("main"));

        return config;
    }

    private static void readMain(LayersConfig config, TomlTable table) {
        Main main = new Main();
        config.setMain(main);

        main.setModule(String.valueOf(table.get("module")));
        main.setClazz(String.valueOf(table.get("class")));
    }

    private static void readLayers(LayersConfig config, TomlTable table) {
        final Map<String, Layer> layers = new LinkedHashMap<>();
        config.setLayers(layers);

        table.entrySet().forEach(entry -> {
            Layer layer = new Layer();
            TomlTable layerTable = (TomlTable) entry.getValue();
            if (layerTable.asMap().containsKey("parents")) {
                layer.setParents((List<String>) layerTable.get("parents"));
            }
            if (layerTable.asMap().containsKey("modules")) {
                layer.setModules((List<String>) layerTable.get("modules"));
            }
            if (layerTable.asMap().containsKey("directory")) {
                layer.setDirectory(String.valueOf(layerTable.get("directory")));
            }
            layers.put(entry.getKey(), layer);
        });
    }
}
