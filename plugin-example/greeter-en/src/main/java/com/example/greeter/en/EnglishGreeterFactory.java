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
package com.example.greeter.en;

import com.example.greeter.api.Greeter;
import com.example.greeter.api.GreeterFactory;

public class EnglishGreeterFactory implements GreeterFactory {

    @Override
    public String getLanguage() {
        return "English";
    }

    @Override
    public String getFlag() {
        return "🇬🇧";
    }

    @Override
    public Greeter getGreeter() {
        return new Greeter() {

            @Override
            public String greet(String name) {
                return "Hi, " + name;
            }
        };
    }
}
