# Layrry ⁠- A Launcher and API for Modularized Java Applications

Layrry is a launcher and Java API for executing modularized Java applications.

It allows to assemble modularized applications based on Maven artifact coordinates of the (modular) JARs to include.
Layrry utilizes the Java Module System's notion of [module layers](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ModuleLayer.html), allowing multiple versions of one module to be used within an application at the same time.

The module graph is built either declaratively (using YAML descriptors) or programmatically (using a fluent API).

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

## Using the Layrry Launcher

The Layrry Launcher is a CLI tool which takes a configuration of a layered application and executes it.
It's used like so:

```
layrry-1.0-SNAPSHOT-jar-with-dependencies.jar --layers-config <path/to/layers.yml> [program arguments]
```

E.g. like so:

```
layrry-1.0-SNAPSHOT-jar-with-dependencies.jar --layers-config hello-world.yml Alice Bob
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
main:
  module: <main module>
  class: <main class>
```

As an example, consider the following application whose modules `foo` and `bar` depend on two different versions of the `greeter` module:

![Layrry Example](example.png)

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

You can find the complete example in the tests of the Layrry project.

## Using the Layrry API

In addition to the YAML-based launcher, Layrry provides also a Java API for assembling and running layered applications.
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
Layers layers = Layers.layer("log")
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
mvn clean install
```

Java 11 or later is needed in order to do so.

## Contributing

Your contributions to Layrry are very welcomed.
Please open issues with your feature suggestions as well as pull requests.
Before working on larger pull requests,
it's suggested to reach out to [@gunnarmorling](https://twitter.com/gunnarmorling).

## License

Layrry is licensed under the Apache License version 2.0.
