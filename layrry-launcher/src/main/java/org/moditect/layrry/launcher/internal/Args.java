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
package org.moditect.layrry.launcher.internal;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Args {

    @Parameter(description = "Main arguments")
    private List<String> mainArgs = new ArrayList<>();

    @Parameter(names = "--layers-config", required = true, description = "Layers configuration file")
    private String layersConfig;

    @Parameter(names = "--properties", description = "Additional config properties")
    private String properties;

    @Parameter(names = "--basedir", description = "Base directory for resolving relative paths")
    private File basedir;

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

    @Override
    public String toString() {
        return "Args [mainArgs=" + mainArgs + ", layersConfig=" + layersConfig + ", properties=" + properties + ", basedir=" + basedir + "]";
    }
}
