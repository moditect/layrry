Layrry Plugin-Example
---

This app demonstrates the usage of Layrry with multiple extension modules. 

# Build
To build the example app, use the following command:
```
mvn clean package
```

# Run
To run the example app, use the following command:

With **YAML** configuration
```
java -jar ../layrry-launcher/target/layrry-launcher-1.0-SNAPSHOT-jar-with-dependencies.jar --layers-config greeter-runner/src/test/resources/layers.yml
```

With **TOML** configuration
```
java -jar ../layrry-launcher/target/layrry-launcher-1.0-SNAPSHOT-jar-with-dependencies.jar --layers-config greeter-runner/src/test/resources/layers.toml
```