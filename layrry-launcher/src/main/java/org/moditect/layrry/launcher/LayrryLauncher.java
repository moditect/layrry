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

/**
 * The main entry point for using Layrry on the command line. Expects the layers config file to be passed in:
 * <p>
 * <code>
 * LayrryLauncher --layers-config &lt;path/to/layrry.yml&gt;
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

        File layersConfigFile = arguments.getLayersConfig().getAbsoluteFile();
        String[] parsedArgs = arguments.getMainArgs().toArray(new String[0]);

        Layrry.run(layersConfigFile.toPath(), parsedArgs);
    }
}
