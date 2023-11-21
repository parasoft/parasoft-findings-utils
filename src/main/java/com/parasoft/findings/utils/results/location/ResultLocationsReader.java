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

package com.parasoft.findings.utils.results.location;

import java.util.Properties;

import com.parasoft.findings.utils.results.testableinput.DefaultLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.ITestableInput;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.violations.ResultLocation;

public class ResultLocationsReader
        extends LocationsReader
        implements IResultLocationsReader {
    private final ITestableInputLocationMatcher _locationMatcher;

    /**
     * @param locationMatcher
     */
    public ResultLocationsReader(ITestableInputLocationMatcher locationMatcher) {
        _locationMatcher = (locationMatcher == null) ? new DefaultLocationMatcher() : locationMatcher;
    }

    public ResultLocation getResultLocation(String sLocRef, SourceRange sourceRange, boolean bAcceptModified)
            throws LocationsException {
        ITestableInput input = getTestableInput(sLocRef, bAcceptModified);
        if (input == null) {
            return null;
        }
        return new ResultLocation(input, sourceRange);
    }

    public ITestableInput getTestableInput(String sLocRef, boolean bAcceptModified)
            throws LocationsException {
        Properties storedLocation = getStoredLocation(sLocRef);
        if (storedLocation == null) {
            return null;
        }
        return _locationMatcher.matchLocation(storedLocation, bAcceptModified);
    }

    public ITestableInputLocationMatcher getLocationMatcher() {
        return _locationMatcher;
    }
}
