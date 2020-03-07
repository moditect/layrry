package org.moditect.layrry;

import java.util.Map.Entry;

import org.moditect.layrry.internal.Args;
import org.moditect.layrry.internal.descriptor.Layer;
import org.moditect.layrry.internal.descriptor.LayersConfig;
import org.moditect.layrry.internal.descriptor.LayersConfigParser;

import com.beust.jcommander.JCommander;

public class Layrry {

    public static void main(String[] args) throws Exception {
        Args arguments= new Args();

        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(args);

        if (!arguments.getLayersConfig().exists()) {
            throw new IllegalArgumentException("Specified layers config file doesn't exist: " + arguments.getLayersConfig());
        }

        LayersConfig layersConfig = LayersConfigParser.parseLayersConfig(arguments.getLayersConfig().toPath());

        LayerBuilder builder = null;
        for(Entry<String, Layer> layer : layersConfig.getLayers().entrySet()) {
            if (builder == null) {
                builder = Layers.layer(layer.getKey());
            }
            else {
                builder = builder.layer(layer.getKey());
            }

            for (String module : layer.getValue().getModules()) {
                builder.withModule(module);
            }

            for (String parent : layer.getValue().getParents()) {
                builder.withParent(parent);
            }
        }

        Layers layers = builder.build();

//        Layers layers = Layers.layer("log")
//                .withModule("org.slf4j:slf4j-api:1.7.30")
//                .withModule("org.slf4j:slf4j-simple:1.7.30")
//            .layer("foo")
//                .withParent("log")
//                .withModule("com.example:greeter:1.0.0")
//                .withModule("com.example:foo:1.0-SNAPSHOT")
//            .layer("bar")
//                .withModule("com.example:greeter:2.0.0")
//                .withModule("com.example:bar:1.0-SNAPSHOT")
//            .layer("app")
//                .withParent("foo")
//                .withParent("bar")
//                .withModule("com.example:app:1.0-SNAPSHOT")
//            .build();

        layers.run(layersConfig.getMain().getModule() + "/" + layersConfig.getMain().getClazz(), arguments.getMainArgs().toArray(new String[0]));
    }

}

//        ModuleLayer boot = ModuleLayer.boot();
//        ClassLoader scl = ClassLoader.getSystemClassLoader();
//
//        File foo = Maven.resolver().resolve("dev.morling.demos.moduleversions:module-versions-foo:1.0-SNAPSHOT").withoutTransitivity().asSingleFile();
//        File greeter10 = Maven.resolver().resolve("dev.morling.demos.moduleversions:module-versions-greeter:1.0.0").withoutTransitivity().asSingleFile();
//
//        ModuleFinder fooFinder = ModuleFinder.of(foo.toPath(), greeter10.toPath());
//        Configuration fooConfig = boot.configuration().resolve(fooFinder, ModuleFinder.of(),
//                Set.of("dev.morling.demos.moduleversions.foo", "dev.morling.demos.moduleversions.greeter"));
//        ModuleLayer fooLayer = boot.defineModulesWithOneLoader(fooConfig, scl);
//
//        File bar = Maven.resolver().resolve("dev.morling.demos.moduleversions:module-versions-bar:1.0-SNAPSHOT").withoutTransitivity().asSingleFile();
//        File greeter20 = Maven.resolver().resolve("dev.morling.demos.moduleversions:module-versions-greeter:2.0.0").withoutTransitivity().asSingleFile();
//
//        ModuleFinder barFinder = ModuleFinder.of(bar.toPath(), greeter20.toPath());
//        Configuration barConfig = boot.configuration().resolve(barFinder, ModuleFinder.of(),
//                Set.of("dev.morling.demos.moduleversions.bar", "dev.morling.demos.moduleversions.greeter"));
//        ModuleLayer barLayer = boot.defineModulesWithOneLoader(barConfig, scl);
//
//        File app = Maven.resolver().resolve("dev.morling.demos.moduleversions:module-versions-app:1.0-SNAPSHOT").withoutTransitivity().asSingleFile();
//        ModuleFinder appFinder = ModuleFinder.of(app.toPath());
//        Configuration appConfig = Configuration.resolve(appFinder, List.of(fooLayer.configuration(), barLayer.configuration()), ModuleFinder.of(),
//                Set.of("dev.morling.demos.moduleversions.app"));
//
//        ModuleLayer appLayer = ModuleLayer.defineModulesWithOneLoader(appConfig, List.of(fooLayer, barLayer), scl).layer();
//
//        Class<?> c = appLayer.findLoader("dev.morling.demos.moduleversions.app").loadClass("dev.morling.moduleversions.app.App");
//        Method main = c.getDeclaredMethod("main", String[].class);
//        main.invoke(null, (Object) args);
//    }

