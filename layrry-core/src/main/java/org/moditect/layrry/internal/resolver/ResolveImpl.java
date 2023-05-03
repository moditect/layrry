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

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

public class ResolveImpl implements Resolve, ArtifactResolver {
    private ConfigurableLocalArtifactResolverSystem local;
    private ConfigurableRemoteArtifactResolverSystem remote;

    @Override
    public LocalResolve local() {
        return fetchLocal();
    }

    @Override
    public RemoteResolve remote() {
        return fetchRemote();
    }

    public CompositeFormatStage resolve() throws IllegalStateException, ResolutionException {
        return new CompositeFormatStageImpl(
                fetchLocal().resolve(),
                fetchRemote().resolve());
    }

    public CompositeFormatStage resolve(String canonicalForm) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        return new CompositeFormatStageImpl(
                fetchLocal().resolve(canonicalForm),
                fetchRemote().resolve(canonicalForm));
    }

    public CompositeFormatStage resolve(String... canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        return new CompositeFormatStageImpl(
                fetchLocal().resolve(canonicalForms),
                fetchRemote().resolve(canonicalForms));
    }

    public CompositeFormatStage resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        return new CompositeFormatStageImpl(
                fetchLocal().resolve(canonicalForms),
                fetchRemote().resolve(canonicalForms));
    }

    private ConfigurableLocalArtifactResolverSystem fetchLocal() {
        synchronized (this) {
            if (local == null) {
                local = new ConfigurableLocalArtifactResolverSystemImpl();
            }
            return local;
        }
    }

    private ConfigurableRemoteArtifactResolverSystem fetchRemote() {
        synchronized (this) {
            if (remote == null) {
                remote = new ConfigurableRemoteArtifactResolverSystemImpl(org.jboss.shrinkwrap.resolver.api.maven.Maven.configureResolver());
            }
            return remote;
        }
    }
}
