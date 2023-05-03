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

public final class LocalRepositories {
    /**
     * No instances
     */
    private LocalRepositories() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Creates a new <code>MavenLocalRepository</code> with ID and Path. Please note that the repository layout should always be set to default.
     *
     * @param id     The unique ID of the repository to create (arbitrary name)
     * @param path   The base Path of the Maven repository
     * @param layout the repository layout. Either "default" or "flat
     * @return A new <code>MavenLocalRepository</code> with the given ID and Path.
     * @throws IllegalArgumentException for null or empty id
     * @throws RuntimeException         if an error occurred during <code>MavenLocalRepository</code> instance creation
     */
    public static LocalRepository createLocalRepository(final String id, final Path path, final String layout) {
        // Argument tests are inside the impl constructor
        if ("flat".equals(layout)) {
            return new FlatLocalRepository(id, path);
        }
        else if ("default".equals(layout)) {
            return new DefaultLocalRepository(id, path);
        }
        throw new IllegalArgumentException("layout must be 'default' or 'flat.");
    }

    /**
     * Overload of {@link #createLocalRepository(String, Path, String)} that thrown an exception if Path is wrong.
     *
     * @param id     The unique ID of the repository to create (arbitrary name)
     * @param path   The base Path of the Maven repository
     * @param layout the repository layout. Either "default" or "flat
     * @return A new <code>MavenLocalRepository</code> with the given ID and Path.
     * @throws IllegalArgumentException for null or empty id or if the Path is technically wrong or null
     */
    public static LocalRepository createLocalRepository(final String id, final String path, final String layout)
            throws IllegalArgumentException {
        if ("flat".equals(layout)) {
            return new FlatLocalRepository(id, path);
        }
        else if ("default".equals(layout)) {
            return new DefaultLocalRepository(id, path);
        }
        throw new IllegalArgumentException("layout must be 'default' or 'flat.");
    }
}
