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
package org.moditect.layrry.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(description = "Main arguments")
    private List<String> mainArgs = new ArrayList<>();

    @Parameter(names = "--layers-config", required = true, description = "Layers configuration file")
    private File layersConfig;

    public List<String> getMainArgs() {
        return mainArgs;
    }

    public File getLayersConfig() {
        return layersConfig;
    }
}
