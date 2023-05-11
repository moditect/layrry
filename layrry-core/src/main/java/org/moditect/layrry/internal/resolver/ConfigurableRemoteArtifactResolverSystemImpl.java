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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtimes;

public class ConfigurableRemoteArtifactResolverSystemImpl implements ConfigurableRemoteArtifactResolverSystem {
    private final ContextOverrides.Builder contextOverridesBuilder;
    private boolean enabled = true;

    public ConfigurableRemoteArtifactResolverSystemImpl() {
        this.contextOverridesBuilder = ContextOverrides.Builder.create();
        this.contextOverridesBuilder.withUserSettings(true);
    }

    @Override
    public RemoteResolve enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public RemoteResolve fromFile(Path file) throws IllegalArgumentException {
        this.contextOverridesBuilder.settingsXml(file);
        return this;
    }

    @Override
    public RemoteResolve workOffline(boolean workOffline) {
        this.contextOverridesBuilder.offline(workOffline);
        return this;
    }

    @Override
    public RemoteResolve withMavenCentralRepo(boolean useMavenCentral) {
        if (useMavenCentral) {
            this.contextOverridesBuilder.repositories(Collections.singletonList(ContextOverrides.CENTRAL));
        } else {
            this.contextOverridesBuilder.repositories(null);
        }
        return this;
    }

    @Override
    public Collection<ArtifactResult> resolve() throws IllegalStateException, ArtifactResolutionException {
        if (!enabled)
            return Collections.emptyList();
        return resolve(Collections.emptyList()); // what is being resolved here?
    }

    @Override
    public Collection<ArtifactResult> resolve(String canonicalForm) throws IllegalArgumentException, ArtifactResolutionException {
        if (!enabled)
            return Collections.emptyList();
        return resolve(Collections.singletonList(canonicalForm));
    }

    @Override
    public Collection<ArtifactResult> resolve(String... canonicalForms) throws IllegalArgumentException, ArtifactResolutionException {
        if (!enabled)
            return Collections.emptyList();
        return resolve(Arrays.asList(canonicalForms));
    }

    @Override
    public Collection<ArtifactResult> resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ArtifactResolutionException {
        if (!enabled)
            return Collections.emptyList();
        try (Context context = Runtimes.INSTANCE.getRuntime().create(this.contextOverridesBuilder.build())) {
            List<ArtifactRequest> requests = canonicalForms.stream()
                    .map(ArtifactUtils::fromCanonical)
                    .map(a -> new ArtifactRequest(a, context.remoteRepositories(), "layrri"))
                    .collect(Collectors.toList());
            return context.repositorySystem().resolveArtifacts(context.repositorySystemSession(), requests);
        }
    }
}
