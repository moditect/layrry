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
package org.moditect.layrry.config;

import java.util.Map;

public class LayersConfig {

    private Map<String, Layer> layers;
    private Main main;
    private Resolve resolve;

    public Map<String, Layer> getLayers() {
        return layers;
    }

    public void setLayers(Map<String, Layer> layers) {
        this.layers = layers;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Resolve getResolve() {
        return resolve;
    }

    public void setResolve(Resolve resolve) {
        this.resolve = resolve;
    }

    @Override
    public String toString() {
        return "LayersConfig [layers=" + layers + ", main=" + main + ", resolve=" + resolve + "]";
    }
}
