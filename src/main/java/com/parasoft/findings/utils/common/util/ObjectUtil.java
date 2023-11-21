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
 * Utility methods for Java objects.
 */
public final class ObjectUtil {

    /**
     * Constructor.
     */
    private ObjectUtil() {
    }

    /**
     * Returns whether the two parameters are equal, assuming that a non-<code>null</code>
     * object cannot be equal to <code>null</code>.
     *
     * @param obj1 object 1 or <code>null</code>
     * @param obj2 object 2 or <code>null</code>
     * @return <code>true</code> iff both objects are null or they are both
     * non-null and equal
     */
    public static boolean equals(Object obj1, Object obj2) {
        return ((obj1 == obj2) || // parasoft-suppress PB.CUB.UEIC FE.UEIC "Using '==' instead of 'equals()' is expected here"
                ((obj1 != null) && obj1.equals(obj2)));
    }

    /**
     * @param object Can be null.
     * @return Hash code for the object.
     */
    public static int hashCode(Object object) {
        return object != null ? object.hashCode() : 31;
    }
}