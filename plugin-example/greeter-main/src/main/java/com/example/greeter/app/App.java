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
package com.example.greeter.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import com.example.greeter.api.GreeterFactory;

/**
 * Hello world!
 */
public class App {

    public static void main(String... args) throws Exception {
        System.out.println("### Layrry plug-in example ###");
        System.out.println("");

        List<GreeterFactory> factories = getGreeterFactories();

        if (factories.isEmpty()) {
            System.out.println("No greeters available; exiting.");
            return;
        }

        System.out.println("Available greeters:");
        int i = 1;
        for (GreeterFactory greeterFactory : factories) {
            System.out.println(i++ + ": " + greeterFactory.getFlag() + "  " + greeterFactory.getLanguage());
        }


        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

        try {
            int factoryIndex = getGreeterFactoryIndex(factories.size(), systemIn);
            String name = getName(systemIn);

            System.out.println("");
            System.out.print("Here's your " + factories.get(factoryIndex).getLanguage() + " greeting: ");
            System.out.println(factories.get(factoryIndex).getGreeter().greet(name));
        }
        catch(ProgramStoppedException e) {
            System.out.println("Exiting");
        }
    }

    private static int getGreeterFactoryIndex(int numberOfFactories, BufferedReader systemIn) throws IOException {
        String line;

        System.out.println("");
        System.out.print("Choose a greeter: 1: ");

        while((line = systemIn.readLine()) != null) {

            int greeter;
            if (line.isEmpty()) {
                greeter = 1;
            }
            else {
                try {
                    greeter = Integer.parseInt(line);
                }
                catch(NumberFormatException e) {
                    System.out.println("Not a valid number.");
                    System.out.println("");
                    System.out.print("Choose a greeter: 1: ");
                    continue;
                }

            }

            greeter--;

            if (greeter < 0 || greeter > numberOfFactories) {
                System.out.println("Not a valid greeter index.");
                System.out.println("");
                System.out.print("Choose a greeter: 1: ");
                continue;
            }

            return greeter;
        }

        throw new ProgramStoppedException();
    }

    private static String getName(BufferedReader systemIn) throws IOException {
        String line;

        System.out.println("");
        System.out.print("What's your name: ");

        while((line = systemIn.readLine()) != null) {
            if (!line.isEmpty()) {
                return line;
            }

            System.out.println("");
            System.out.print("What's your name: ");
        }

        throw new ProgramStoppedException();
    }

    private static List<GreeterFactory> getGreeterFactories() {
        return ServiceLoader.load(App.class.getModule().getLayer(), GreeterFactory.class)
            .stream()
            .map(p -> p.get())
            .sorted((gf1, gf2) -> gf1.getLanguage().compareTo(gf2.getLanguage()))
            .collect(Collectors.toList());
    }

    private static class ProgramStoppedException extends RuntimeException {
    }
}
