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

    /** Buffer size used for input and output streams */
    private final static int COPY_BUFFER_SIZE = 32768;

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
     * Closes an <code>OutputStream</code> writing error message to the log in case
     * of any problems.
     * @param out The stream to close, allowed to be null. In the latter case
     *    nothing happens.
     */
    public static void close(final OutputStream out)
    {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            Logger.getLogger().error("Error while closing output stream", ex); //$NON-NLS-1$
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

    /**
     * mkdirs. This method will create the directory specified by the path
     * (including any necessary parent directories). If the directory already
     * exists, it will just be returned.
     *
     * @param path -
     *            the path to the directory that you would like to create
     * @return the directory that was successfully created, the directory that
     *         already exists (if the directory already exists), or
     *         <code>null</code> if the path passed in is null or the
     *         directory could not be created
     */
    public static File mkdirs(String path)
    {
        // makes the directories needed in path
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            return file;
        }
        if (!file.mkdirs()) {
            return null;
        }
        return file;
    }

    /**
     * Copies data from specified source stream to destination stream using default (32768 bytes) buffer size.
     * @param source the source stream
     * @param destination the destination stream
     * @throws IOException if operation fails due to an IO error
     *
     * @pre source != null
     * @pre destination != null
     */
    public static void copy(InputStream source, OutputStream destination)
            throws IOException
    {
        copy(source, destination, COPY_BUFFER_SIZE);
    }

    /**
     * Copies data from specified source stream to destination stream.
     * @param source the source stream
     * @param destination the destination stream
     * @param bufferSize the size of buffer to use
     * @throws IOException if operation fails due to an IO error
     *
     * @pre source != null
     * @pre destination != null
     * @pre bufferSize > 0
     */
    public static void copy(InputStream source, OutputStream destination, int bufferSize)
            throws IOException
    {
        BufferedInputStream bufferedSource = null;
        BufferedOutputStream bufferedDestination = null;

        try {
            bufferedSource = new BufferedInputStream(source, bufferSize);
            bufferedDestination = new BufferedOutputStream(destination, bufferSize);
            byte[] aBuffer = new byte[bufferSize];

            int numOfBytes = 0;
            while ((numOfBytes = bufferedSource.read(aBuffer)) != -1) {
                bufferedDestination.write(aBuffer, 0, numOfBytes);
            }

        } finally {
            close(bufferedSource);
            close(bufferedDestination);
            close(source);
            close(destination);
        }
    }
}