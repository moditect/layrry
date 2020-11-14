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

import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryChangeEvent.EventType;
import io.methvin.watcher.DirectoryWatcher;
import org.moditect.layrry.Layers;
import org.moditect.layrry.LocalResolveCapture;
import org.moditect.layrry.RemoteResolveCapture;
import org.moditect.layrry.internal.jfr.PluginLayerAddedEvent;
import org.moditect.layrry.internal.jfr.PluginLayerRemovedEvent;
import org.moditect.layrry.internal.resolver.ResolveImpl;
import org.moditect.layrry.internal.util.FilesHelper;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LayersImpl implements Layers {

    /**
     * The configured components (static layers or plug-ins) by name.
     */
    private final Map<String, Component> components;

    /**
     * The actual module layers by name.
     */
    private final Map<String, ModuleLayer> moduleLayers;

    /**
     * Temporary directory where all plug-ins will be copied to. Modules will be
     * sourced from there, allowing to remove plug-ins by deleting their original
     * directory.
     */
    private final Path pluginsWorkingDir;

    /**
     * All configured directories potentially containing plug-ins.
     */
    private final Set<PluginsDirectory> pluginsDirectories;

    private final ResolveImpl resolve = new ResolveImpl();

    private int pluginIndex = 0;

    public LayersImpl(Set<PluginsDirectory> pluginsDirectories, Map<String, Component> components,
                      List<LocalResolveCapture> localResolveCaptures,
                      List<RemoteResolveCapture> remoteResolveCaptures) {
        this.components = Collections.unmodifiableMap(components);
        this.moduleLayers = new ConcurrentHashMap<>();
        this.pluginsDirectories = Collections.unmodifiableSet(pluginsDirectories);

        // apply captures
        for (LocalResolveCapture capture : localResolveCaptures) {
            ((LocalResolveCaptureImpl) capture).localRepositories().forEach(resolve.local()::withLocalRepo);
        }
        for (RemoteResolveCapture capture : remoteResolveCaptures) {
            RemoteResolveCaptureImpl remote = (RemoteResolveCaptureImpl) capture;
            resolve.remote().enabled(remote.enabled());
            resolve.remote().workOffline(remote.workOffline());
            resolve.remote().withMavenCentralRepo(remote.useMavenCentral());
            if (null != remote.configFile()) resolve.remote().fromFile(remote.configFile());
        }

        try {
            this.pluginsWorkingDir = Files.createTempDirectory("layrry-plugins");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(String main, String... args) {
        Map<String, ModuleLayer> pluginLayersByName = new HashMap<>();

        for(Entry<String, Component> entry : components.entrySet()) {
            PluginLayerAddedEvent event = new PluginLayerAddedEvent();
            event.begin();

            Component component = entry.getValue();

            List<Path> modulePathEntries = resolveModulePathEntries(component);

            List<ModuleLayer> parentLayers = getParentLayers(entry.getKey(), component.getParents());
            ModuleLayer moduleLayer = createModuleLayer(parentLayers, modulePathEntries);

            moduleLayers.put(entry.getKey(), moduleLayer);

            if (entry.getValue().isPlugin()) {
                pluginLayersByName.put(entry.getKey(), moduleLayer);
            }

            event.name = entry.getKey();
            event.modules = moduleLayer.modules().stream().map(Module::getName).collect(Collectors.joining(", "));
            event.commit();
        }

        if (!pluginsDirectories.isEmpty()) {
            Deployer deployer = new Deployer(pluginsDirectories);

            for (Entry<String, ModuleLayer> plugin : pluginLayersByName.entrySet()) {
                deployer.deploy(plugin.getKey(), plugin.getValue());
            }
        }

        try {
            Class<?> mainClass = getMainClass(main);
            Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Couldn't run module main class", e);
        }
    }

    private List<Path> resolveModulePathEntries(Component component) {
        if (component.isPlugin()) {
            Plugin plugin = (Plugin)component;
            Path pluginDir = pluginsWorkingDir.resolve(pluginIndex++ + "-" + plugin.getName());
            FilesHelper.copyFolder(plugin.getLayerDir(), pluginDir);

            return List.of(pluginDir);
        }
        else {
            return getModulePathEntries((Layer) component);
        }
    }

    private ModuleLayer createModuleLayer(List<ModuleLayer> parentLayers, List<Path> modulePathEntries) {
        ClassLoader scl = ClassLoader.getSystemClassLoader();

        ModuleFinder finder = ModuleFinder.of(modulePathEntries.toArray(Path[]::new));

        Set<String> roots = finder.findAll()
            .stream()
            .map(m -> m.descriptor().name())
            .collect(Collectors.toSet());

        Configuration appConfig = Configuration.resolve(
                finder,
                parentLayers.stream().map(ModuleLayer::configuration).collect(Collectors.toList()),
                ModuleFinder.of(),
                roots
        );

        ModuleLayer moduleLayer = ModuleLayer.defineModulesWithOneLoader(appConfig, parentLayers, scl).layer();
        return moduleLayer;
    }

    private List<Path> getModulePathEntries(Layer layer) {
        List<String> moduleGavs = layer.getModuleGavs();
        return Arrays.asList(resolve.resolve(moduleGavs).asPath());
    }

    private Class<?> getMainClass(String main) throws ClassNotFoundException {
        String[] parts = main.split("/");
        for(Entry<String, ModuleLayer> entry : moduleLayers.entrySet()) {
            try {
                ClassLoader loader = entry.getValue().findLoader(parts[0]);
                return loader.loadClass(parts[1]);
            }
            catch(IllegalArgumentException iae) {
                // IGNORE
            }
        }

        throw new IllegalArgumentException("Module " + parts[0] + " not found");
    }

    private List<ModuleLayer> getParentLayers(String name, List<String> parents) {
        List<ModuleLayer> parentLayers = new ArrayList<>();

        for (String parent : parents) {
            ModuleLayer parentLayer = moduleLayers.get(parent);
            if (parentLayer == null) {
                throw new IllegalArgumentException("Layer '" + name  + "': parent layer '" + parent + "' not configured yet");
            }

            parentLayers.add(parentLayer);
        }

        return parentLayers.isEmpty() ? List.of(ModuleLayer.boot()) : parentLayers;
    }

    private class Deployer {

        private final Method notifyOnAddition;
        private final Method notifyOnRemoval;
        private final Object supportInstance;

        public Deployer(Set<PluginsDirectory> pluginsDirectories) {
            try {
                ModuleLayer platformLayer = getLayrryPlatformLayer(moduleLayers);
                ClassLoader loader = platformLayer.findLoader("org.moditect.layrry.platform");
                Class<?> support = loader.loadClass("org.moditect.layrry.platform.internal.PluginLifecycleSupport");
                supportInstance = support.getConstructor().newInstance();
                notifyOnAddition = support.getDeclaredMethod("notifyPluginListenersOnAddition", ModuleLayer.class, String.class, ModuleLayer.class);
                notifyOnRemoval = support.getDeclaredMethod("notifyPluginListenersOnRemoval", ModuleLayer.class, String.class, ModuleLayer.class);
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }

            ExecutorService executor = Executors.newFixedThreadPool(pluginsDirectories.size());

            for (PluginsDirectory pluginDirectory : pluginsDirectories) {
                executor.execute(() -> {
                    try {
                        DirectoryWatcher watcher = DirectoryWatcher.builder()
                                .path(pluginDirectory.getDirectory())
                                .listener(event -> onDirectoryChange(event, pluginDirectory))
                                .build();

                        watcher.watch();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.out.println("Executor did not terminate in the specified time.");
                    }
                }
                catch (InterruptedException e) {
                    // IGNORE
                }
            }));
        }

        private void onDirectoryChange(DirectoryChangeEvent event, PluginsDirectory pluginDirectory) {

            // only interested in direct sub-directories atm.
            if (event.path().getNameCount() > pluginDirectory.getDirectory().getNameCount() + 1) {
                return;
            }

            Path pluginSourceDir = event.path();
            String derivedFrom = pluginDirectory.getName();
            String pluginName = derivedFrom + "-" + pluginSourceDir.getFileName().toString();

            if (event.eventType() == EventType.CREATE) {
                PluginLayerAddedEvent jfrEvent = new PluginLayerAddedEvent();
                jfrEvent.begin();

                Path pluginDir = pluginsWorkingDir.resolve(pluginIndex++ + "-" + pluginName);
                FilesHelper.copyFolder(pluginSourceDir, pluginDir);
                List<Path> modulePathEntries = List.of(pluginDir);
                List<ModuleLayer> parentLayers = getParentLayers(pluginName, pluginDirectory.getParents());

                ModuleLayer moduleLayer = createModuleLayer(parentLayers, modulePathEntries);

                moduleLayers.put(pluginName, moduleLayer);
                deploy(pluginName, moduleLayer);

                jfrEvent.name = pluginName;
                jfrEvent.modules = moduleLayer.modules().stream().map(Module::getName).collect(Collectors.joining(", "));
                jfrEvent.commit();
            }
            else if (event.eventType() == EventType.DELETE) {
                PluginLayerRemovedEvent jfrEvent = new PluginLayerRemovedEvent();
                jfrEvent.begin();

                ModuleLayer pluginLayer = moduleLayers.get(pluginName);
                undeploy(pluginName, pluginLayer);
                moduleLayers.remove(pluginName);

                jfrEvent.name = pluginName;
                jfrEvent.modules = pluginLayer.modules().stream().map(Module::getName).collect(Collectors.joining(", "));
                jfrEvent.commit();
            }
        }

        public void deploy(String pluginName, ModuleLayer pluginLayer) {
            // for each existing layer, notify any potential lifecycle listeners about the new layer
            for (ModuleLayer moduleLayer : moduleLayers.values()) {
                try {
                    notifyOnAddition.invoke(supportInstance, moduleLayer, pluginName, pluginLayer);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        public void undeploy(String pluginName, ModuleLayer pluginLayer) {
            // for each existing layer, notify any potential lifecycle listeners about the new layer
            for (ModuleLayer moduleLayer : moduleLayers.values()) {
                try {
                    notifyOnRemoval.invoke(supportInstance, moduleLayer, pluginName, pluginLayer);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        private ModuleLayer getLayrryPlatformLayer(Map<String, ModuleLayer> moduleLayers) {
            for (Entry<String, ModuleLayer> layer : moduleLayers.entrySet()) {
                Optional<Module> platformModule = layer.getValue()
                    .modules()
                    .stream()
                    .filter(m -> m.getName().equals("org.moditect.layrry.platform"))
                    .findFirst();

                if (platformModule.isPresent()) {
                    return layer.getValue();
                }
            }

            throw new IllegalArgumentException("Layrry Platform module not found");
        }
    }
}
