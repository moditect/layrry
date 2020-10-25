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

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurableLocalMavenResolverSystemImpl implements ConfigurableLocalMavenResolverSystem {
    private final Map<String, MavenLocalRepository> localRepositories = new LinkedHashMap<>();

    @Override
    public ConfigurableLocalMavenResolverSystem withLocalRepo(String id, String path, String layout) {
        withLocalRepo(MavenLocalRepositories.createLocalRepository(id, path, layout));
        return this;
    }

    @Override
    public ConfigurableLocalMavenResolverSystem withLocalRepo(String id, Path path, String layout) {
        withLocalRepo(MavenLocalRepositories.createLocalRepository(id, path, layout));
        return this;
    }

    @Override
    public ConfigurableLocalMavenResolverSystem withLocalRepo(MavenLocalRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        if (localRepositories.containsKey(repository.getId())) {
            throw new IllegalArgumentException("Repository id must be unique. '" + repository.getId() + "'");
        }
        localRepositories.put(repository.getId(), repository);
        return this;
    }

    @Override
    public LocalMavenFormatStage resolve() throws IllegalStateException, ResolutionException {
        Collection<LocalMavenResolvedArtifact> artifacts = new ArrayList<>();

        for (MavenLocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve());
        }

        return new LocalMavenFormatStageImpl(artifacts);
    }

    @Override
    public LocalMavenFormatStage resolve(String canonicalForm) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        Collection<LocalMavenResolvedArtifact> artifacts = new ArrayList<>();

        for (MavenLocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve(canonicalForm));
        }

        return new LocalMavenFormatStageImpl(artifacts);
    }

    @Override
    public LocalMavenFormatStage resolve(String... canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        Collection<LocalMavenResolvedArtifact> artifacts = new ArrayList<>();

        for (MavenLocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve(canonicalForms));
        }

        return new LocalMavenFormatStageImpl(artifacts);
    }

    @Override
    public LocalMavenFormatStage resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        Collection<LocalMavenResolvedArtifact> artifacts = new ArrayList<>();

        for (MavenLocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve(canonicalForms));
        }

        return new LocalMavenFormatStageImpl(artifacts);
    }
}
