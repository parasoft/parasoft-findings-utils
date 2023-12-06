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

public final class RuleUtil
{
    private RuleUtil() {}


    /**
     * @param sId rule or category id
     * @param separator
     * @return id or null if there is no parent in give id
     */
    public static String getParentId(String sId, char separator)
    {
        if (sId == null) {
            return null;
        }
        int index = sId.lastIndexOf(separator);
        if (index <= 0) {
            return null;
        }
        return sId.substring(0, index);
    }

}