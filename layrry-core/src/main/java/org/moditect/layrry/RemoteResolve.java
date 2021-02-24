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
package org.moditect.layrry;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;

import java.nio.file.Path;

/**
 * Configures remote artifact resolution.
 */
public interface RemoteResolve {
    /**
     * Sets whether to consult any repositories Maven (Maven local and remote); defaults to true.
     *
     * @param enabled Whether to consult any repositories Maven (Maven local and remote); defaults to true.
     * @return Modified instance of {@code RemoteResolve}
     */
    RemoteResolve enabled(boolean enabled);

    /**
     * Configures this {@link RemoteResolve} from the specified file
     *
     * @param file The file the {@link RemoteResolve} should be configured from
     * @return Modified instance of {@code RemoteResolve}
     * @throws IllegalArgumentException
     *             If the file is not specified, is a directory, or does not exist
     * @throws InvalidConfigurationFileException
     *             If the file is not in correct format
     */
    RemoteResolve fromFile(Path file);

    /**
     * Sets whether to consult any remote Maven Repository in resolution; defaults to false.
     * This method is able to override the value defined in settings.xml if loaded later.
     *
     * @param workOffline Whether to consult any remote Maven Repository in resolution; defaults to false.
     * @return Modified instance of {@code RemoteResolve}
     */
    RemoteResolve workOffline(boolean workOffline);

    /**
     * Sets whether to consult the Maven Central Repository in resolution; defaults to true.
     *
     * @param useMavenCentral Whether to consult the Maven Central Repository in resolution; defaults to true.
     * @return Modified instance of {@code RemoteResolve}
     */
    RemoteResolve withMavenCentralRepo(boolean useMavenCentral);
    
    /**
     * Sets whether to resolve transitive dependencies or not; defaults to false.
     *
     * @param useTransitivity Whether to resolve transitive dependencies or not; defaults to false.
     * @return Modified instance of {@code RemoteResolve}
     */
    RemoteResolve withTransitivity(boolean useTransitivity);
}
