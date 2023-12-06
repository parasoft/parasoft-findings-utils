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

/**
 *
 * Methods that can detect various architectures
 *
 */
public final class ArchUtil {

    public final static String OS_NAME = "os.name"; //$NON-NLS-1$

    private ArchUtil () {}

    public static boolean archIsWindows () {
        return File.pathSeparatorChar == ';';
    }

    /**
     * Check if currently running on Mac OS X.
     * @return <code>true</code> if running on Mac OS X, else <code>false</code>
     */
    public static boolean archIsMacOSX() {

        return "Mac OS X".equals(System.getProperty(OS_NAME)); //$NON-NLS-1$
    }
}
