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

import java.util.*;

import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.testableinput.LocationUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.parasoft.findings.utils.results.testableinput.ITestableInput;
import com.parasoft.findings.utils.results.testableinput.PathInput;

public class LegacyResultLocationsReader extends DefaultHandler implements IResultLocationsReader {
    private final ITestableInputLocationMatcher _locationMatcher;

    private Map<String, Properties> _locations = null;

    public LegacyResultLocationsReader(ITestableInputLocationMatcher locationMatcher) {
        _locationMatcher = locationMatcher;
        _locations = new HashMap<String, Properties>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (IXmlTagsAndAttributes.LOCATIONS_TAG.equals(qName)) {
            // nothing to do
        } else if (IXmlTagsAndAttributes.LOCATION_TAG.equals(qName)) {
            String loc = attributes.getValue(IXmlTagsAndAttributes.LOC_ATTR);
            Properties storedLocation = LocationUtil.readStoredLocation(attributes);
            _locations.put(loc, storedLocation);
        } else {
            throw new SAXException(ILLEGAL_TAG_MESSAGE + qName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (IXmlTagsAndAttributes.LOCATIONS_TAG.equals(qName)) {
            // nothing to do
        } else if (IXmlTagsAndAttributes.LOCATION_TAG.equals(qName)) {
            // nothing to do
        } else {
            throw new SAXException(ILLEGAL_TAG_MESSAGE + qName);
        }
    }

    public Properties getStoredLocation(String loc) {
        if (_locations == null) {
            return null;
        }
        return _locations.get(loc); // parasoft-suppress BD.OPT.INEFMAP "Reviewed, not modifying public methods to obtain iterable elements."
    }

    public ResultLocation getResultLocation(String loc, SourceRange sourceRange, boolean bAcceptModified)
            throws LocationsException {
        ITestableInput input = getTestableInput(loc, bAcceptModified);
        if (input == null) {
            return null;
        }
        return new ResultLocation(input, sourceRange);
    }

    public ITestableInput getTestableInput(String loc, boolean bAcceptModified)
            throws LocationsException {
        Properties storedLocation = getStoredLocation(loc);
        if (storedLocation == null) {
            return new PathInput(loc);
        }
        return _locationMatcher.matchLocation(storedLocation, bAcceptModified);
    }

    public ITestableInputLocationMatcher getLocationMatcher() {
        return _locationMatcher;
    }

    private static final String ILLEGAL_TAG_MESSAGE = "Tag with illegal name spotted:"; //$NON-NLS-1$
}
