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

    /** Buffer size used for input and output streams */
    private final static int BUFFER_SIZE = 32768;

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

    /**
     * Copies directory structure (note - structures cannot overlap)
     * @param sourceDir source directory
     * @param destinationDir parent directory
     * @throws IOException thrown when problems during copy
     *
     * @pre sourceDir != null
     * @pre destinationDir != null
     */
    public static void recursiveCopy(File sourceDir, File destinationDir)
            throws IOException
    {
        if (sourceDir.equals(destinationDir)) {
            Logger.getLogger().warn("Trying to copy directory onto itself!"); //$NON-NLS-1$
            return;
        }

        if (overlaps(sourceDir.getAbsolutePath(), destinationDir.getAbsolutePath())) {
            throw new IOException("Cannot copy from " + sourceDir.getAbsolutePath() + " to: " + destinationDir.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        IOUtils.mkdirs(destinationDir.getAbsolutePath());

        String[] asChildren = sourceDir.list();
        for (int i = 0; (asChildren != null) && (i < asChildren.length); i++) {
            File srcChild = new File(sourceDir, asChildren[i]);
            File destChild = new File(destinationDir, asChildren[i]);
            if (srcChild.isFile()) {
                copyFile(srcChild, destChild);
            } else {
                recursiveCopy(srcChild, destChild);
            }
        }
    }

    /**
     * Checks if path2 is a child of path1
     * @param path1 the first path (root)
     * @param path2 the path to check
     * @return true when path2 belongs to path1 directory structure
     *
     * @pre path1 != null
     * @pre path2 != null
     */
    public static boolean overlaps(String path1, String path2)
    {
        String[] asPath1 = PathUtil.splitPath(path1);
        String[] asPath2 = PathUtil.splitPath(path2);

        if (asPath2.length <= asPath1.length) { // parasoft-suppress BD.EXCEPT.NP "contract that path1 and 2 cannot be null"
            return false;
        }
        boolean bIgnoreCase = PathUtil.caseInsensitiveFileSystem();
        boolean bOverlaps = true;
        for (int i = 0; i < asPath1.length; i++) {
            if (bIgnoreCase) {
                bOverlaps = asPath1[i].equalsIgnoreCase(asPath2[i]);
            } else {
                bOverlaps = asPath1[i].equals(asPath2[i]);
            }
            if (!bOverlaps) {
                break;
            }
        }
        return bOverlaps;
    }

    /**
     * Copies file from location <code>source</code> path
     * unter <code>destination</code> location.
     * @param source source file (should not be a directory)
     * @param destination destination file (should not be a directory)
     * @throws IOException when I/O error occurs
     *
     * @pre source != null && destination != null
     **/
    public static void copyFile(File source, File destination)
            throws IOException
    {
        if (source.equals(destination)) {
            Logger.getLogger().warn("Trying to copy file onto itself!"); //$NON-NLS-1$
            return;
        }

        FileInputStream in = null;
        FileOutputStream out = null;
        BufferedOutputStream bufferedOut = null;
        BufferedInputStream bufferedIn = null;

        try {
            in = new FileInputStream(source);
            bufferedIn = new BufferedInputStream(in, BUFFER_SIZE);
            out = new FileOutputStream(destination);
            bufferedOut = new BufferedOutputStream(out, BUFFER_SIZE);
            byte[] aBuffer = new byte[BUFFER_SIZE];

            int numOfBytes = 0;
            while ((numOfBytes = bufferedIn.read(aBuffer)) != -1) { // parasoft-suppress OPT.CEL "reviewed"
                bufferedOut.write(aBuffer, 0, numOfBytes);
            }

        } finally {
            close(bufferedIn);
            close(bufferedOut);
            close(in);
            close(out);
        }
    }

    /**
     * Delete given path.
     * If it is a file - remove sole file.
     * If it is a directory - remove it along with all removable contents.
     * @param root what to delete
     *
     * @pre root != null
     */
    public static boolean recursiveDelete(File root)
    {
        if (!root.exists()){
            return true;
        }

        if (root.isDirectory()){
            File [] listing = root.listFiles();
            for (File element : listing) {
                recursiveDelete(element);
            }
        }

        return root.delete();
    }

    /**
     * Method close output stream
     * @param is input stream which need to be closed
     */
    private static void close(InputStream is)
    {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException exc) { // parasoft-suppress SECURITY.UEHL.LGE "acceptable"
            // do nothing
        }
    }

    /**
     * Method flush and close output stream
     * @param os output stream which need to be closed.
     */
    private static void close(OutputStream os)
    {
        if (os == null) {
            return;
        }
        try {
            os.flush();
        } catch (IOException exc) { // parasoft-suppress SECURITY.UEHL.LGE "acceptable"
            // do nothing
        }
        try {
            os.close();
        } catch (IOException exc) { // parasoft-suppress SECURITY.UEHL.LGE "acceptable"
            // do nothing
        }
    }
}