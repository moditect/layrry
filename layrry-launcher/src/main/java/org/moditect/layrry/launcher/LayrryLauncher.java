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
package org.moditect.layrry.launcher;

import com.beust.jcommander.JCommander;
import org.moditect.layrry.Layrry;
import org.moditect.layrry.launcher.internal.Args;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(args);

        String[] parsedArgs = arguments.getMainArgs().toArray(new String[0]);

        File basedir = arguments.getBasedir();

        String propertiesFile = arguments.getProperties();
        URL propertiesFileUrl = toUrl(propertiesFile);

        String layersConfig = arguments.getLayersConfig();
        URL layersConfigUrl = toUrl(layersConfig);

        if (null != layersConfigUrl) {
            if (basedir == null) {
                basedir = new File(System.getProperty("user.dir"));
            }

            if (null == propertiesFile) {
                Layrry.run(layersConfigUrl, basedir.toPath(), parsedArgs);
            } else if (null != propertiesFileUrl) {
                Layrry.run(layersConfigUrl, basedir.toPath(), propertiesFileUrl, parsedArgs);
            } else {
                Layrry.run(layersConfigUrl, basedir.toPath(), Paths.get(propertiesFile).toAbsolutePath(), parsedArgs);
            }
        } else {
            Path layersConfigPath = Paths.get(layersConfig).toAbsolutePath();

            Path basedirPath = layersConfigPath.getParent();
            if (null != basedir) {
                basedirPath = basedir.toPath();
            }

            if (null == propertiesFile) {
                Layrry.run(layersConfigPath, basedirPath, parsedArgs);
            } else if (null != propertiesFileUrl) {
                Layrry.run(layersConfigPath, basedirPath, propertiesFileUrl, parsedArgs);
            } else {
                Layrry.run(layersConfigPath, basedirPath, Paths.get(propertiesFile).toAbsolutePath(), parsedArgs);
            }
        }
    }

    private static URL toUrl(String input) {
        try {
            return new URL(input);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
