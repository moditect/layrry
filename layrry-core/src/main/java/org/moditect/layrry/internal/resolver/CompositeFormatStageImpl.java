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
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactResult;

public class CompositeFormatStageImpl implements CompositeFormatStage {
    private final LocalFormatStage local;
    private final Collection<ArtifactResult> remote;

    public CompositeFormatStageImpl(LocalFormatStage local, Collection<ArtifactResult> remote) {
        this.local = local != null ? local : new EmptyLocalFormatStage();
        this.remote = remote != null ? remote : Collections.emptyList();
    }

    private Path[] remoteAsPathArray() {
        return remote.stream()
                .map(ArtifactResult::getArtifact)
                .map(Artifact::getFile)
                .filter(Objects::nonNull)
                .map(File::toPath)
                .collect(Collectors.toSet())
                .toArray(new Path[remote.size()]);
    }

    @Override
    public Path[] asPath() {
        Path[] a = local.asPath();
        Path[] b = remoteAsPathArray();
        Path[] c = new Path[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
