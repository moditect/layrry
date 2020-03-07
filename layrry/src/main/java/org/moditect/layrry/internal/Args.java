package org.moditect.layrry.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(description = "Main arguments")
    private List<String> mainArgs = new ArrayList<>();

    @Parameter(names = "--layers-config", description = "Layers configuration file")
    private File layersConfig;

    public List<String> getMainArgs() {
        return mainArgs;
    }

    public File getLayersConfig() {
        return layersConfig;
    }
}
