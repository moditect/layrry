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
package org.moditect.layrry.internal.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class FilesHelper {

    private FilesHelper() {
    }

    public static void copyFolder(Path src, Path dest) {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copy(Path source, Path dest) {
        try {
            Files.createDirectories(dest.getParent());
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void unpack(Path src, Path dest) {
        File destinationDir = dest.toFile();

        try (InputStream fi = Files.newInputStream(src);
                InputStream bi = new BufferedInputStream(fi);
                ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(bi)) {

            String filename = src.getFileName().toString();
            // subtract .zip, .tar
            filename = filename.substring(0, filename.length() - 4);
            unpack(filename + "/", destinationDir, in);
        }
        catch (ArchiveException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void unpackCompressed(Path src, Path dest) {
        File destinationDir = dest.toFile();

        try (InputStream fi = Files.newInputStream(src);
                InputStream bi = new BufferedInputStream(fi);
                InputStream gzi = new GzipCompressorInputStream(bi);
                ArchiveInputStream in = new TarArchiveInputStream(gzi)) {
            String filename = src.getFileName().toString();
            // subtract .tar.gz
            filename = filename.substring(0, filename.length() - 7);
            unpack(filename + "/", destinationDir, in);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void unpack(String basename, File destinationDir, ArchiveInputStream in) throws IOException {
        ArchiveEntry entry = null;
        while ((entry = in.getNextEntry()) != null) {
            if (!in.canReadEntryData(entry)) {
                // log something?
                continue;
            }

            String entryName = entry.getName();
            if (entryName.startsWith(basename) && entryName.length() > basename.length() + 1) {
                entryName = entryName.substring(basename.length());
            }

            File file = new File(destinationDir, entryName);
            String destDirPath = destinationDir.getCanonicalPath();
            String destFilePath = file.getCanonicalPath();
            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + entry.getName());
            }

            if (entry.isDirectory()) {
                if (!file.isDirectory() && !file.mkdirs()) {
                    throw new IOException("failed to create directory " + file);
                }
            }
            else {
                File parent = file.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                }
                try (OutputStream o = Files.newOutputStream(file.toPath())) {
                    IOUtils.copy(in, o);
                }
            }
        }
    }
}
