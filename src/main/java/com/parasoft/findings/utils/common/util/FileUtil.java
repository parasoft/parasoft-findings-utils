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
import java.util.*;

import com.parasoft.findings.utils.common.IStringConstants;

public final class FileUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private FileUtil() {
    }

    /**
     * Gets file name without extension.
     *
     * @param file the file instance
     * @return the name of file without extension
     */
    public static String getNoExtensionName(File file) {
        if (file != null) {
            return getNoExtensionName(file.getName());
        }
        return null;
    }

    /**
     * Return all files within giver dir with such name, no matter what is the file extension
     *
     * @param dir
     * @param sName
     * @return
     * @pre dir != null
     * @pre sName != null
     * @post $result != null
     */
    public static File[] listFilesByName(File dir, final String sName) {
        return dir.listFiles(new FileFilter() {

            public boolean accept(File child) {
                if (!child.isFile()) {
                    return false;
                }
                String sChildName = FileUtil.getNoExtensionName(child);
                return sChildName.equals(sName); // parasoft-suppress BD.EXCEPT.NP "bogus"
            }
        });
    }

    /**
     * Gets file name without extension.
     *
     * @param sFileName the full file name
     * @return the name of file without extension
     */
    public static String getNoExtensionName(String sFileName) {
        if (sFileName != null) {
            int iLastDotIndex = sFileName.lastIndexOf("."); //$NON-NLS-1$
            if ((iLastDotIndex > 0) && (iLastDotIndex < (sFileName.length() - 1))) {
                return sFileName.substring(0, iLastDotIndex);
            }
            return sFileName;
        }
        return null;
    }

    /**
     * Reads file contents to given lines list
     *
     * @param file  file to read
     * @param lines output lines list
     * @throws IOException
     */
    public static void readFile(File file, List<String> lines)
            throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file)); // parasoft-suppress INTER.SEO "Want to use default encoding"
        try {
            String sLine = reader.readLine();
            while (sLine != null) {
                lines.add(sLine);
                sLine = reader.readLine();
            }
        } finally {
            IOUtils.close(reader);
        }
    }

    /**
     * Reads file contents and returns it as a string with specified encoding
     *
     * @param file      file to read
     * @param sEncoding encoding file, example: UTF-8
     * @return file content
     * @throws IOException
     */
    public static String readFile(File file, String sEncoding)
            throws IOException {
        String sContentFile = IStringConstants.EMPTY;

        FileInputStream fileStream = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;

        try {
            fileStream = new FileInputStream(file);
            streamReader = new InputStreamReader(fileStream, sEncoding);
            reader = new BufferedReader(streamReader);
            sContentFile = readFile(reader);
        } finally {
            IOUtils.close(fileStream);
        }
        return sContentFile;
    }

    /**
     * Reads contents from BufferedReader and returns it as a string
     * Note that this method do not close BufferedReader.
     *
     * @param reader
     * @return file content
     * @throws IOException
     */
    public static String readFile(BufferedReader reader)
            throws IOException {
        StringBuffer buffer = new StringBuffer();
        char[] aBuffer = new char[1024];
        int len = reader.read(aBuffer, 0, aBuffer.length);
        while (len > 0) {
            buffer.append(String.valueOf(aBuffer, 0, len));
            len = reader.read(aBuffer, 0, aBuffer.length);
        }
        return buffer.toString();
    }

}