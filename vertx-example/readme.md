Layrry Vert.x Example
---

This app demonstrates the usage of Layrry with dynamic modules in Vert.x. 

# Build
To build the example app, use the following command:
```
mvn clean package
```

# Run
To run the example app, use the following command:
```
java --enable-preview -jar ../layrry-launcher/target/layrry-launcher-*-jar-with-dependencies.jar --layers-config layrry-links-runner/src/test/resources/layers.yml
```

# Dynamically manage modules

To dynamically add the `layrry-links-tournament` module, run the following:
```
tar -xvf layrry-links-tournament/target/layrry-links-tournament-*.tar.gz -C layrry-links-runner/target/route-plugins
```

To dynamically remove the `layrry-links-tournament` module, run the following:
```
rm -R layrry-links-runner/target/route-plugins/layrry-links-tournament-*
```