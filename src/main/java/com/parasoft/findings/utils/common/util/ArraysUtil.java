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
 * A Class holding extended static algorithms for arrays. You just might find
 * something useful if you look. For those that long for LISP.
 */
public final class ArraysUtil {

    /**
     * Automatically generated constructor for utility class
     */
    private ArraysUtil() {
    }

    /**
     * @param array
     * @param element
     * @return <code>true</code> if the given array contains the given element
     * and <code>false</code> otherwise. Algorithm uses equality comparison.
     * @pre array != null
     */
    public static boolean contains(Object[] array, Object element) {
        return ArraysUtil.indexOf(array, element) != -1;
    }

    /**
     * Searches for the first occurrence of element in the given array using equality comparison.
     *
     * @param array
     * @param element
     * @return the index of the first occurrence of element in the given array or
     * <tt>-1</tt> if element is not found.
     * @pre array != null
     * @post ($result > = - 1) && ($result < array.length)
     */
    public static int indexOf(Object[] array, Object element) {
        for (int i = 0; i < array.length; i++) {
            if (ObjectUtil.equals(element, array[i])) {
                return i;
            }
        }
        return -1;
    }
}