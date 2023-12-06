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

package com.parasoft.findings.utils.rules;

public final class CategoryDescription
{
    private final String _sCategoryId;
    private final char _separator;
    private String _sDescription = null;

    /**
     * @param sCategoryId
     * @param sDescription
     * @param separator
     *
     * @pre sCategoryId != null
     * @pre sDescription != null
     */
    public CategoryDescription(String sCategoryId, String sDescription, char separator)
    {
        _sCategoryId = sCategoryId;
        _sDescription = sDescription;
        _separator = separator;
    }

    public String getCategoryId()
    {
        return _sCategoryId;
    }

    @Override
    public String toString()
    {
        return "Category: " + getCategoryId(); //$NON-NLS-1$
    }

    public static final String UNKNOWN_PATH = "UNKNOWN";  //$NON-NLS-1$
    public static final String UNKNOWN_CATEGORY = "UNKNOWN";  //$NON-NLS-1$
}