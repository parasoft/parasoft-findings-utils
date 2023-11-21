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


package com.parasoft.findings.utils.results.violations;

/**
 * Represents entity that allows easy decoration with additional attributes.
 * The decoration should be performed mostly by <code>IResultPostProcessorService</code>
 * and used by <code>IResultReporterService</code>
 */
public interface IAttributedEntity {
    /**
     * Decorate result with a new attribute.
     *
     * @param sName  the attribute key, cannot be <code>null</code>
     * @param sValue the attribute value, can be <code>null</code>
     * @pre sName != null
     */
    void addAttribute(String sName, String sValue);

    /**
     * Return attribute value for specified name.
     *
     * @param sName the attribute name
     * @return String with value
     */
    String getAttribute(String sName);
}