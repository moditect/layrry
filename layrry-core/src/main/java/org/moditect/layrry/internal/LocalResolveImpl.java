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
package org.moditect.layrry.internal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moditect.layrry.LocalResolve;
import org.moditect.layrry.internal.resolver.LocalRepositories;
import org.moditect.layrry.internal.resolver.LocalRepository;

public class LocalResolveImpl implements LocalResolve {
    private final List<LocalRepository> localRepositories = new ArrayList<>();

    final List<LocalRepository> localRepositories() {
        return Collections.unmodifiableList(localRepositories);
    }

    @Override
    public String toString() {
        return new StringBuilder("LocalResolve[repositories=")
                .append(localRepositories)
                .append("]")
                .toString();
    }

    @Override
    public LocalResolveImpl withLocalRepo(String id, String path, String layout) {
        localRepositories.add(LocalRepositories.createLocalRepository(id, path, layout));
        return this;
    }

    @Override
    public LocalResolveImpl withLocalRepo(String id, Path path, String layout) {
        localRepositories.add(LocalRepositories.createLocalRepository(id, path, layout));
        return this;
    }

    @Override
    public LocalResolveImpl withLocalRepo(LocalRepository repository) {
        localRepositories.add(repository);
        return this;
    }
}
