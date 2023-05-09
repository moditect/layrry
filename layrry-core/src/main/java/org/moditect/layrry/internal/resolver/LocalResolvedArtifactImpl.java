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

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.util.artifact.JavaScopes;

class LocalResolvedArtifactImpl implements LocalResolvedArtifact {
    private static final MavenArtifactInfo[] NO_DEPENDENCIES = new MavenArtifactInfo[0];

    private final Artifact mavenCoordinate;
    private final File file;

    LocalResolvedArtifactImpl(Artifact mavenCoordinate, File file) {
        this.mavenCoordinate = mavenCoordinate;
        this.file = file;
    }

    @Override
    public Artifact getCoordinate() {
        return mavenCoordinate;
    }

    @Override
    public String getResolvedVersion() {
        return mavenCoordinate.getVersion();
    }

    @Override
    public boolean isSnapshotVersion() {
        return mavenCoordinate.isSnapshot();
    }

    @Override
    public String getExtension() {
        return "jar";
    }

    @Override
    public MavenArtifactInfo[] getDependencies() {
        return NO_DEPENDENCIES;
    }

    @Override
    public String getScope() {
        return JavaScopes.COMPILE;
    }

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public File getFile() {
        return file;
    }
}
