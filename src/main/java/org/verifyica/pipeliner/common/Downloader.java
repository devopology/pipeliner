/*
 * Copyright (C) 2024-present Pipeliner project authors and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.verifyica.pipeliner.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;

/** Class to implement Downloader */
public class Downloader {

    private static final FileAttribute<?> PERMISSIONS =
            PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));

    /** Constructor */
    private Downloader() {
        // INTENTIONALLY BLANK
    }

    /**
     * Download a file
     *
     * @param url URL of the file
     * @return the path to the downloaded file
     * @throws IOException If an error occurs
     */
    public static Path download(String url) throws IOException {
        Path path = Files.createTempFile("pipeliner-extension-", "", PERMISSIONS);
        ShutdownHook.deleteOnExit(path);

        if (url.startsWith("file://")) {
            Path localFilePath = new File(url.replace("file://", "")).toPath();
            Files.copy(localFilePath, path, StandardCopyOption.REPLACE_EXISTING);
        } else {
            URL fileUrl = URI.create(url).toURL();
            try (InputStream in = fileUrl.openStream();
                    OutputStream out = Files.newOutputStream(path)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }

        return path;
    }
}