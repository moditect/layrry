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
package org.moditect.layrry.it.bar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.moditect.layrry.it.greeter.Greeter;

public class Bar {

    private static final Logger LOGGER = LogManager.getLogger(Bar.class);

    public void bar(String name) {
        LOGGER.info(new Greeter().hello(name, "Bar"));
        LOGGER.info(new Greeter().goodBye(name, "Bar"));
    }
}
