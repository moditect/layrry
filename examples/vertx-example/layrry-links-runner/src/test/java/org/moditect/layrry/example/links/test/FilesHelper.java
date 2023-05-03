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
package org.moditect.layrry.example.links.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

public class FilesHelper {

    private FilesHelper() {
    }

    public static void deleteFolder(Path src, Path dest) {
        if (!Files.exists(dest)) {
            return;
        }

        try {
            Files.list(src)
                    .sorted(Comparator.reverseOrder())
                    .map(source -> dest.resolve(src.relativize(source)).toFile())
                    .forEach(File::delete);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFiles(Path src, Path dest) {
        try (Stream<Path> stream = Files.list(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
