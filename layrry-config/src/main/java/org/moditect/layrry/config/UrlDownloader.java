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
package org.moditect.layrry.config;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.ServiceLoader;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

class UrlDownloader {
    private static final String JAVA_NET_USE_SYSTEM_PROXIES = "java.net.useSystemProxies";

    static Path download(URL url, Properties properties) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.of(getLong("connection.timeout", properties, 30_000), ChronoUnit.MILLIS))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2);

        builder = setupProxy(builder, url, properties);
        builder = setupAuthentication(builder, url, properties);

        HttpClient client = builder.build();

        HttpRequest request = createRequest(url);

        try {
            HttpResponse<Path> response = client.send(request, fileBodyHandler(url));
            return response.body();
        }
        catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Unexpected error when downloading from " + url, e);
        }
    }

    private static HttpRequest createRequest(URL url) {
        try {
            return HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .version(HttpClient.Version.HTTP_2)
                    .build();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
    }

    private static HttpResponse.BodyHandler<Path> fileBodyHandler(URL url) {
        return responseInfo -> {
            try {
                // 1. see if 'Content-Disposition' is set, if so use BodyHandlers.ofFileDownload()
                if (responseInfo.headers().firstValue("Content-Disposition").isPresent()) {
                    Path layrryTmpDirPath = Files.createTempDirectory("layrry-download");
                    return HttpResponse.BodyHandlers.ofFileDownload(layrryTmpDirPath, CREATE, WRITE).apply(responseInfo);
                }

                // 2. use BodyHandlers.ofFile() instead
                // must compute file name
                String fileName = resolveFileName(url);
                String fileExtension = resolveFileExtension(fileName, responseInfo.headers());
                if (fileExtension != null) {
                    fileName += fileExtension;
                }

                Path layersConfigFilePath = File.createTempFile("layrry", fileName).toPath();
                return HttpResponse.BodyHandlers.ofFile(layersConfigFilePath).apply(responseInfo);
            }
            catch (IOException ioe) {
                throw new IllegalStateException("Unexpected I/O error", ioe);
            }
        };
    }

    private static HttpClient.Builder setupProxy(HttpClient.Builder builder, URL url, Properties properties) {
        if (getBoolean("use.proxy", properties)) {
            if (getBoolean("http.proxy", properties)) {
                setIfUndefined("http.proxyHost", properties, "");
                setIfUndefined("http.proxyPort", properties, "80");
                setIfUndefined("http.nonProxyHosts", properties, "localhost|127.*|[::1]");
            }
            if (getBoolean("https.proxy", properties)) {
                setIfUndefined("https.proxyHost", properties, "");
                setIfUndefined("https.proxyPort", properties, "443");
                setIfUndefined("http.nonProxyHosts", properties, "localhost|127.*|[::1]");
            }
            if (getBoolean("socks.proxy", properties)) {
                setIfUndefined("socksProxyHost", properties.getProperty("socks.proxyHost", ""));
                setIfUndefined("socksProxyPort", properties.getProperty("socks.proxyPort", "1080"));
            }

            int port = url.getPort();
            if (port == -1)
                port = url.getDefaultPort();
            return builder.proxy(ProxySelector.of(new InetSocketAddress(url.getHost(), port)));
        }

        return builder;
    }

    private static HttpClient.Builder setupAuthentication(HttpClient.Builder builder, URL url, Properties properties) {
        if (getBoolean("http.proxy", properties) || getBoolean("https.proxy", properties)) {
            return builder.authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    String protocol = url.getProtocol().toLowerCase();
                    String username = properties.getProperty(protocol + ".proxyUser", "");
                    String password = properties.getProperty(protocol + ".proxyPassword", "");
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        }
        else if (getBoolean("socks.proxy", properties) && getBoolean("socks.proxy.auth", properties)) {
            String username = properties.getProperty("socks.proxyUser", "");
            String password = properties.getProperty("socks.proxyPassword", "");
            System.setProperty("java.net.socks.username", username);
            System.setProperty("java.net.socks.password", password);
            return builder.authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });

        }

        return builder;
    }

    /**
     * Sets a System property if the given key is not defined.
     */
    private static void setIfUndefined(String key, Properties properties, String defaultValue) {
        if (null == System.getProperty(key)) {
            System.setProperty(key, properties.getProperty(key, defaultValue));
        }
    }

    /**
     * Sets a System property if the given key is not defined.
     */
    private static void setIfUndefined(String key, String value) {
        if (null == System.getProperty(key)) {
            System.setProperty(key, value);
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

    /**
     * Resolves the filename extension.
     * If the supplied {@code fileName} has an extension, i.e, a suffix matching {@code .\\w} then returns {@code null},
     * else inspect the headers for "Content-Type" and find matching extension from the available {@code LayersConfigParser}s.
     */
    private static String resolveFileExtension(String fileName, HttpHeaders headers) {
        int dot = fileName.lastIndexOf('.');
        if (dot > -1) {
            // no need to guess the file extension
            // we assume it's the correct one
            return null;
        }

        String contentType = headers.firstValue("Content-Type").orElse("text/plain");
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
        }
        catch (IllegalArgumentException | NullPointerException e) {
            // ignored
        }
        return false;
    }

    static int getInteger(String name, Properties properties, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(name));
        }
        catch (NumberFormatException | NullPointerException e) {
            // ignored
        }
        return defaultValue;
    }

    static long getLong(String name, Properties properties, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(name));
        }
        catch (NumberFormatException | NullPointerException e) {
            // ignored
        }
        return defaultValue;
    }
}
