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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class loads resources from configured locations, looking for a specified set of language suffixes.
 */
public class IntlResourceProvider {
    private final String[] _suffixes;

    private IResourceLoader[] _loaders = null;
    private final String _extension = null;

    /**
     * Provides an instance which will search among classpath resources only.
     *
     * @param suffixes
     * @param classs   owner of classloader to load resources with
     * @return new instance
     */
    public static IntlResourceProvider forClasspathResources(String[] suffixes, Class<?> classs) {
        return new IntlResourceProvider(suffixes, new ClasspathResourceLoader(classs));
    }

    /**
     * @param suffixes file suffixes to search when looking for resources,
     *                 eg: { "_zh_CN.properties", "_zh.properties", ".properties" }
     * @param loaders  ordered set of resource loaders
     */
    public IntlResourceProvider(String[] suffixes, IResourceLoader... loaders) {
        _suffixes = suffixes;
        _loaders = loaders;
    }

    /**
     * Provide all existing resources, starting with most matching language.
     *
     * @param fullName resource full path, name without suffixes
     * @return all resources in all matching language variants or empty
     */
    public InputStream[] getResources(String fullName) {
        List<InputStream> result = new ArrayList<InputStream>();
        String[] pathVariants = getNameVariants(fullName);
        for (String pathVariant : pathVariants) {
            InputStream input = getResourceStreamFromLoader(pathVariant);
            if (input != null) {
                result.add(input);
            }
        }

        return result.toArray(new InputStream[result.size()]);
    }


    private String[] getNameVariants(String fullName) {
        if (_extension != null) {
            return IntlUtil.buildVariants(fullName, _suffixes, _extension);
        }
        return IntlUtil.buildVariants(fullName, _suffixes);
    }

    private InputStream getResourceStreamFromLoader(String pathVariant) {
        for (IResourceLoader loader : _loaders) {
            try {
                InputStream result = loader.getResource(pathVariant);
                if (result != null) {
                    return result;
                }
            } catch (IOException e) {
                Logger.getLogger().error(e);
            }
        }
        return null;
    }

    public interface IResourceLoader {
        /**
         * @param path
         * @return stream to an existing resource at given full path or null
         * @throws IOException
         */
        InputStream getResource(String path)
                throws IOException;

    }

}