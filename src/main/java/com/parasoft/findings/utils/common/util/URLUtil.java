/*
 * Copyright 2023 Parasoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parasoft.findings.utils.common.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods for URLs.
 */
public final class URLUtil {
    /**
     * Constructor : URLUtil
     */
    private URLUtil() {
    }

    /**
     * Converts a string representing an URL or a file path into a URL object.
     * Returned URL has proper escaping e.g ' ' are replaced with '%20'.
     *
     * @param sUrl
     * @return URL
     */
    public static URL toURL(String sUrl) {
        try {
            URL url = new URL(sUrl);
            URI uri = new URI(url.getProtocol(),
                    null,
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    null,
                    null);
            return uri.toURL();
        } catch (MalformedURLException e) {
            // ignore exception
        } catch (URISyntaxException e) {
            // ignore exception
        }
        if (!sUrl.contains(URL_SEPARATOR)) {
            return makeFromPath(sUrl);
        }
        return null;
    }

    /**
     * @param path local file or directory path
     * @return URL representation of given local path
     */
    public static URL makeFromPath(String path) {
        if (path == null) {
            return null;
        }
        try {
            File file = new File(path);
            URI uri = file.toURI();
            return uri.toURL();
        } catch (MalformedURLException mue) {
            // ignore exception
            return null;
        }
    }

    /**
     * @param url
     * @return local file based on given url, null if url does not represent a local file
     */
    public static File getLocalFile(URL url) {
        if (url == null) {
            return null;
        }
        if (!url.getProtocol().startsWith(FILE)) {
            return null;
        }

        return URLUtil.toFile(url);
    }

    /**
     * Returns a file corresponding to a file:// URL. The result from passing
     * another URL to this method is unpredicatble and likely garbage.
     *
     * @param url a file URL
     * @return the corresponding file
     * @pre isFileProtocol(url)
     */
    public static File toFile(URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException use) {
            return new File(getPath(url));
        }
    }

    public static String getPath(URL url) {
        try {
            return URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
        } catch (Throwable thr) {
            Logger.getLogger().error("An exception is thrown durind decode URL. The uncoded path will be returned", thr); //$NON-NLS-1$
            return url.getFile();
        }
    }

    private static final String FILE = "file"; //$NON-NLS-1$

    private static final String URL_SEPARATOR = "://"; //$NON-NLS-1$
}