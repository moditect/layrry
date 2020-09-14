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
package org.moditect.layrry.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * This type allows external configuration to be expressed with a custom format.
 */
public interface LayersConfigParser {

    /**
     * Whether the given config file format is supported or not.</p>
     * Implementors would typically look at the file extension.
     *
     * @param layersConfigFile the configuration file to inspect
     * @return {@code true} if the given format is supported, {@code false} otherwise.
     */
    boolean supports(Path layersConfigFile);

    /**
     * Reads and parses external configuration into a {@code LayersConfig} instance.
     * @param inputStream the configuration's input source
     * @return a configured {@code LayersConfig} instance.
     * @throws IOException if an error occurs while reading from the {@code InputStream}.
     */
    LayersConfig parse(InputStream inputStream) throws IOException;

}
