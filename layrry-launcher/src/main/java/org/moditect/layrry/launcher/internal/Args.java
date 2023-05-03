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
package org.moditect.layrry.launcher.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(description = "[arguments]")
    private List<String> mainArgs = new ArrayList<>();

    @Parameter(names = "--layers-config", description = "Layers configuration file. May be a local file or remote URL. " +
            "Examples: path/to/layers.yml, https://server/path/to/layers.toml")
    private String layersConfig;

    @Parameter(names = "--properties", description = "Additional configuration properties. May be a local file or remote URL. " +
            "Must use the Java properties format. Examples: path/to/versions.properties, https://server/path/to/versions.properties")
    private String properties;

    @Parameter(names = "--basedir", description = "Base directory for resolving relative paths")
    private File basedir;

    @Parameter(names = "--help", description = "Show usage and quit", help = true)
    private boolean help;

    @Parameter(names = "--version", description = "Show version and quit", help = true)
    private boolean version;

    public List<String> getMainArgs() {
        return mainArgs;
    }

    public String getLayersConfig() {
        return layersConfig;
    }

    public String getProperties() {
        return properties;
    }

    public File getBasedir() {
        return basedir;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Args [mainArgs=" + mainArgs + ", layersConfig=" + layersConfig + ", properties=" + properties + ", basedir=" + basedir + ", help=" + help + ", version="
                + version + "]";
    }
}
