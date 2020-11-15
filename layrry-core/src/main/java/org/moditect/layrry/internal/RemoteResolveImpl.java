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
package org.moditect.layrry.internal;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.moditect.layrry.RemoteResolve;

import java.nio.file.Path;

public class RemoteResolveImpl implements RemoteResolve {
    private boolean enabled = true;
    private boolean workOffline;
    private boolean useMavenCentral = true;
    private Path configFile;

    boolean enabled() {
        return enabled;
    }

    boolean workOffline() {
        return workOffline;
    }

    boolean useMavenCentral() {
        return useMavenCentral;
    }

    Path configFile() {
        return configFile;
    }

    @Override
    public RemoteResolveImpl enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public RemoteResolveImpl fromFile(Path file) throws IllegalArgumentException, InvalidConfigurationFileException {
        this.configFile = file;
        return this;
    }

    @Override
    public RemoteResolveImpl workOffline(boolean workOffline) {
        this.workOffline = workOffline;
        return this;
    }

    @Override
    public RemoteResolveImpl withMavenCentralRepo(boolean useMavenCentral) {
        this.useMavenCentral = useMavenCentral;
        return this;
    }
}
