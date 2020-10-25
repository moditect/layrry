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
import org.moditect.layrry.LocalMaven;
import org.moditect.layrry.Maven;
import org.moditect.layrry.RemoteMaven;

import java.util.Collection;

public class MavenImpl implements Maven, MavenResolver {
    private ConfigurableLocalMavenResolverSystem local;
    private ConfigurableRemoteMavenResolverSystem remote;

    @Override
    public LocalMaven local() {
        return fetchLocal();
    }

    @Override
    public RemoteMaven remote() {
        return fetchRemote();
    }

    public CompositeMavenFormatStage resolve() throws IllegalStateException, ResolutionException {
        return new CompositeMavenFormatStageImpl(
            fetchLocal().resolve(),
            fetchRemote().resolve());
    }

    public CompositeMavenFormatStage resolve(String canonicalForm) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        return new CompositeMavenFormatStageImpl(
            fetchLocal().resolve(canonicalForm),
            fetchRemote().resolve(canonicalForm));
    }

    public CompositeMavenFormatStage resolve(String... canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        return new CompositeMavenFormatStageImpl(
            fetchLocal().resolve(canonicalForms),
            fetchRemote().resolve(canonicalForms));
    }

    public CompositeMavenFormatStage resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        return new CompositeMavenFormatStageImpl(
            fetchLocal().resolve(canonicalForms),
            fetchRemote().resolve(canonicalForms));
    }

    private ConfigurableLocalMavenResolverSystem fetchLocal() {
        synchronized (this) {
            if (local == null) {
                local = new ConfigurableLocalMavenResolverSystemImpl();
            }
            return local;
        }
    }

    private ConfigurableRemoteMavenResolverSystem fetchRemote() {
        synchronized (this) {
            if (remote == null) {
                remote = new ConfigurableRemoteMavenResolverSystemImpl(org.jboss.shrinkwrap.resolver.api.maven.Maven.configureResolver());
            }
            return remote;
        }
    }
}
