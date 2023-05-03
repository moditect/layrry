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
import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertTrue;

public class DefaultLocalRepositoryTest {
    @Rule
    public TemporaryFolder repository = new TemporaryFolder();

    @Before
    public void setupRepository() throws IOException {
        createArtifact("com.acme", "aaa", "1.2.3", null);
        createArtifact("com.acme.extension", "bbb", "1.2.3-SNAPSHOT", null);
        createArtifact("com.acme.plugins", "ccc", "1.2.3", "mac");
    }

    @Test
    public void sanityCheck() {
        // given:
        LocalRepository localRepository = LocalRepositories.createLocalRepository("test", repository.getRoot().toPath(), "default");

        // when:
        Collection<LocalResolvedArtifact> artifacts = localRepository.resolve();

        // then:
        assertThat(artifacts, hasSize(3));
        assertTrue(artifacts.stream()
                .filter(a -> "1.2.3-SNAPSHOT".equals(a.getCoordinate().getVersion()))
                .findFirst().isPresent());
        assertTrue(artifacts.stream()
                .filter(a -> "mac".equals(a.getCoordinate().getClassifier()))
                .findFirst().isPresent());
    }

    private void createArtifact(String groupId, String artifactId, String version, String classifier) throws IOException {
        File dir = repository.newFolder((groupId + "." + artifactId).split("\\."));
        dir = new File(dir, version);
        dir.mkdirs();
        String fileName = artifactId + "-" + version + (classifier != null ? "-" + classifier : "") + ".jar";
        new File(dir, fileName).createNewFile();
    }
}
