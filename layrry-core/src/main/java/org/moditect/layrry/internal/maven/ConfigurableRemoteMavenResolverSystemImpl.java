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
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.moditect.layrry.RemoteMaven;

import java.nio.file.Path;
import java.util.Collection;

public class ConfigurableRemoteMavenResolverSystemImpl implements ConfigurableRemoteMavenResolverSystem {
    private final ConfigurableMavenResolverSystem delegate;
    private boolean enabled = true;

    public ConfigurableRemoteMavenResolverSystemImpl(ConfigurableMavenResolverSystem delegate) {
        this.delegate = delegate;
    }

    @Override
    public RemoteMaven enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public RemoteMaven fromFile(Path file) throws IllegalArgumentException, InvalidConfigurationFileException {
        delegate.fromFile(file.toFile());
        return this;
    }

    @Override
    public RemoteMaven workOffline(boolean workOffline) {
        delegate.workOffline(workOffline);
        return this;
    }

    @Override
    public RemoteMaven withMavenCentralRepo(boolean useMavenCentral) {
        delegate.withMavenCentralRepo(useMavenCentral);
        return this;
    }

    @Override
    public MavenFormatStage resolve() throws IllegalStateException, ResolutionException {
        if (!enabled) return new EmptyMavenFormatStage();
        return delegate.resolve().withoutTransitivity();
    }

    @Override
    public MavenFormatStage resolve(String canonicalForm) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        if (!enabled) return new EmptyMavenFormatStage();
        return delegate.resolve(canonicalForm).withoutTransitivity();
    }

    @Override
    public MavenFormatStage resolve(String... canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        if (!enabled) return new EmptyMavenFormatStage();
        return delegate.resolve(canonicalForms).withoutTransitivity();
    }

    @Override
    public MavenFormatStage resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ResolutionException, CoordinateParseException {
        if (!enabled) return new EmptyMavenFormatStage();
        return delegate.resolve(canonicalForms).withoutTransitivity();
    }
}
