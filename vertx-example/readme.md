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
java --enable-preview -jar ../layrry-launcher/target/layrry-launcher-*-all.jar --layers-config layrry-links-runner/src/test/resources/layers.yml
```

Example output
```
23:10:50.284 [main] INFO  c.e.l.l.c.i.LayrryLinksVerticle - Adding plug-in: PluginDescriptor [name=plugins-layrry-links-membership-1.0.0, moduleLayer=com.example.layrry.links.membership]
23:10:50.417 [vert.x-eventloop-thread-0] INFO  c.e.l.l.c.i.LayrryLinksVerticle - Adding router for path: /routes
23:10:50.422 [vert.x-eventloop-thread-0] INFO  c.e.l.l.c.i.LayrryLinksVerticle - Adding router for path: /members
23:10:50.470 [vert.x-eventloop-thread-0] INFO  c.e.l.l.c.i.LayrryLinksVerticle - Server ready! Browse to http://localhost:8080/routes
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