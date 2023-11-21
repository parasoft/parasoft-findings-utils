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

import java.util.*;

/**
 * Utility class for collections.
 */
public final class CollectionUtil {
    private CollectionUtil() {
        // Prevent instantiation
    }

    /**
     * Tells whether given collection is non-null and contains at least one element
     *
     * @param c collection
     * @return
     */
    public static <E> boolean isNonEmpty(Collection<E> c) {
        return hasAtLeastNElements(c, 1);
    }

    /**
     * Tells whether given collection is non-null and contains at least <tt>n</tt> element
     *
     * @param c collection
     * @param n
     * @return
     */
    public static <E> boolean hasAtLeastNElements(Collection<E> c, int n) {
        return (c != null) && (c.size() >= n);
    }
}