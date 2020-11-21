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

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;

import java.nio.file.Path;

public class CompositeMavenFormatStageImpl implements CompositeMavenFormatStage {
    private final LocalMavenFormatStage local;
    private final MavenFormatStage remote;

    public CompositeMavenFormatStageImpl(LocalMavenFormatStage local, MavenFormatStage remote) {
        this.local = local != null ? local : new EmptyLocalMavenFormatStage();
        this.remote = remote != null ? remote : new EmptyMavenFormatStage();
    }

    @Override
    public Path[] asPath() {
        Path[] a = local.asPath();
        Path[] b = remote.as(Path.class);
        Path[] c = new Path[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
