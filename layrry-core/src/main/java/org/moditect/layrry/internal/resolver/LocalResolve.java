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

/**
 * Configures local artifact resolution.
 */
public interface LocalResolve {
    /**
     * Adds a local repository to use in resolution.
     *
     * @param id     a unique arbitrary ID such as "local1"
     * @param path   the repository Path, such as "/path/to/repository"
     * @param layout the repository layout. Should always be "default" (may be reused one day by Maven with other values).
     * @return Modified instance of {@code LocalMaven}
     * @throws IllegalArgumentException if name or layout are null or if layout is not "default", or if no path protocol is
     *                                  specified, or an unknown path protocol is found, or path is null
     */
    LocalResolve withLocalRepo(String id, String path, String layout);

    /**
     * See {@link #withLocalRepo(String, String, String)}
     *
     * @param id     a unique arbitrary ID such as "local1"
     * @param path   the repository Path, such as "/path/to/repository"
     * @param layout the repository layout. Should always be "default" (may be reused one day by Maven with other values).
     * @return Modified instance of {@code LocalMaven}
     */
    LocalResolve withLocalRepo(String id, Path path, String layout);

    /**
     * Adds a local repository to use in resolution. This repository should be built with
     * {@link LocalRepositories#createLocalRepository(String, Path, String)}
     *
     * @param repository The local repository
     * @return Modified instance of {@code LocalMaven}
     */
    LocalResolve withLocalRepo(LocalRepository repository);
}
