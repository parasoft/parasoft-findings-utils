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

import com.parasoft.findings.utils.common.IStringConstants;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.StringReader;
import java.util.Map;

/**
 * Class for XML document processing.
 **/
public final class XMLUtil {

    /**
     * Default SAXParserFactory instance
     */
    private static SAXParserFactory _saxParserFactory = null;

    /**
     * To prevent creation of instances.
     */
    private XMLUtil() {
        super();
    }

    /**
     * @return default SAXParserFactory instance. Factory newInstance creation can be time consuming (especially in Visual Studio) so use this method
     * instead of creation new instance.
     * <p>
     * ATTENTION! Direct usage of SAXParserFactory is not recommended because of vulnerability (XML bomb)
     * Use createSAXParser() method to get secured parser.
     */
    public static SAXParserFactory getSaxParserFactory() {
        if (_saxParserFactory == null) {
            try {
                // Cache SAX parser factory for performance reason.
                // Use Xerces SAX implementation if available. Avoiding SAXParserFactory.newInstance()
                // which can return an inconsistent implementation depending on the context.
                _saxParserFactory = SAXParserFactory.newInstance(
                        "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl", XMLUtil.class.getClassLoader()); //$NON-NLS-1$
            } catch (FactoryConfigurationError err) {
                Logger.getLogger().error("Failed to get an instance of com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl." //$NON-NLS-1$
                        + " Falling back to default SAXParserFactory implementation."); //$NON-NLS-1$
                _saxParserFactory = SAXParserFactory.newInstance();
            }
        }
        return _saxParserFactory;
    }

    public static SAXParser createSAXParser()
            throws ParserConfigurationException, SAXException
    {
        return createSAXParser(true, true);
    }

    public static SAXParser createSAXParser(boolean disableDTD, boolean disableExternalEntities)
            throws ParserConfigurationException, SAXException
    {
        SAXParser parser = getSaxParserFactory().newSAXParser();
        configureReader(parser.getXMLReader(), disableDTD, disableExternalEntities);
        return parser;
    }

    /**
     * Creates an instance of XMLReader.
     * @param disableDTD disallows DTDs
     * @param disableExternalEntities disallows loading of external entities
     * @return the instance of XMLReader
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static XMLReader createXMLReader(boolean disableDTD, boolean disableExternalEntities)
            throws ParserConfigurationException, SAXException
    {
        return createSAXParser(disableDTD, disableExternalEntities).getXMLReader();
    }

    private static void configureReader(XMLReader reader, boolean disableDTD, boolean disableExternalEntities) {
        // limit entity expansion to prevent billion laughs
        try {
            reader.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", 100000); //$NON-NLS-1$
        } catch (SAXException e) {
            Logger.getLogger().warn(e);
        }

        // disallow DTD
        if (disableDTD) {
            try {
                reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //$NON-NLS-1$
                reader.setEntityResolver(new EmptyEntityResolver());
            } catch (SAXException e) {
                Logger.getLogger().warn(e);
            }
        }

        // disable external entities
        if (disableExternalEntities) {
            try {
                reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false); //$NON-NLS-1$
                reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false); //$NON-NLS-1$
            } catch (SAXException e) {
                Logger.getLogger().warn(e);
            }
        }
    }

    /**
     * Returns the int value for an attribute.
     *
     * @param sKey the key string
     * @param map  the map instance
     * @return int value
     * @throws java.lang.NumberFormatException
     * @pre sKey != null
     * @pre map != null
     */
    public static int getInt(final String sKey, final Map map) {
        return Integer.parseInt((String) map.get(sKey));
    }

    public static int getInt(final String sKey, final int defaultValue, final Map map) {
        try {
            String sVal = (String) map.get(sKey);
            if (sVal != null) {
                return Integer.parseInt(sVal);
            }
        } catch (NumberFormatException e) {
            Logger.getLogger().error(e);
        }
        return defaultValue;
    }

    /**
     * Returns the long value for an attribute.
     *
     * @param sKey the key string
     * @param map  the map instance
     * @return long value
     * @throws java.lang.NumberFormatException
     * @pre sKey != null
     * @pre map != null
     */
    public static long getLong(final String sKey, final Map map) {
        return Long.parseLong((String) map.get(sKey));
    }

    /**
     * Returns the string value for an attribute.
     *
     * @param sKey the key string
     * @param map  the map instance
     * @return string value or <code>null</code> for none
     * @pre sKey != null
     * @pre map != null
     */
    public static String getString(final String sKey, final Map map) {
        return (String) map.get(sKey);
    }

    /**
     * Returns the boolean value for an attribute.
     *
     * @param sKey the key string
     * @param map  the map instance
     * @return the boolean value
     * @pre sKey != null
     * @pre map != null
     */
    public static boolean getBoolean(final String sKey, final Map map) {
        return Boolean.TRUE.toString().equalsIgnoreCase((String) map.get(sKey));
    }

    /**
     * This method reads the specified attributes to the specified map.
     *
     * @param attributes Attributes to read from
     * @param map        Map to read into
     * @pre attributes != null
     * @pre map != null
     */
    public static void processAttributesToMap(Attributes attributes, Map<String, String> map) {
        final int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            String sName = attributes.getQName(i);
            String sValue = attributes.getValue(i);
            map.put(sName, sValue);
        }
    }

    // This would prevent making any calls to resolve URL references to external DTD
    private static class EmptyEntityResolver
            implements EntityResolver {
        @Override
        public InputSource resolveEntity(String publicID, String systemID) {
            return new InputSource(new StringReader(IStringConstants.EMPTY));
        }
    }
}