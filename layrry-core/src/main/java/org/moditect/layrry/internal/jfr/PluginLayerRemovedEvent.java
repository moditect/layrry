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
package org.moditect.layrry.internal.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Name(PluginLayerRemovedEvent.NAME)
@Label("Plug-in Layer Removed")
@Category("Layrry")
@Description("A plug-in layer was removed")
@StackTrace(false)
public class PluginLayerRemovedEvent extends Event {

    static final String NAME = "org.moditect.layrry.PluginLayerRemovedEvent";

    @Label("Layer Name")
    public String name;

    @Label("Modules")
    public String modules;
}
