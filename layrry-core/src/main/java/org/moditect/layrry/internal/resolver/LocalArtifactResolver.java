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

import org.eclipse.aether.resolution.ArtifactResolutionException;

/**
 * Resolves artifacts using a locally configured repositories.
 */
public interface LocalArtifactResolver {
    /**
     * Begins resolution of the prior-defined dependencies.
     *
     * @return The {@link LocalFormatStage} for the user to define the artifact format to use.
     * @throws IllegalStateException
     * If no dependencies have yet been added
     * @throws ArtifactResolutionException
     * If an error occurred in resolution
     */
    LocalFormatStage resolve() throws IllegalStateException, ArtifactResolutionException;

    /**
     * Begins resolution by defining the single desired dependency (in canonical form).
     * Previously-added dependencies will be included in resolution.
     *
     * @param canonicalForm The canonical form of the single desired dependency.
     * @return The {@link LocalFormatStage} for the user to define the artifact format to use.
     * @throws IllegalArgumentException
     * If no coordinate is supplied
     * @throws ArtifactResolutionException
     * If an error occurred in resolution
     */
    LocalFormatStage resolve(String canonicalForm) throws IllegalArgumentException, ArtifactResolutionException;

    /**
     * Begins resolution by defining a set of desired dependencies (in canonical form).
     * Previously-added dependencies will be included in resolution.
     *
     * @param canonicalForms The canonical forms of the set of desired dependencies.
     * @return The {@link LocalFormatStage} for the user to define the artifact format to use.
     * @throws IllegalArgumentException
     * If no coordinates are supplied
     * @throws ArtifactResolutionException
     * If an error occurred in resolution
     */
    LocalFormatStage resolve(String... canonicalForms) throws IllegalArgumentException, ArtifactResolutionException;

    /**
     * Begins resolution by defining a set of desired dependencies (in canonical form).
     * Previously-added dependencies will be included in resolution.
     *
     * @param canonicalForms The canonical forms of the set of desired dependencies.
     * @return The {@link LocalFormatStage} for the user to define the artifact format to use.
     * @throws IllegalArgumentException
     * If no coordinates are supplied
     * @throws ArtifactResolutionException
     * If an error occurred in resolution
     */
    LocalFormatStage resolve(Collection<String> canonicalForms) throws IllegalArgumentException, ArtifactResolutionException;
}
