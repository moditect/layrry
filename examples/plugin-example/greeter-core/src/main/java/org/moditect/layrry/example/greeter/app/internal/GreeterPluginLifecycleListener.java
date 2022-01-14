/*
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
package org.moditect.layrry.example.greeter.app.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.moditect.layrry.platform.PluginDescriptor;
import org.moditect.layrry.platform.PluginLifecycleListener;

public class GreeterPluginLifecycleListener implements PluginLifecycleListener {

    private static Map<String, ModuleLayer> moduleLayers = new ConcurrentHashMap<>();

    @Override
    public void pluginAdded(PluginDescriptor plugin) {
        moduleLayers.put(plugin.getName(), plugin.getModuleLayer());
    }

    @Override
    public void pluginRemoved(PluginDescriptor plugin) {
        moduleLayers.remove(plugin.getName());
    }

    public static Map<String, ModuleLayer> getModuleLayers() {
        return moduleLayers;
    }
}
