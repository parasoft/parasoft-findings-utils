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

import java.util.HashMap;
import java.util.Map;

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

    private final static Map<Character, String> JAVA_ESCAPE = new HashMap<Character, String>();

    static {
        JAVA_ESCAPE.put('\0', "\\0");
        for (int i = 0x1; i < 0x7; i++) {
            JAVA_ESCAPE.put((char) i, "\\" + charToThreeDigitOctal((char) i)); // $NON-NLS-1$
        }
        JAVA_ESCAPE.put('\b', "\\b"); // $NON-NLS-1$
        JAVA_ESCAPE.put('\t', "\\t"); // $NON-NLS-1$
        JAVA_ESCAPE.put('\n', "\\n"); // $NON-NLS-1$
        JAVA_ESCAPE.put((char) 0x0B, "\\" + charToThreeDigitOctal((char) 0x0B)); // $NON-NLS-1$
        JAVA_ESCAPE.put('\f', "\\f"); // $NON-NLS-1$
        JAVA_ESCAPE.put('\r', "\\r"); // $NON-NLS-1$
        for (int i = 0x0E; i <= 0x1F; i++) {
            JAVA_ESCAPE.put((char) i, "\\" + charToThreeDigitOctal((char) i)); // $NON-NLS-1$
        }
        JAVA_ESCAPE.put('\"', "\\\""); // $NON-NLS-1$
        JAVA_ESCAPE.put('\\', "\\\\"); // $NON-NLS-1$
        for (int i = 0x7F; i <= 0x9F; i++) {
            JAVA_ESCAPE.put((char) i, "\\u" + charToFourDigitHex((char) i)); // $NON-NLS-1$
        }
        JAVA_ESCAPE.put((char) 0x2028, "\\u2028"); // $NON-NLS-1$
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
        if (sText1 == sText2) {
            return true;
        }
        return (sText1 != null) && sText1.equals(sText2);
    }

    /**
     * Represent the numerical value of a char as a string of four hexadecimal digits.
     *
     * @param c The char to be represented in hexadecimal digits.
     * @return A string of four hexadecimal digits that represents c.
     * @post $result != null
     * @post $result.length () == 4
     * @post Integer.parseInt ($result, 16) >= 0
     * @post Integer.parseInt ($result, 16) < 65536
     */
    private static String charToFourDigitHex(char c) {
        StringBuilder result = new StringBuilder(Integer.toHexString(c).toUpperCase());
        while (result.length() < 4) { // parasoft-suppress OPT.CEL "Required for correct processing."
            result.insert(0, '0');
        }
        return result.toString();
    }

    /**
     * Represent the numerical value of a char as a string of three octal digits.
     *
     * @param c The char to be represented in octal digits.
     * @return A string of three octal digits that represents c.
     * @pre c < 256
     * @post $result != null
     * @post $result.length () == 3
     * @post Integer.parseInt ($result, 8) >= 0
     * @post Integer.parseInt ($result, 8) < 256
     */
    private static String charToThreeDigitOctal(char c) {
        StringBuilder result = new StringBuilder(Integer.toOctalString(c));
        while (result.length() < 3) { // parasoft-suppress OPT.CEL "Required for correct processing."
            result.insert(0, '0');
        }
        return result.toString();
    }

}