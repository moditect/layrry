# Layrry - A Launcher and API for Modularized Java Applications

Layrry is a launcher and Java API for executing modularized Java applications.

It allows to assemble modularized applications based on Maven artifact coordinates of the (modular) JARs to include.
Layrry utilizes the Java Module System's notion of [module layers](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ModuleLayer.html), allowing multiple versions of one module to be used within an application at the same time, as well as dynamically adding and removing modules at application runtime.

The module graph is built either declaratively (using YAML descriptors) or programmatically (using a fluent API).

Learn more about Layrry in this series of blog posts:

* [Part 1: "Introducing Layrry: A Launcher and API for Modularized Java Applications"](https://www.morling.dev/blog/introducing-layrry-runner-and-api-for-modularized-java-applications/)
* [Part 2: "Plug-in Architectures With Layrry and the Java Module System"](https://www.morling.dev/blog/plugin-architectures-with-layrry-and-the-java-module-system/)
* Part 3: Dynamically loading Java modules at application runtime with Layrry (coming soon)

## Why Layrry?

The Java Module System doesn't define any means of mapping between modules (e.g. _com.acme.crm_) and JARs providing such module
(e.g. _acme-crm-1.0.0.Final.jar_) or retrieving modules from remote repositories using unique identifiers (e.g. _com.acme:acme-crm:1.0.0.Final_).
Instead, it's the responsibility of the user to obtain all required JARs of a modularized application and provide them via `--module-path`.

Furthermore, the module system doesn't define any means of module versioning;
i.e. it's the responsibility of the user to obtain all modules in the right version.
Using the `--module-path` option, it's not possible, though, to assemble an application that uses multiple versions of one and the same module.
This may be desirable for transitive dependencies of an application,
which might be required in different versions by two separate direct dependencies.

This is where Layrry comes in:
utilizing the notion of module layers,
it provides a declarative approach as well as an API for assembling modularized applications, organized in module layers.
The JARs to be included are described using Maven GAV (group id, artifact id, version) coordinates,
solving the issue of retrieving all required JARs in the right version.

Module layers allow to use different versions of one and the same module in different layers of an application
(as long as they are not exposed in a conflicting way on module API boundaries).

Module layers and thus Layrry also allow application extensions to be added and removed dynamically at runtime.
Here's [an example](https://github.com/moditect/layrry-examples/tree/master/modular-tiles) for using the Layrry plug-in API to dynamically modify a JavaFX application:

![JavaFX app based on Layrry](images/javafx-layrry.gif)

## Using the Layrry Launcher

The Layrry Launcher is a CLI tool which takes a configuration of a layered application and executes it.
It's used like so:

```
layrry-launcher-1.0-SNAPSHOT.jar --layers-config <path/to/layers.yml> [program arguments]
```

E.g. like so:

```
layrry-launcher-1.0-SNAPSHOT.jar --layers-config hello-world.yml Alice Bob
```

The application layers configuration file is a YAML file which the following structure:

```yaml
layers:
  <name 1>:
    modules:
      - "G:A:V"
      - "G:A:V"
      - ...
  <name 2>:
    parents:
      - "<name 1>"
    modules:
      - ...
      - ...
  <name 3>:
    parents:
      - "<name 2"
    directory: "relative/path/to/directory/of/layer/directories

main:
  module: <main module>
  class: <main class>
```

Each layer comprises:

* A unique name
* The list of parent layers
* The list of contained modules given via Maven GAV coordinates OR
* A directory which contains one or more sub-directories, each of which represent one layer made up of the modular JARs 
within that sub-directory; the directory path is resolved relatively to the location of the _layrry.yml_ file. Alternatively
the directory may be an absolute path however **be very careful** as this may cause a non portable configuration.

As an example, consider the following application whose modules `foo` and `bar` depend on two different versions of the `greeter` module:

![Layrry Example](images/example.png)

Running this application wouldn't be possible with the default module path,
which only allows for one version of a given module.
Here is how the application can be executed via Layrry,
organizing all the modules in multiple layers:

```yaml
layers:
  log:
    modules:
      - "org.apache.logging.log4j:log4j-api:jar:2.13.1"
      - "org.apache.logging.log4j:log4j-core:jar:2.13.1"
      - "com.example:logconfig:1.0.0"
  foo:
    parents:
      - "log"
    modules:
      - "com.example:greeter:1.0.0"
      - "com.example:foo:1.0.0"
  bar:
    parents:
      - "log"
    modules:
      - "com.example:greeter:2.0.0"
      - "com.example:bar:1.0.0"
  app:
    parents:
      - "foo"
      - "bar"
    modules:
      - "com.example:app:1.0.0"
main:
  module: com.example.app
  class: com.example.app.App
```

Alternatively you may use TOML instead of YAML

```toml
[layers.log]
  modules = [
    "org.apache.logging.log4j:log4j-api:jar:2.13.1",
    "org.apache.logging.log4j:log4j-core:jar:2.13.1",
    "com.example.it:it-logconfig:1.0.0"]
[layers.foo]
  parents = ["log"]
  modules = [
    "com.example.it:it-greeter:1.0.0",
    "com.example.it:it-foo:1.0.0"]
[layers.bar]
  parents = ["log"]
  modules = [
    "com.example.it:it-greeter:2.0.0",
    "com.example.it:it-bar:1.0.0"]
[layers.app]
  parents = ["foo", "bar"]
  modules = ["com.example.it:it-app:1.0.0"]
[main]
  module = "com.example.app"
  class = "com.example.app.App"
```

Be sure to use `.toml` as file extension to let Layrry know which format should be parsed.

You can find the complete example in the tests of the Layrry project.

The Layrry Launcher accepts the following arguments:

 * --layers-config: Path to the layers config file. The file must use any of the supported config formats. REQUIRED.
 * --properties: Path to additional properties in Java `.properties` format. These properties will be used to replace value
 placeholders found in the layers config file. OPTIONAL.

## Using jbang

[jbang](https://github.com/jbangdev/jbang) can launch self contained Java sources, JShell scripts, JARs. jbang has a feature
that allows you to tey out Layrry withlut having to install or build Layrry yourself. You only need a JDK (11+ is preferred)
and jbang installed. Once you do, you may invoke the previous example with

```sh
jbang layrry@moditect --layers-config layers.yml
```

jbang will resolve and download the appropriate Layrry bootstrap binary, then Layrry resolves the modules described in
the input configuration file, finally the application is launched.

## Dynamic Plug-Ins

Layrry also supports the dynamic addition and removal of plug-ins at runtime.
For that, simply add or remove plug-in sub-directories to the `directory` of a layer configuration.
Layrry watches the given plug-ins directory and will add or remove the corresponding module layer to/from the application in case a new plug-in is added or removed.
The core of an application can react to added or removed module layers.
In order to do so, the module _org.moditect.layrry:layrry-platform_  must be added to the application core layer and an implementation of the `PluginLifecycleListener` interface must be created and registered as service:

```java
public interface PluginLifecycleListener {

    void pluginAdded(PluginDescriptor plugin);

    void pluginRemoved(PluginDescriptor plugin);
}
```

Typically, an application will retrieve application-specific services from newly added module layers:

```java
@Override
public void pluginAdded(PluginDescriptor plugin) {
  ServiceLoader<MyService> services = ServiceLoader.load(
      plugin.getModuleLayer(), MyService.class);

    services.forEach(service -> {
      // only process services declared by the added layer itself, but not
      // from ancestor layers
      if (service.getClass().getModule().getLayer() == layer) {
        // process service ...
      }
    });
}
```

To avoid class-loader leaks, it's vital that all references to plug-in contribued classes are released upon `pluginRemoved()`.
Note that classes typically will not instantly be unloaded, but only upon the next full GC (when using G1).

You can find a complete example for the usage of dynamic plug-ins in the _vertx-example_ directory:
"Layrry Links" is an example application for managing golf courses, centered around a web application core built using Vert.x.
Routes of the web application (_/members_, _/tournaments_) are contributed by plug-ins which can be added to or removed from the application at runtime.
The _routes_ path shows all routes available at a given time.

## Parameterized Layer Configuration

Layrry supports the [Mustache](https://github.com/spullara/mustache.java) template syntax, enabling parameterization of
the content found in configuration files, regardless of their target format (YAML, TOML, etc). To use this feature you must
use a `{{property}}` expression to refer to value placeholders. Layrry makes all `System` properties available for value
replacement, as well as an extra set of properties that are related to OS values; these include all properties exposed by
the [os-maven-plugin](https://github.com/trustin/os-maven-plugin/) plus `javax.os.classifier` which is specific to JavaFX
dependencies. This last property can have the following values: `linux`, `win`, `mac`. If the `--properties` command flag
is passed to the Layrry Launcher then all properties found in the given properties file will also become available.

The following example shows a parameterized TOML config file for a JavaFX application that can be run on any of the 3 platforms
supported by JavaFX

**layers.toml**
```toml
[layers.javafx]
    modules = [
        "org.openjfx:javafx-base:jar:{{javafx.os.classifier}}:{{javafx_version}}",
        "org.openjfx:javafx-controls:jar:{{javafx.os.classifier}}:{{javafx_version}}",
        "org.openjfx:javafx-graphics:jar:{{javafx.os.classifier}}:{{javafx_version}}",
        "org.openjfx:javafx-web:jar:{{javafx.os.classifier}}:{{javafx_version}}",
        "org.openjfx:javafx-media:jar:{{javafx.os.classifier}}:{{javafx_version}}"]
[layers.core]
    modules = [
        "org.kordamp.tiles:modular-tiles-model:{{project_version}}",
        "org.kordamp.tiles:modular-tiles-core:{{project_version}}",
        "org.kordamp.tiles:modular-tiles-app:{{project_version}}",
        "org.moditect.layrry:layrry-platform:{{layrry_version}}",
        "eu.hansolo:tilesfx:{{tilesfx_version}}"]
    parents = ["javafx"]
[layers.plugins]
    parents = ["core"]
    directory = "plugins"
[main]
  module = "org.kordamp.tiles.app"
  class = "org.kordamp.tiles.app.Main"
```

**versions.properties**
```properties
project_version = 1.0.0
javafx_version = 11.0.2
tilesfx_version = 11.44
layrry_version = 1.0-SNAPSHOT
```

This application can be launched as

```
layrry-launcher-1.0-SNAPSHOT.jar --layers-config layers.toml --properties versions.properties
```

## Using the Layrry API

In addition to the YAML-based/TOML-based launcher, Layrry provides also a Java API for assembling and running layered applications.
This can be used in cases where the structure of layers is only known at runtime,
or for implementing plug-in architectures.

In order to use Layrry programmatically, add the following dependency to your _pom.xml_:

```xml
<dependency>
    <groupId>org.moditect.layrry</groupId>
    <artifactId>layrry</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Then, the Layrry Java API can be used like this (showing the same example as above):

```java
Layers layers = Layers.builder()
    .layer("log")
        .withModule("org.apache.logging.log4j:log4j-api:jar:2.13.1")
        .withModule("org.apache.logging.log4j:log4j-core:jar:2.13.1")
        .withModule("com.example:logconfig:1.0.0")
    .layer("foo")
        .withParent("log")
        .withModule("com.example:greeter:1.0.0")
        .withModule("com.example:foo:1.0.0")
    .layer("bar")
        .withParent("log")
        .withModule("com.example:greeter:2.0.0")
        .withModule("com.example:bar:1.0.0")
    .layer("app")
        .withParent("foo")
        .withParent("bar")
        .withModule("com.example:app:1.0.0")
    .build();

layers.run("com.example.app/com.example.app.App", "Alice");
```

## Building Layrry

Layrry is not available on Maven Central yet.
In order to use it, build it from source like so:

```
mvn install
```

Java 15 or later is needed in order to do so.

## Contributing

Your contributions to Layrry are very welcomed.
Please open issues with your feature suggestions as well as pull requests.
Before working on larger pull requests,
it's suggested to reach out to [@gunnarmorling](https://twitter.com/gunnarmorling).

## License

Layrry is licensed under the Apache License version 2.0.
