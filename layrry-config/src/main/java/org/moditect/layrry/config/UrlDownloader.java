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
package org.moditect.layrry.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Properties;
import java.util.ServiceLoader;

class UrlDownloader {
    private static final String JAVA_NET_USE_SYSTEM_PROXIES = "java.net.useSystemProxies";

    static Path download(URL url, Properties properties) {
        setupProxy(properties);

        try {
            URLConnection connection = url.openConnection();
            // default timeout is 30 seconds
            connection.setConnectTimeout(getInteger("connection.timeout", properties, 30_000));
            connection.connect();
            String fileName = resolveFileName(url);
            String fileExtension = resolveFileExtension(fileName, connection);
            if (fileExtension != null) {
                fileName += fileExtension;
            }

            File layersConfigFile = File.createTempFile("layrry", fileName);
            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(layersConfigFile)) {
                in.transferTo(out);
            }
            return layersConfigFile.toPath();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected error downloading layers file from " + url, e);
        }
    }

    private static String resolveFileName(URL url) {
        String path = url.getPath();
        // TODO: should ending '/' be chopped?
        // TODO: perhaps the name is defined in the headers
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        int index = path.lastIndexOf('/');
        if (index < 0) {
            return path;
        }
        return path.substring(index + 1);
    }

    private static String resolveFileExtension(String fileName, URLConnection connection) {
        int dot = fileName.lastIndexOf('.');
        if (dot > -1) {
            // no need to guess the file extension
            // we assume it's the correct one
            return null;
        }

        String contentType = connection.getContentType();
        ServiceLoader<LayersConfigParser> parsers = ServiceLoader.load(LayersConfigParser.class, LayersConfigLoader.class.getClassLoader());

        for (LayersConfigParser parser : parsers) {
            for (String mimeType : parser.getSupportedMimeTypes()) {
                if (mimeType.equals(contentType)) {
                    return "." + parser.getPreferredFileExtension();
                }
            }
        }

        return null;
    }

    private static void setupProxy(Properties properties) {
        String useSystemProxies = properties.getProperty(JAVA_NET_USE_SYSTEM_PROXIES);
        if (useSystemProxies != null && getBoolean(JAVA_NET_USE_SYSTEM_PROXIES, properties)) {
            // we're done here
            System.setProperty(JAVA_NET_USE_SYSTEM_PROXIES, "true");
            return;
        }

        if (getBoolean("http.proxy", properties) || getBoolean("https.proxy", properties)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    if (getRequestorType() == Authenticator.RequestorType.PROXY) {
                        String protocol = getRequestingProtocol().toLowerCase();
                        String host = properties.getProperty(protocol + ".proxyHost", "");
                        String port = properties.getProperty(protocol + ".proxyPort", "80");
                        String username = properties.getProperty(protocol + ".proxyUser", "");
                        String password = properties.getProperty(protocol + ".proxyPassword", "");
                        if (getRequestingHost().equalsIgnoreCase(host) &&
                            Integer.parseInt(port) == getRequestingPort()) {
                            return new PasswordAuthentication(username, password.toCharArray());
                        }
                    }
                    return null;
                }
            });
        }

        if (getBoolean("socks.proxy", properties)) {
            System.setProperty("socksProxyHost", properties.getProperty("socks.proxyHost"));
            System.setProperty("socksProxyPort", properties.getProperty("socks.proxyPort"));
            if (getBoolean("socks.proxy.auth", properties)) {
                String username = properties.getProperty("socks.proxyUser", "");
                String password = properties.getProperty("socks.proxyPassword", "");
                System.setProperty("java.net.socks.username", username);
                System.setProperty("java.net.socks.password", password);
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });
            }
        }
    }

    static boolean getBoolean(String name, Properties properties) {
        try {
            return Boolean.parseBoolean(properties.getProperty(name));
        } catch (IllegalArgumentException | NullPointerException e) {
            // ignored
        }
        return false;
    }

    static int getInteger(String name, Properties properties, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(name));
        } catch (NumberFormatException | NullPointerException e) {
            // ignored
        }
        return defaultValue;
    }
}
