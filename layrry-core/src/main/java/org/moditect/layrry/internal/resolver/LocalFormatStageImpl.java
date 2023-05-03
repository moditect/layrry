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

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class LocalFormatStageImpl implements LocalFormatStage {
    private final Collection<LocalResolvedArtifact> artifacts;

    public LocalFormatStageImpl(final Collection<LocalResolvedArtifact> artifacts) {
        assert artifacts != null : "Artifacts are required";
        this.artifacts = artifacts;
    }

    @Override
    public Path[] asPath() {
        return artifacts.stream()
                .map(LocalResolvedArtifact::getFile)
                .map(File::toPath)
                .collect(Collectors.toSet())
                .toArray(new Path[artifacts.size()]);
    }
}