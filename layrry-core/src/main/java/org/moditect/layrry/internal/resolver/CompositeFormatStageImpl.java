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

import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;

public class CompositeFormatStageImpl implements CompositeFormatStage {
    private final LocalFormatStage local;
    private final MavenFormatStage remote;

    public CompositeFormatStageImpl(LocalFormatStage local, MavenFormatStage remote) {
        this.local = local != null ? local : new EmptyLocalFormatStage();
        this.remote = remote != null ? remote : new EmptyFormatStage();
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
