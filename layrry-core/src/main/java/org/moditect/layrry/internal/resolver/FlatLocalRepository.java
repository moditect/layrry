/*
 *  Copyright 2020 - 2023 The ModiTect authors
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
package org.moditect.layrry.internal.resolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;

/**
 * Organizes artifacts in a single directory.
 *
 * <pre>
 *  repodir
 *   |-- foo-1.0.0.jar
 *   \-- bar-2.0.0.jar
 * </pre>
 */
public class FlatLocalRepository implements LocalRepository {
    private String id;
    private Path path;

    FlatLocalRepository(String id, String path) {
        this(id, Paths.get(path));
    }

    FlatLocalRepository(String id, Path path) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }

        this.id = id;
        this.path = path;
    }

    @Override
    public String toString() {
        return new StringBuilder("FlatLocalRepository[id=")
                .append(id)
                .append(", path=")
                .append(path)
                .append("]")
                .toString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return "flat";
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Collection<LocalResolvedArtifact> resolve() throws IllegalStateException, ResolutionException {
        return getLocalMavenResolvedArtifacts()
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<LocalResolvedArtifact> resolve(String canonicalForm) throws IllegalStateException, ResolutionException {
        MavenCoordinate mavenCoordinate = MavenCoordinates.createCoordinate(canonicalForm);

        return getLocalMavenResolvedArtifacts()
                .filter(a -> ArtifactUtils.coordinatesMatch(a.getCoordinate(), mavenCoordinate))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<LocalResolvedArtifact> resolve(String... canonicalForms) throws IllegalStateException, ResolutionException {
        if (canonicalForms == null || canonicalForms.length == 0) {
            return Collections.emptySet();
        }

        Set<MavenCoordinate> mavenCoordinates = Arrays.stream(canonicalForms)
                .map(MavenCoordinates::createCoordinate)
                .collect(Collectors.toSet());

        return getLocalMavenResolvedArtifacts()
                .filter(a -> mavenCoordinates.stream().anyMatch(m -> ArtifactUtils.coordinatesMatch(a.getCoordinate(), m)))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<LocalResolvedArtifact> resolve(Collection<String> canonicalForms) throws IllegalStateException, ResolutionException {
        if (canonicalForms == null || canonicalForms.isEmpty()) {
            return Collections.emptySet();
        }

        Set<MavenCoordinate> mavenCoordinates = canonicalForms.stream()
                .map(MavenCoordinates::createCoordinate)
                .collect(Collectors.toSet());

        return getLocalMavenResolvedArtifacts()
                .filter(a -> mavenCoordinates.stream().anyMatch(m -> ArtifactUtils.coordinatesMatch(a.getCoordinate(), m)))
                .collect(Collectors.toSet());
    }

    private Stream<LocalResolvedArtifact> getLocalMavenResolvedArtifacts() {
        return Arrays.stream(path.toFile()
                .listFiles(file -> file.isFile() && file.getName().endsWith(".jar")))
                .map(ArtifactUtils::fromFile);
    }
}
