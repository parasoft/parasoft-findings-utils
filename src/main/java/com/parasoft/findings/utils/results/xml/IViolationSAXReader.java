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

import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.location.IResultLocationsReader;

/**
 * Interface for violation readers
 */
public interface IViolationSAXReader
        extends IResultSAXReader {
    /**
     * This method should return a concrete IViolation readed by this reader.
     *
     * @return a concreate IResult readed by this reader.
     */
    IViolation getReadViolation();

    /**
     * Sets the locations data to use
     *
     * @param //javadoc-ref//locationsReader
     */
    void setLocations(IResultLocationsReader locations);

}