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
package org.moditect.layrry.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class Resolve {

    private Map<String, Repository> localRepositories = new LinkedHashMap<>();
    private boolean remote = true;
    private boolean workOffline = false;
    private boolean useMavenCentral = true;
    private String fromFile;

    public Map<String, Repository> getLocalRepositories() {
        return localRepositories;
    }

    public void setLocalRepositories(Map<String, Repository> localRepositories) {
        this.localRepositories = localRepositories;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public boolean isWorkOffline() {
        return workOffline;
    }

    public void setWorkOffline(boolean workOffline) {
        this.workOffline = workOffline;
    }

    public boolean isUseMavenCentral() {
        return useMavenCentral;
    }

    public void setUseMavenCentral(boolean useMavenCentral) {
        this.useMavenCentral = useMavenCentral;
    }

    public String getFromFile() {
        return fromFile;
    }

    public void setFromFile(String fromFile) {
        this.fromFile = fromFile;
    }

    @Override
    public String toString() {
        return "Resolve [remote=" + remote +
                ", workOffline=" + workOffline +
                ", useMavenCentral=" + useMavenCentral +
                ", fromFile=" + fromFile +
                ", localRepositories=" + localRepositories + "]";
    }
}
