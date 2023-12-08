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


import java.io.*;

/**
 * A bunch of utility functions for various I/O related applications.
 **/
public final class IOUtils {
    /**
     * Private constructor for utility class
     */
    private IOUtils() {
    }

    /**
     * Closes an <code>InputStream</code> writing error message to the log in case
     * of any problems.
     *
     * @param in The stream to close, allowed to be null. In the latter case
     *           nothing happens.
     */
    public static void close(final InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ioe) {
            Logger.getLogger().error("Error while closing input stream.", ioe); //$NON-NLS-1$
        }
    }

    /**
     * Closes a <code>Reader</code> writing error message to the log in case
     * of any problems.
     *
     * @param in The stream to close, allowed to be null. In the latter case
     *           nothing happens.
     */
    public static void close(final Reader in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            Logger.getLogger().error("Error while closing input stream.", ex); //$NON-NLS-1$
        }
    }

    /**
     * Closes a <code>Writer</code> writing error message to the log in case
     * of any problems.
     *
     * @param out The stream to close, allowed to be null. In the latter case
     *            nothing happens.
     */
    public static void close(final Writer out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            Logger.getLogger().error("Error while closing output stream.", ex); //$NON-NLS-1$
        }
    }

}