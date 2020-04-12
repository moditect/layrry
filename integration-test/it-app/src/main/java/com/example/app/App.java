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
package com.example.app;

import com.example.bar.Bar;
import com.example.foo.Foo;

/**
 * Hello world!
 */
public class App {

    public static void main(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: com.example.app.App <name>");
        }

        new Foo().foo(args[0]);
        new Bar().bar(args[0]);
    }
}
