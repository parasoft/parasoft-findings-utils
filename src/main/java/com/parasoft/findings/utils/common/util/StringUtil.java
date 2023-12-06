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

import com.parasoft.findings.utils.common.IStringConstants;

/**
 * String - related utilities
 */
public final class StringUtil {

    /**
     * Private constructor for utility class 'StringUtil'
     */
    private StringUtil() {
        super();
    }

    /**
     * get the newline character based on the system (ie '\n', '\r', '\r\n'
     *
     * @return String - newline character
     */
    public static String getLineSeparator() {
        String default_nl = System.getProperty("line.separator"); //$NON-NLS-1$
        if (default_nl == null) {
            default_nl = IStringConstants.CHAR_LINEFEED; // ok : by default
        }
        return default_nl;
    }

    /**
     * Checks if any of given strings string is null or trimmed value is zero-length
     *
     * @param strings
     * @return
     */
    public static boolean isAnyEmptyTrimmed(String... strings) {
        for (String string : strings) {
            if ((string == null) || (string.trim().length() == 0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a string is null or zero-length
     *
     * @param string or <code>null</code>
     * @return whether string is null or zero-length
     * @concurrency concurrent
     * @post $result || ((sText != null) && (sText.length() > 0))
     */
    public static boolean isEmpty(String string) {
        return (string == null) || (string.length() == 0);
    }

    /**
     * Checks if a string is null or trimmed value is zero-length
     *
     * @param string or <code>null</code>
     * @return <code>true</code> string is null or trimmed value is zero-length
     * @concurrency concurrent
     */
    public static boolean isEmptyTrimmed(String string) {
        return isAnyEmptyTrimmed(string);
    }

    /**
     * Checks if a string is not-null and not zero-length
     *
     * @param string or <code>null</code>
     * @return <code>true</code> string is not-null and not zero-length
     * @concurrency concurrent
     */
    public static boolean isNonEmpty(String string) {
        return (string != null) && (string.length() > 0);
    }

    /**
     * Checks if a string is not-null and trimmed value is not zero-length
     *
     * @param string or <code>null</code>
     * @return <code>true</code> string is not-null and trimmed value is not zero-length
     * @concurrency concurrent
     */
    public static boolean isNonEmptyTrimmed(String string) {
        return (string != null) && (string.trim().length() > 0);
    }


    /**
     * Checks if given String objects are equal. Returns <code>true</code> if
     * <code>sText1.equals(str2)</code> or both <code>sText1</code> and <code>sText2</code> are
     * <code>null</code>; false otherwise.
     *
     * @param sText1 string to compare or null
     * @param sText2 string to compare or null
     * @return <code>true</code> if given strings are equal.
     */
    public static boolean equals(String sText1, String sText2) {
        if (sText1 == sText2) { // parasoft-suppress PB.CUB.UEIC "Expected way to compare"
            return true;
        }
        return (sText1 != null) && sText1.equals(sText2);
    }

    /**
     * Returns the same string or empty string if <code>null</code> is passed.
     *
     * @param string source string
     * @return not null string
     * @post $result != null
     */
    public static String getNonEmpty(String string) {
        return string == null ? "" : string;
    }
}