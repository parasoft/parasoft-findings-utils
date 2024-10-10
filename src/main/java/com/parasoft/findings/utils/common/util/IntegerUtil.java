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

/**
 * Utility methods for integers.
 */
public final class IntegerUtil {
    /**
     * Constructor.
     */
    private IntegerUtil() {
    }

    /**
     * utility method to parse integer value, which avoid NumberFormatException. If exception
     * thrown, by default return <code>-1</code>
     *
     * @param value
     * @return
     */
    public static int parseInt(String value) {
        return parseInt(value, -1);
    }

    /**
     * utility method to parse integer value, which avoid NumberFormatException. If exception
     * thrown, return the value that you pass in the method
     *
     * @param sValue
     * @param defaultValue return value if exception thrown
     * @return the integer value represented by the argument or default_value if string doesn't
     * contain a parsable integer.
     */
    public static int parseInt(String sValue, int defaultValue) {
        if (StringUtil.isEmpty(sValue)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(sValue);
        } catch (Exception exc) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to ensure exceptions during int value parsing don't cause the process to fail."
            Logger.getLogger().info("Could not parse int value from: " + sValue); //$NON-NLS-1$
        }
        return defaultValue;
    }

    /**
     * @param left
     * @param right
     * @return Result of integer numerical comparison. The implementation is same as in
     * {@link Integer#compareTo}.
     */
    public static int compare(int left, int right) {
        return left < right ? -1 : (left == right ? 0 : 1);
    }
}