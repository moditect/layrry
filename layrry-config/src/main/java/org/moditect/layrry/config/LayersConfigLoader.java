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
package org.moditect.layrry.config;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import kr.motd.maven.os.Detector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;
import java.util.ServiceLoader;

public class LayersConfigLoader {

    private static final String JAVAFX_OS_CLASSIFIER = "javafx.os.classifier";

    public static LayersConfig loadConfig(Path layersConfigFile) {
        return loadConfig(layersConfigFile, new Properties());
    }

    public static LayersConfig loadConfig(Path layersConfigFile, Path propertiesFile) {
        Properties properties = new Properties();

        try (InputStream inputStream = propertiesFile.toUri().toURL().openStream()) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unexpected error reading properties file. " + propertiesFile, e);
        }

        return loadConfig(layersConfigFile, properties);
    }

    private static LayersConfig loadConfig(Path layersConfigFile, Properties properties) {
        ServiceLoader<LayersConfigParser> parsers = ServiceLoader.load(LayersConfigParser.class, LayersConfigLoader.class.getClassLoader());

        for (LayersConfigParser parser : parsers) {
            if (parser.supports(layersConfigFile)) {
                try (InputStream inputStream = layersConfigFile.toUri().toURL().openStream()) {
                    return parser.parse(replacePlaceholders(properties, inputStream));
                } catch (IOException e) {
                    throw new IllegalArgumentException("Unexpected error parsing config file. " + layersConfigFile, e);
                }
            }
        }
        throw new IllegalArgumentException("Unsupported config format. " + layersConfigFile);
    }

    private static InputStream replacePlaceholders(Properties properties, InputStream inputStream) {
        // detect OS
        OsDetector detector = new OsDetector();

        Properties props = new Properties();
        // 1. custom properties
        props.putAll(properties);
        // 2. system properties
        props.putAll(System.getProperties());
        // 3. os properties
        props.putAll(detector.getProperties());
        // 4. special case for JavaFX OS classifier
        String javafxClassifier = resolveJavaFxClassifier(detector.get(Detector.DETECTED_NAME));
        if (null != javafxClassifier) {
            props.put(JAVAFX_OS_CLASSIFIER, javafxClassifier);
        }

        // evaluate expressions
        StringWriter input = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new InputStreamReader(inputStream), "layrry");
        mustache.execute(input, props);
        input.flush();

        return new ByteArrayInputStream(input.toString().getBytes());
    }

    private static String resolveJavaFxClassifier(String os) {
        switch (os) {
            case "linux":
                return "linux";
            case "windows":
                return "win";
            case "osx":
                return "mac";
            default:
                // any other OS is not supported, leave classifier as null
                return null;
        }
    }

    private static class OsDetector extends Detector {
        private final Properties props = new Properties();

        private OsDetector() {
            props.put("failOnUnknownOS", "false");
            detect(props, Collections.emptyList());
        }

        private Properties getProperties() {
            return props;
        }

        private String get(String key) {
            return props.getProperty(key);
        }

        @Override
        protected void log(String message) {
            // quiet
        }

        @Override
        protected void logProperty(String name, String value) {
            // quiet
        }
    }
}
