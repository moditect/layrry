package org.moditect.layrry.internal.descriptor;

public class Main {
    private String module;
    private String clazz;

    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }
    public String getClazz() {
        return clazz;
    }
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
    @Override
    public String toString() {
        return "Main [module=" + module + ", clazz=" + clazz + "]";
    }
}
