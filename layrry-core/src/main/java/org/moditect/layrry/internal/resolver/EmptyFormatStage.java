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
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.NonUniqueResultException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;

public class EmptyFormatStage implements MavenFormatStage {
    @Override
    public File[] asFile() {
        return new File[0];
    }

    @Override
    public File asSingleFile() throws NonUniqueResultException, NoResolvedResultException {
        return null;
    }

    @Override
    public InputStream[] asInputStream() {
        return new InputStream[0];
    }

    @Override
    public InputStream asSingleInputStream() throws NonUniqueResultException, NoResolvedResultException {
        return null;
    }

    @Override
    public MavenResolvedArtifact[] asResolvedArtifact() {
        return new MavenResolvedArtifact[0];
    }

    @Override
    public MavenResolvedArtifact asSingleResolvedArtifact() throws NonUniqueResultException, NoResolvedResultException {
        return null;
    }

    @Override
    public <RETURNTYPE> RETURNTYPE[] as(Class<RETURNTYPE> aClass) throws IllegalArgumentException, UnsupportedOperationException {
        @SuppressWarnings("unchecked")
        final RETURNTYPE[] array = (RETURNTYPE[]) Array.newInstance(aClass, 0);
        return array;
    }

    @Override
    public <RETURNTYPE> List<RETURNTYPE> asList(Class<RETURNTYPE> aClass) throws IllegalArgumentException, UnsupportedOperationException {
        return Collections.emptyList();
    }

    @Override
    public <RETURNTYPE> RETURNTYPE asSingle(Class<RETURNTYPE> aClass)
            throws IllegalArgumentException, UnsupportedOperationException, NonUniqueResultException, NoResolvedResultException {
        return null;
    }
}
