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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class LayersConfigParser {

    public static LayersConfig parseLayersConfig(Path layersConfigFile) {
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
}
