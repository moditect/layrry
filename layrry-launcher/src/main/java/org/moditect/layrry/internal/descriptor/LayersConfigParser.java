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
package org.moditect.layrry.internal.descriptor;

import com.github.jezza.Toml;
import com.github.jezza.TomlTable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LayersConfigParser {

    public static LayersConfig parseLayersConfig(Path layersConfigFile) {
        if (layersConfigFile.getFileName().toString().endsWith(".toml")) {
            return parseFromToml(layersConfigFile);
        }

        // YAML is the default format
        return parseFromYaml(layersConfigFile);
    }

    private static LayersConfig parseFromYaml(Path layersConfigFile) {
        Constructor c = new Constructor(LayersConfig.class);

        c.setPropertyUtils(new PropertyUtils() {
            @Override
            public Property getProperty(Class<? extends Object> type, String name) {
                if (name.equals("class")) {
                    name = "clazz";
                }
                return super.getProperty(type, name);
            }
        });

        Yaml yaml = new Yaml(c);

        try (InputStream inputStream = layersConfigFile.toUri().toURL().openStream()) {
            return yaml.load(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static LayersConfig parseFromToml(Path layersConfigFile) {
        try (InputStream inputStream = layersConfigFile.toUri().toURL().openStream()) {
            return readFromToml(Toml.from(inputStream));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
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
