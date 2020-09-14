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
package org.moditect.layrry.config.yaml;

import org.moditect.layrry.config.LayersConfig;
import org.moditect.layrry.config.LayersConfigParser;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class YamlLayersConfigParser implements LayersConfigParser {

    @Override
    public boolean supports(Path layersConfigFile) {
        return layersConfigFile.getFileName().toString().endsWith(".yml");
    }

    @Override
    public LayersConfig parse(InputStream inputStream) throws IOException {
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

        return yaml.load(inputStream);
    }

}
