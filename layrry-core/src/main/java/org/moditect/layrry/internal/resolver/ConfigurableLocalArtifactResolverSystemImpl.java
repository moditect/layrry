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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

public class ConfigurableLocalArtifactResolverSystemImpl implements ConfigurableLocalArtifactResolverSystem {
    private final Map<String, LocalRepository> localRepositories = new LinkedHashMap<>();

    @Override
    public String toString() {
        return new StringBuilder("ConfigurableLocalArtifactResolverSystem[repositories=")
                .append(localRepositories)
                .append("]")
                .toString();
    }

    @Override
    public ConfigurableLocalArtifactResolverSystem withLocalRepo(String id, String path, String layout) {
        withLocalRepo(LocalRepositories.createLocalRepository(id, path, layout));
        return this;
    }

    @Override
    public ConfigurableLocalArtifactResolverSystem withLocalRepo(String id, Path path, String layout) {
        withLocalRepo(LocalRepositories.createLocalRepository(id, path, layout));
        return this;
    }

    @Override
    public ConfigurableLocalArtifactResolverSystem withLocalRepo(LocalRepository repository) {
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
    public LocalFormatStage resolve() throws IllegalStateException, ResolutionException {
        Collection<LocalResolvedArtifact> artifacts = new ArrayList<>();

        for (LocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve());
        }

        return new LocalFormatStageImpl(artifacts);
    }

    @Override
    public LocalFormatStage resolve(String canonicalForm) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        Collection<LocalResolvedArtifact> artifacts = new ArrayList<>();

        for (LocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve(canonicalForm));
        }

        return new LocalFormatStageImpl(artifacts);
    }

    @Override
    public LocalFormatStage resolve(String... canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        Collection<LocalResolvedArtifact> artifacts = new ArrayList<>();

        for (LocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve(canonicalForms));
        }

        return new LocalFormatStageImpl(artifacts);
    }

    @Override
    public LocalFormatStage resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        Collection<LocalResolvedArtifact> artifacts = new ArrayList<>();

        for (LocalRepository repository : localRepositories.values()) {
            artifacts.addAll(repository.resolve(canonicalForms));
        }

        return new LocalFormatStageImpl(artifacts);
    }
}
