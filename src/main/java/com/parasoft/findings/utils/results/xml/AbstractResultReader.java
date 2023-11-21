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

import java.util.*;

import com.parasoft.findings.utils.common.util.XMLUtil;
import com.parasoft.findings.utils.results.location.IResultLocationsReader;
import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.violations.PathElementAnnotation;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.violations.IPathElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractResultReader
        extends DefaultHandler
        implements IResultSAXReader {
    private IResultLocationsReader _locationsReader = null;

    /**
     * the name of the currently processed list
     */
    private String _sProcessedListName = null;

    /**
     * the contents of the currently processed list
     */
    private List<HashMap<String, String>> _processedList = null;

    /**
     * current violation element descriptor
     */
    private final LinkedList<Map<String, Object>> _descriptorDataStack = new LinkedList<Map<String, Object>>();

    /**
     * the stack of current ancestor violation element descriptors
     */
    private final LinkedList<List<IPathElement>> _violElemDescStack = new LinkedList<List<IPathElement>>();

    /**
     * the current properties
     */
    private LinkedHashMap<String, String> _curProperties = null;

    /**
     * the current annotations
     */
    private List<PathElementAnnotation> _curAnnotations = null;

    private final Map<String, Object> _resultData = new HashMap<String, Object>();

    @Override
    public void startElement(String sUri, String sLocalName, String sQName, Attributes attributes)
            throws SAXException {
        if (sQName.equals(_sProcessedListName)) {
            HashMap<String, String> map = new HashMap<String, String>();
            XMLUtil.processAttributesToMap(attributes, map);

            if (_processedList == null) {
                throw new SAXException("Invalid structure - processed list not initialized."); //$NON-NLS-1$
            }

            _processedList.add(map);
        } else if (sQName.endsWith(IXmlTagsAndAttributes.LIST_POSTFIX)) { // begin list reading
            if (sQName.startsWith(IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG)) {
                _violElemDescStack.addLast(new ArrayList<IPathElement>());
            } else {
                if (_sProcessedListName != null) {
                    throw new SAXException("Invalid structure - nested list detected!"); //$NON-NLS-1$
                }
                _sProcessedListName = sQName.substring(0, (sQName.length() - IXmlTagsAndAttributes.LIST_POSTFIX.length()));
                _processedList = new ArrayList<HashMap<String, String>>();
            }
        } else if (IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG.equals(sQName)) {
            Map<String, Object> descriptorData = new HashMap<String, Object>();
            Map<String, String> descriptorAttributes = new HashMap<String, String>();
            XMLUtil.processAttributesToMap(attributes, descriptorAttributes);
            descriptorData.put(_ATTRIBUTES_KEY, descriptorAttributes);
            _descriptorDataStack.addLast(descriptorData);
        } else if (IXmlTagsAndAttributes.PROPERTIES_V2_TAG.equals(sQName)) {
            _curProperties = new LinkedHashMap<String, String>();
        } else if (IXmlTagsAndAttributes.PROPERTY_V2_TAG.equals(sQName)) {
            if (_curProperties == null) {
                throw new SAXException(
                        "Invalid structure - property tag detected at wrong place"); //$NON-NLS-1$
            }
            String sKey = attributes.getValue(IXmlTagsAndAttributes.PROPERTY_KEY_ATTR);
            String sValue = attributes.getValue(IXmlTagsAndAttributes.PROPERTY_VALUE_V2_ATTR);
            if ((sKey == null) || (sValue == null)) {
                throw new SAXException(
                        "Invalid structure - invalid property tag arguments"); //$NON-NLS-1$
            }
            _curProperties.put(sKey, sValue);
        } else if (IXmlTagsAndAttributes.ANNOTATIONS_TAG.equals(sQName)) {
            _curAnnotations = new ArrayList<PathElementAnnotation>();
        } else if (IXmlTagsAndAttributes.ANNOTATION_TAG.equals(sQName)) {
            if (_curAnnotations == null) {
                throw new SAXException(
                        "Invalid structure - annotation tag detected at wrong place"); //$NON-NLS-1$
            }
            String sMessage = attributes.getValue(IXmlTagsAndAttributes.ANNOTATION_MSG_ATTR);
            String sKind = attributes.getValue(IXmlTagsAndAttributes.ANNOTATION_KIND_ATTR);
            if ((sMessage == null) || (sKind == null)) {
                throw new SAXException(
                        "Invalid structure - invalid annotation tag arguments"); //$NON-NLS-1$
            }
            _curAnnotations.add(new PathElementAnnotation(sMessage, sKind));
        } else {
            Map<String, String> resultAttributes = (Map<String, String>) _resultData.get(_ATTRIBUTES_KEY);
            if (resultAttributes == null) {
                resultAttributes = new HashMap<String, String>();
                _resultData.put(_ATTRIBUTES_KEY, resultAttributes);
            }
            XMLUtil.processAttributesToMap(attributes, resultAttributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String sQName)
            throws SAXException {
        if (sQName.equals(_sProcessedListName)) { // list element end - do nothing
            // do nothing
        } else if (sQName.endsWith(IXmlTagsAndAttributes.LIST_POSTFIX) && sQName.startsWith(IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG)) {
            List<IPathElement> descriptors = _violElemDescStack.removeLast();
            Map<String, Object> attributes = getCurrentData();
            attributes.put(IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG, descriptors);
        } else if (sQName.endsWith(IXmlTagsAndAttributes.LIST_POSTFIX)) {  // end list reading
            String sName = sQName.substring(0, (sQName.length() - IXmlTagsAndAttributes.LIST_POSTFIX.length()));
            if (_sProcessedListName == null) {
                throw new SAXException("Internal parser error - encountered list end but list has not been created!"); //$NON-NLS-1$
            }
            if (!_sProcessedListName.equals(sName)) {
                throw new SAXException("Internal parser error - encountered list end but list name doesn't match read list name!"); //$NON-NLS-1$
            }
            _resultData.put(_sProcessedListName, _processedList);
            _sProcessedListName = null;
            _processedList = null;
        } else if (IXmlTagsAndAttributes.PROPERTIES_V2_TAG.equals(sQName)) {
            if (_curProperties == null) {
                throw new SAXException("Properties not read"); //$NON-NLS-1$
            }
            Map<String, Object> attributes = getCurrentData();
            attributes.put(IXmlTagsAndAttributes.PROPERTIES_V2_TAG, _curProperties);
            _curProperties = null;
        } else if (IXmlTagsAndAttributes.ANNOTATIONS_TAG.equals(sQName)) {
            if (_curAnnotations == null) {
                throw new SAXException("Annotations not read"); //$NON-NLS-1$
            }
            Map<String, Object> attributes = getCurrentData();
            attributes.put(IXmlTagsAndAttributes.ANNOTATIONS_TAG, _curAnnotations);
            _curProperties = null;
        } else if (IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG.equals(sQName)) {
            if (_descriptorDataStack.isEmpty()) {
                throw new SAXException("Violation not read"); //$NON-NLS-1$
            }
            Map<String, Object> descriptorData = _descriptorDataStack.removeLast();
            Map<String, String> descriptorAttributes = (Map<String, String>) descriptorData.get(_ATTRIBUTES_KEY);
            try {
                ResultLocation location = getLocation(descriptorAttributes, IXmlTagsAndAttributes.SOURCE_RANGE_V2_ATTR, true);
                if (location != null) {
                    String sHash = descriptorAttributes.remove(IXmlTagsAndAttributes.RESOURCE_HASH_ATTR);
                    if (sHash != null) {
                        descriptorAttributes.put(IXmlTagsAndAttributes.LOCATION_HASH_ATTR, sHash);
                    }
                }
                String sSourcelessElemDesc = descriptorAttributes.get(IXmlTagsAndAttributes.SOURCELESS_ELEM_DESC);
                List<IPathElement> children = (List<IPathElement>) descriptorData.get(IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG);
                LinkedHashMap<String, String> properties = (LinkedHashMap<String, String>) descriptorData.get(IXmlTagsAndAttributes.PROPERTIES_V2_TAG);
                List<PathElementAnnotation> annotations = (List<PathElementAnnotation>) descriptorData.get(IXmlTagsAndAttributes.ANNOTATIONS_TAG);
                IPathElement curDesc = createViolElemDesc(location, sSourcelessElemDesc, children, properties, descriptorAttributes, annotations);
                if (curDesc != null) {
                    List<IPathElement> descriptors = _violElemDescStack.getLast();
                    descriptors.add(curDesc);
                }
                _curProperties = null;
            } catch (LocationsException le) {
                throw new SAXException(le);
            }
        }
    }

    private Map<String, Object> getCurrentData() {
        if (!_descriptorDataStack.isEmpty()) {
            return _descriptorDataStack.getLast();
        }
        return _resultData;
    }

    /**
     * Returns the attribute map
     *
     * @return the attribute map
     */
    protected Map<String, String> getMap() {
        return (Map<String, String>) _resultData.get(_ATTRIBUTES_KEY);
    }

    /**
     * @param key
     * @return true if the map contains the key, false otherwise
     */
    protected boolean containsKey(final String key) {
        return getMap().containsKey(key);
    }

    /**
     * Returns the boolean value for an attribute.
     *
     * @param sKey string key
     * @return boolean value
     * @pre sKey != null
     */
    protected boolean getBoolean(final String sKey) {
        return XMLUtil.getBoolean(sKey, getMap());
    } // getBoolean(final String)

    /**
     * Returns the string value for an attribute.
     *
     * @param sKey string key
     * @return string value or <code>null</code> for none
     * @pre sKey != null
     */
    protected String getString(final String sKey) {
        return XMLUtil.getString(sKey, getMap());
    } // getString(final String)

    /**
     * Returns the int value for an attribute.
     *
     * @param sKey string key
     * @return int value
     * @throws java.lang.NumberFormatException
     * @pre sKey != null
     */
    protected int getInt(final String sKey) {
        return XMLUtil.getInt(sKey, getMap());

    } // getInt(final String)

    protected int getInt(final String sKey, int default_value) {
        return XMLUtil.getInt(sKey, default_value, getMap());
    }

    /**
     * Returns the long value for an attribute.
     *
     * @param sKey string key
     * @return int value
     * @throws java.lang.NumberFormatException
     * @pre sKey != null
     */
    protected long getLong(final String sKey) {
        return XMLUtil.getLong(sKey, getMap());
    }

    protected ResultLocation getLocation()
            throws LocationsException {
        String sLocPrefix = IXmlTagsAndAttributes.LOCATION_ATTR_V2_PREFIX;
        return getLocation(getMap(), sLocPrefix, false);
    }

    protected ResultLocation getLocation(Map<String, String> attributesMap, String sLocPrefix, boolean bAcceptModified)
            throws LocationsException {
        return getLocation(attributesMap, sLocPrefix, IXmlTagsAndAttributes.LOC_REF_ATTR, bAcceptModified);
    }

    protected ResultLocation getLocation(Map<String, String> attributesMap, String sLocPrefix, String sLocRefAttr,
                                         boolean bAcceptModified)
            throws LocationsException {
        String sLocRef = attributesMap.get(sLocRefAttr);
        if (sLocRef == null) {
            return null;
        }
        SourceRange sourceRange = getSourceRange(attributesMap, sLocPrefix);
        return _locationsReader.getResultLocation(sLocRef, sourceRange, bAcceptModified);
    }

    abstract protected SourceRange getSourceRange(Map<String, String> attributesMap, String sLocPrefix);

    public void setLocations(IResultLocationsReader locationsReader) {
        _locationsReader = locationsReader;
    }

    protected IResultLocationsReader getLocationsReader() {
        return _locationsReader;
    }

    protected List<IPathElement> getDescriptors() {
        return (List<IPathElement>) _resultData.get(IXmlTagsAndAttributes.VIOLATION_ELEMENT_DESC_V2_TAG);
    }

    protected Map<String, String> getProperties() {
        return (Map<String, String>) _resultData.get(IXmlTagsAndAttributes.PROPERTIES_V2_TAG);
    }

    protected IPathElement createViolElemDesc(ResultLocation sourceLocation, String sSourcelessElemDesc,
                                              List<IPathElement> children, Map<String, String> curProperties, Map<String, String> attributesMap, List<PathElementAnnotation> annotations)
            throws SAXException {
        throw new UnsupportedOperationException("Illegal call."); //$NON-NLS-1$
    }

    private final static String _ATTRIBUTES_KEY = "ATTRIBUTES"; //$NON-NLS-1$
}