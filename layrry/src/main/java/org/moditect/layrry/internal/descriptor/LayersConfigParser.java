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
