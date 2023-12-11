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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class PathUtil {
    /**
     * Private constructor for utility class 'PathUtil'
     */
    private PathUtil() {
    }

    /**
     * Splits path for segments using either UNIX or Win separators
     *
     * @param sPath path to split
     * @return array of path segments
     * @throws IllegalArgumentException
     * @pre sPath != null
     * @post $result != null
     */
    public static String[] splitPath(String sPath) {
        return splitPath(sPath, "/\\"); //$NON-NLS-1$
    }

    /**
     * Splits path for segments using given separators
     *
     * @param sPath
     * @param sSeparator
     * @return
     * @throws IllegalArgumentException
     */
    public static String[] splitPath(String sPath, String sSeparator)
            throws IllegalArgumentException {
        if (sPath == null) {
            return null;
        }
        if ((sSeparator == null) || (sSeparator.length() < 1)) {
            throw new IllegalArgumentException("Missing separator."); //$NON-NLS-1$
        }
        StringTokenizer st = new StringTokenizer(sPath, sSeparator);
        List<String> tokenList = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String sToken = st.nextToken();
            tokenList.add(sToken);
        }
        String[] aToken = new String[tokenList.size()];
        return tokenList.toArray(aToken);
    }

    /**
     * Converts <code>path</code> so that it contains '/' as separator
     *
     * @param path
     * @return
     */
    public static String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        path = path.replace('\\', '/');
        path = path.replace("//", "/"); //$NON-NLS-1$ //$NON-NLS-2$
        return path;
    }

    /**
     * Returns true if the file system is case-insensitive (currently windows or mac)
     * @return
     * @author birdo
     */
    public static boolean caseInsensitiveFileSystem () {
        return ArchUtil.archIsWindows() || ArchUtil.archIsMacOSX();
    }
}