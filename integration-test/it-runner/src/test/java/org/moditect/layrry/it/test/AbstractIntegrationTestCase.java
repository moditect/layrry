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
package org.moditect.layrry.it.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertTrue;

public abstract class AbstractIntegrationTestCase {
    private ByteArrayOutputStream sysOut;
    private PrintStream originalSysOut;

    @Before
    public void setupSysOut() {
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));

        originalSysOut = System.out;
    }

    @After
    public void restoreSysOut() {
        System.setOut(originalSysOut);
    }

    protected void assertOutput() {
        String output = sysOut.toString();

        assertTrue(output.contains("org.moditect.layrry.it.foo.Foo - Hello, Alice from Foo (Greeter 1.0.0)"));
        assertTrue(output.contains("org.moditect.layrry.it.bar.Bar - Hello, Alice from Bar (Greeter 2.0.0)"));
        assertTrue(output.contains("org.moditect.layrry.it.bar.Bar - Good bye, Alice from Bar (Greeter 2.0.0)"));
    }
}
