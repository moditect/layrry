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
package org.moditect.layrry.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.moditect.layrry.Layrry;
import org.moditect.layrry.config.LayersConfigParser;
import org.moditect.layrry.launcher.internal.Args;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * The main entry point for using Layrry on the command line. Expects the layers config file to be passed in:
 * <p>
 * <code>
 * LayrryLauncher --layers-config &lt;path/to/layrry.yml&gt;
 * LayrryLauncher --layers-config &lt;path/to/layrry.yml&gt; --properties &lt;path/to/layrry.properties&gt;
 * LayrryLauncher --layers-config https://host/path/to/layrry.yml --properties &lt;path/to/layrry.properties&gt;
 * LayrryLauncher --layers-config https://host/path/to/layrry.yml --properties https://host/path/to/layrry.properties
 * </code>
 */
public final class LayrryLauncher {

    public static void main(String... args) throws Exception {
        launch(args);
    }

    public static void launch(String... args) throws Exception {
        Args arguments = new Args();

        JCommander jCommander = JCommander.newBuilder()
                .programName(LayrryLauncher.class.getName())
                .addObject(arguments)
                .build();

        try {
            jCommander.parse(args);
        }
        catch (ParameterException e) {
            printUsage(jCommander);
            return;
        }

        if (arguments.isHelp()) {
            printUsage(jCommander);
            return;
        }

        if (arguments.isVersion()) {
            System.out.println(LayrryLauncher.class.getPackage().getImplementationVersion());
            return;
        }

        String[] parsedArgs = arguments.getMainArgs().toArray(new String[0]);

        File basedir = arguments.getBasedir();

        String propertiesFile = arguments.getProperties();
        URL propertiesFileUrl = toUrl(propertiesFile);

        String layersConfig = arguments.getLayersConfig();
        if (null == layersConfig || layersConfig.isEmpty()) {
            layersConfig = resolveLayersConfigFile(basedir);
        }
        if (null == layersConfig) {
            jCommander.getConsole()
                    .println("Missing --layers-config parameter or local file named layers[" +
                            String.join("|", getSupportedConfigFormats()) + "]");
            jCommander.getConsole().println("");
            printUsage(jCommander);
            return;
        }

        URL layersConfigUrl = toUrl(layersConfig);

        if (null != layersConfigUrl) {
            if (basedir == null) {
                basedir = new File(System.getProperty("user.dir"));
            }

            if (null == propertiesFile) {
                Layrry.run(layersConfigUrl, basedir.toPath(), parsedArgs);
            }
            else if (null != propertiesFileUrl) {
                Layrry.run(layersConfigUrl, basedir.toPath(), propertiesFileUrl, parsedArgs);
            }
            else {
                Layrry.run(layersConfigUrl, basedir.toPath(), Paths.get(propertiesFile).toAbsolutePath(), parsedArgs);
            }
        }
        else {
            Path layersConfigPath = Paths.get(layersConfig).toAbsolutePath();

            Path basedirPath = layersConfigPath.getParent();
            if (null != basedir) {
                basedirPath = basedir.toPath();
            }

            if (null == propertiesFile) {
                Layrry.run(layersConfigPath, basedirPath, parsedArgs);
            }
            else if (null != propertiesFileUrl) {
                Layrry.run(layersConfigPath, basedirPath, propertiesFileUrl, parsedArgs);
            }
            else {
                Layrry.run(layersConfigPath, basedirPath, Paths.get(propertiesFile).toAbsolutePath(), parsedArgs);
            }
        }
    }

    private static URL toUrl(String input) {
        try {
            return new URL(input);
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    private static void printUsage(JCommander jCommander) {
        jCommander.usage();

        StringBuilder sb = new StringBuilder("Supported config formats are ")
                .append(getSupportedConfigFormats());
        jCommander.getConsole().println(sb.toString());
    }

    private static Set<String> getSupportedConfigFormats() {
        Set<String> extensions = new LinkedHashSet<>();

        ServiceLoader<LayersConfigParser> parsers = ServiceLoader.load(LayersConfigParser.class,
                LayrryLauncher.class.getClassLoader());

        for (LayersConfigParser parser : parsers) {
            extensions.add("." + parser.getPreferredFileExtension());
        }

        return extensions;
    }

    private static String resolveLayersConfigFile(File basedir) {
        if (basedir == null) {
            basedir = new File(System.getProperty("user.dir"));
        }

        ServiceLoader<LayersConfigParser> parsers = ServiceLoader.load(LayersConfigParser.class,
                LayrryLauncher.class.getClassLoader());

        for (LayersConfigParser parser : parsers) {
            File layersConfigFile = new File(basedir, "layers." + parser.getPreferredFileExtension());
            if (layersConfigFile.exists()) {
                return layersConfigFile.getAbsolutePath();
            }
        }

        return null;
    }
}
