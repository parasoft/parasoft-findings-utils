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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import com.parasoft.findings.utils.results.testableinput.LocationUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LocationsReader extends DefaultHandler {
    private final Map<String, String> _repositoriesMapping;

    private final Map<String, Properties> _locations;

    public LocationsReader() {
        this(null);
    }

    public LocationsReader(Map<String, String> repositoriesMapping) {
        _locations = new HashMap<String, Properties>();
        _repositoriesMapping = repositoriesMapping;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (IXmlTagsAndAttributes.LOCATIONS_TAG.equals(qName)) {
            // nothing to do
        } else if (IXmlTagsAndAttributes.LOCATION_TAG.equals(qName)) {
            String sLocRef = attributes.getValue(IXmlTagsAndAttributes.LOC_REF_ATTR);
            if (sLocRef == null) {
                throw new SAXException("Location ref attribute not found."); //$NON-NLS-1$
            }
            String sReadRepRef = attributes.getValue(IXmlTagsAndAttributes.REP_REF_ATTR);
            String sRepRef = null;
            if ((_repositoriesMapping != null) && (sReadRepRef != null)) {
                sRepRef = _repositoriesMapping.get(sReadRepRef);
                if (sRepRef == null) {
                    Logger.getLogger().error("Ref matching failed for repository mappings: "); //$NON-NLS-1$
                    for (Map.Entry<String, String> entry : _repositoriesMapping.entrySet()) {
                        Logger.getLogger().error("    " + entry.getKey() + " -> " + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    throw new SAXException("Repository ref not matched: " + sReadRepRef); //$NON-NLS-1$
                }
            }
            Properties storedLocation = LocationUtil.readStoredLocation(attributes);
            if (sRepRef != null) {
                storedLocation.setProperty(IXmlTagsAndAttributes.REP_REF_ATTR, sRepRef);
            }
            _locations.put(sLocRef, storedLocation);
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

    public Properties getStoredLocation(String sLocRef) {
        return _locations.get(sLocRef); // parasoft-suppress BD.OPT.INEFMAP "Reviewed, not modifying public methods to obtain iterable elements."
    }

    private static final String ILLEGAL_TAG_MESSAGE = "Tag with illegal name spotted:";  //$NON-NLS-1$

}
