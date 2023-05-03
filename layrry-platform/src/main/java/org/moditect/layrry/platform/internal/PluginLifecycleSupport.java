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
package org.moditect.layrry.platform.internal;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.moditect.layrry.platform.PluginDescriptor;
import org.moditect.layrry.platform.PluginLifecycleListener;

/**
 * Invoked by the launcher whenever a plugin layer gets added or removed.
 * Invokes all registered {@link PluginLifecycleListener}s in turn.
 */
public class PluginLifecycleSupport {

    public void notifyPluginListenersOnAddition(ModuleLayer moduleLayer, String pluginName, ModuleLayer pluginLayer) {
        ServiceLoader<PluginLifecycleListener> loader = ServiceLoader.load(moduleLayer, PluginLifecycleListener.class);

        PluginDescriptor plugin = new PluginDescriptor(pluginName, pluginLayer);

        Iterator<PluginLifecycleListener> listeners = loader.iterator();
        while (listeners.hasNext()) {
            PluginLifecycleListener listener = listeners.next();

            // notify each listener only through it defining layer, but not via other layers
            // derived from that
            if (listener.getClass().getModule().getLayer().equals(moduleLayer)) {
                listener.pluginAdded(plugin);
            }
        }
    }

    public void notifyPluginListenersOnRemoval(ModuleLayer moduleLayer, String pluginName, ModuleLayer pluginLayer) {
        ServiceLoader<PluginLifecycleListener> loader = ServiceLoader.load(moduleLayer, PluginLifecycleListener.class);

        PluginDescriptor plugin = new PluginDescriptor(pluginName, pluginLayer);

        Iterator<PluginLifecycleListener> listeners = loader.iterator();
        while (listeners.hasNext()) {
            PluginLifecycleListener listener = listeners.next();

            // notify each listener only through it defining layer, but not via other layers
            // derived from that
            if (listener.getClass().getModule().getLayer().equals(moduleLayer)) {
                listener.pluginRemoved(plugin);
            }
        }
    }
}
