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

package com.parasoft.findings.utils.results.xml;

import com.parasoft.findings.utils.results.location.IResultLocationsReader;
import org.xml.sax.ContentHandler;

import com.parasoft.findings.utils.results.violations.IViolation;

/**
 * Interface for a sax reader of a group of results.
 */
public interface IViolationsSAXReader
        extends ContentHandler {

    /**
     * Gets the next violation read from the report.
     *
     * @return the next violation from the report or <code>null</code> if not available
     */
    IViolation getNextViolation();


    /**
     * Sets if getReadViolations should return only those violations with unchanged hash
     *
     * @param bEnabled new flag state
     */
    void setResourceHashVerifification(boolean bEnabled);

    /**
     * Sets the locations data to use
     *
     * @param locationsProvider
     */
    void setLocations(IResultLocationsReader locationsProvider);

    /**
     * Sets the parent violations reader.
     *
     * @param parentViolationsReader the parent violations reader
     */
    void setParentReader(XmlReportReader parentViolationsReader);

}