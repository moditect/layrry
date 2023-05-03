/*
 *  Copyright 2020 - 2023 The ModiTect authors
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.moditect.layrry.config.Layer;
import org.moditect.layrry.config.LayersConfig;
import org.moditect.layrry.config.LayersConfigParser;
import org.moditect.layrry.config.Main;
import org.moditect.layrry.config.Repository;
import org.moditect.layrry.config.Resolve;

import com.github.jezza.Toml;
import com.github.jezza.TomlTable;

public class TomlLayersConfigParser implements LayersConfigParser {

    @Override
    public Set<String> getSupportedMimeTypes() {
        return Set.of(
                "text/vnd.toml",
                "application/vnd.toml",
                "text/toml",
                "text/x-toml",
                "application/toml",
                "application/x-toml");
    }

    @Override
    public String getPreferredFileExtension() {
        return "toml";
    }

    @Override
    public boolean supports(Path layersConfigFile) {
        return layersConfigFile.getFileName().toString().endsWith(".toml");
    }

    @Override
    public LayersConfig parse(InputStream inputStream) throws IOException {
        return readFromToml(Toml.from(inputStream));
    }

    private static LayersConfig readFromToml(TomlTable toml) {
        LayersConfig config = new LayersConfig();

        readResolve(config, (TomlTable) toml.get("resolve"));
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

    private static void readResolve(LayersConfig config, TomlTable table) {
        Resolve resolve = new Resolve();
        config.setResolve(resolve);

        if (table != null) {
            resolve.setRemote((Boolean) table.getOrDefault("remote", true));
            resolve.setWorkOffline((Boolean) table.getOrDefault("workOffline", false));
            resolve.setUseMavenCentral((Boolean) table.getOrDefault("useMavenCentral", true));
            resolve.setFromFile((String) table.get("fromFile"));
            readRepositories(resolve, (TomlTable) table.get("localRepositories"));
        }
    }

    private static void readRepositories(Resolve resolve, TomlTable table) {
        Map<String, Repository> repositories = new LinkedHashMap<>();
        resolve.setLocalRepositories(repositories);

        if (table == null)
            return;

        table.entrySet().forEach(entry -> {
            Repository repository = new Repository();
            TomlTable repositoryTable = (TomlTable) entry.getValue();
            repository.setPath((String) repositoryTable.get("path"));
            repository.setLayout((String) repositoryTable.get("layout"));
            repositories.put(entry.getKey(), repository);
        });
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
