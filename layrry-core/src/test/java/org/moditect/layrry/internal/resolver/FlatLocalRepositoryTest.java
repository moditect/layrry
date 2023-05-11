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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertTrue;

public class FlatLocalRepositoryTest {
    @Rule
    public TemporaryFolder repository = new TemporaryFolder();

    @Before
    public void setupRepository() throws IOException {
        File pomProperties = repository.newFile("pom.properties");
        Properties props = new Properties();
        props.setProperty("groupId", "com.acme");
        props.setProperty("artifactId", "ccc");
        props.setProperty("version", "1.2.3");
        props.store(new FileOutputStream(pomProperties), "");

        repository.newFile("aaa-1.2.3.jar");
        repository.newFile("bbb-1.2.3-SNAPSHOT.jar");
        File jarfile = repository.newFile("ccc-1.2.3-mac.jar");

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        props.store(bos, null);
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarfile), manifest)) {
            JarEntry entry = new JarEntry("/META-INF/maven/com/acme/ccc/pom.properties");
            jar.putNextEntry(entry);
            jar.write(bos.toByteArray());
            jar.closeEntry();
        }
    }

    @Test
    public void sanityCheck() {
        // given:
        LocalRepository localRepository = LocalRepositories.createLocalRepository("test", repository.getRoot().toPath(), "flat");

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
}
