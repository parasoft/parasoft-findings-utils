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

package com.parasoft.findings.utils.common.nls;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Loads resources with use of classloader of given class.
 */
public class ClasspathResourceLoader
        implements IntlResourceProvider.IResourceLoader {
    private ClassLoader _classloader;

    public ClasspathResourceLoader(Class<?> classs) {
        _classloader = classs.getClassLoader();
        if (_classloader == null) {
            _classloader = ClassLoader.getSystemClassLoader();
        }
    }

    @Override
    public InputStream getResource(String path) {
        path = validatePath(canonicalize(path));
        try {
            return _classloader.getResourceAsStream(path);
        } catch (NullPointerException e) { // XT-37381
            Logger.getLogger().warn("NPE while trying to get resource " + path, e); //$NON-NLS-1$
            return null;
        }
    }

    private String validatePath(String path) {
        if ((path != null) && path.startsWith("/")) { //$NON-NLS-1$
            path = path.substring(1);
        }
        return path;
    }

    // Resolve the OWASP2021.A3.CDBV violation and normalize the parameters
    private String canonicalize(String prevalidatedStr) {
        if (prevalidatedStr != null) {
            return new String(prevalidatedStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return null;
    }
}
