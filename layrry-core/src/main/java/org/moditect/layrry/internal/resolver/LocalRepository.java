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
import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;

public interface LocalRepository {
    /**
     * Returns the unique ID of this {@link LocalRepository}. The ID is arbitrary. There is no default, and this is
     * never <code>null</code>
     *
     * @return The unique ID of this {@link LocalRepository}.
     */
    String getId();

    /**
     * Returns the type, i.e. the layout of this {@link LocalRepository}. Either "default" or "flat".
     *
     * @return The type, i.e. the layout of this {@link LocalRepository}. Either "default" or "flat".
     */
    String getType();

    /**
     * Returns the Path of this {@link LocalRepository}. There is no default, and this is never <code>null</code>
     *
     * @return The Path of this {@link LocalRepository}. There is no default, and this is never <code>null</code>
     */
    Path getPath();

    Collection<LocalResolvedArtifact> resolve() throws IllegalStateException, ResolutionException;

    Collection<LocalResolvedArtifact> resolve(String canonicalForm) throws IllegalStateException, ResolutionException;

    Collection<LocalResolvedArtifact> resolve(String... canonicalForms) throws IllegalStateException, ResolutionException;

    Collection<LocalResolvedArtifact> resolve(Collection<String> canonicalForms) throws IllegalStateException, ResolutionException;
}
