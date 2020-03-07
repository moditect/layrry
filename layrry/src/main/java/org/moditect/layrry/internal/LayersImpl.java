package org.moditect.layrry.internal;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.moditect.layrry.Layers;

public class LayersImpl implements Layers {

    private final Map<String, Layer> layers;
    private final Map<String, ModuleLayer> moduleLayers;

    public LayersImpl(Map<String, Layer> layers) {
        this.layers = Collections.unmodifiableMap(layers);
        this.moduleLayers = new HashMap<>();
    }

    @Override
    public void run(String main, String... args) {
        ClassLoader scl = ClassLoader.getSystemClassLoader();

        MavenResolverSystem resolver = Maven.resolver();
        for(Entry<String, Layer> entry : layers.entrySet()) {
            Path[] moduleJars = resolver.resolve(entry.getValue().getModuleGavs()).withoutTransitivity().as(Path.class);
            ModuleFinder finder = ModuleFinder.of(moduleJars);
            List<ModuleLayer> parentLayers = getParentLayers(entry.getKey(), entry.getValue().getParents());
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

            ModuleLayer layer = ModuleLayer.defineModulesWithOneLoader(appConfig, parentLayers, scl).layer();
            moduleLayers.put(entry.getKey(), layer);
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

    private Class<?> getMainClass(String main) throws ClassNotFoundException {
        String[] parts = main.split("\\/");
        for(Entry<String, ModuleLayer> entry : moduleLayers.entrySet()) {
            try {
                ClassLoader loader = entry.getValue().findLoader(parts[0]);
                return loader.loadClass(parts[1]);
            }
            catch(IllegalArgumentException iae) {
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

        return parentLayers.isEmpty() ? Collections.singletonList(ModuleLayer.boot()) : parentLayers;
    }

}
