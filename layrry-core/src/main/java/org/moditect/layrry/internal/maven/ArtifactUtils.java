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
package org.moditect.layrry.internal.maven;

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ArtifactUtils {
    private static final Pattern ARTIFACT_PATTERN = Pattern.compile("(.*?)\\-(\\d[\\d+\\.]*?)\\.jar");
    private static final Pattern ARTIFACT_PATTERN2 = Pattern.compile("(.*?)\\-(\\d[\\d+\\-_A-Za-z\\.]*?)\\.jar");

    static LocalMavenResolvedArtifact fromPaths(Path basedir, Path file) {
        String version = file.getParent().toFile().getName();
        String artifactId = file.getParent().getParent().toFile().getName();

        List<String> parts = new ArrayList<>();
        Path path = file.getParent().getParent().getParent();
        while (!path.getFileName().equals(basedir.getFileName())) {
            parts.add(path.getFileName().toFile().getName());
            path = path.getParent();
        }
        Collections.reverse(parts);

        String groupId = String.join(".", parts);

        String classifier = null;
        Pattern pattern = Pattern.compile(artifactId + "\\-" + version + "\\-(.*?).jar");
        Matcher matcher = pattern.matcher(file.getFileName().toFile().getName());

        if (matcher.matches()) {
            classifier = matcher.group(1);
        }

        MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate(groupId, artifactId, version, PackagingType.JAR, classifier);
        return new LocalMavenResolvedArtifactImpl(mavenCoordinate, file.toFile());
    }

    static LocalMavenResolvedArtifact fromFile(File file) {
        Matcher matcher = ARTIFACT_PATTERN.matcher(file.getName());
        if (matcher.matches()) {
            String artifactId = matcher.group(1);
            String version = matcher.group(2);

            MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate("*", artifactId, version, PackagingType.JAR, null);
            return new LocalMavenResolvedArtifactImpl(mavenCoordinate, file);
        }

        matcher = ARTIFACT_PATTERN2.matcher(file.getName());
        if (matcher.matches()) {
            String artifactId = matcher.group(1);
            // may contain classifier
            String version = matcher.group(2);

            // try to read /META-INF/maven/${groupId}/pom.properties
            // and match props.artifactId and props.version with file.name
            LocalMavenResolvedArtifact artifact = resolveFromPomProperties(file, artifactId, version);
            if (artifact != null) {
                return artifact;
            }

            // can't tell if version has classifier or not
            MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate("*", artifactId, version, PackagingType.JAR, null);
            return new LocalMavenResolvedArtifactImpl(mavenCoordinate, file);
        }

        return null;
    }

    private static LocalMavenResolvedArtifact resolveFromPomProperties(File file, String artifactId, String version) {
        try (JarFile jarFile = new JarFile(file)) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                if (entry.getName().endsWith("pom.properties")) {
                    Properties props = new Properties();
                    props.load(jarFile.getInputStream(entry));

                    if (!artifactId.equals(props.getProperty("artifactId"))) {
                        continue;
                    }

                    String v = props.getProperty("version");
                    if (!version.startsWith(v)) {
                        continue;
                    }

                    // found it!
                    String classifier = null;
                    if (!v.equals(version)) {
                        classifier = version.substring(v.length());
                        if (classifier.startsWith("-")) {
                            classifier = classifier.substring(1);
                        }
                    }

                    MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate(props.getProperty("groupId"), artifactId, v, PackagingType.JAR, classifier);
                    return new LocalMavenResolvedArtifactImpl(mavenCoordinate, file);
                }
            }
        } catch (IOException ignored) {
            // noop
        }
        return null;
    }

    static boolean coordinatesMatch(MavenCoordinate a, MavenCoordinate b) {
        if (a.equals(b) && a.getVersion().equals(b.getVersion())) return true;
        if ("*".equals(a.getGroupId()) || "*".equals(b.getGroupId())) {
            MavenCoordinate a1 = MavenCoordinates.createCoordinate("*", a.getArtifactId(), a.getVersion(), a.getPackaging(), a.getClassifier());
            MavenCoordinate b1 = MavenCoordinates.createCoordinate("*", b.getArtifactId(), b.getVersion(), b.getPackaging(), b.getClassifier());
            return a1.equals(b1) && a1.getVersion().equals(b1.getVersion());
        }

        return false;
    }
}
