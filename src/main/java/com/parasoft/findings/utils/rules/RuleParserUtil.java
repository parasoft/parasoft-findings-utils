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

import com.parasoft.findings.utils.common.util.IOUtils;
import com.parasoft.findings.utils.common.util.XMLUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class RuleParserUtil
{
    private RuleParserUtil() {}

    public static void saxParse(URL file, DefaultHandler handler, RuleParsingOptions options)
        throws IOException
    {
        InputStream in = null;
        try {
            in = file.openStream();
            saxParse(in, handler, options);
        } catch (FileNotFoundException e) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during parsing rule creating don't cause the process to fail."
            Logger.getLogger().warn("File not found under given url " + file); //$NON-NLS-1$
        } catch (Exception e) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to wrap exceptions with IOException."
            Logger.getLogger().error(e);
            throw new IOException("Error while parsing document: " + e.getMessage(), e); //$NON-NLS-1$
        } finally {
            IOUtils.close(in);
        }
    }

    public static void saxParse(InputStream in, DefaultHandler handler, RuleParsingOptions options)
        throws ParserConfigurationException, SAXException, IOException
    {
        if (options == null) {
            saxParse(in, handler);
            return;
        }

        XMLReader reader = createReader(options);

        reader.setContentHandler(handler);
        reader.setDTDHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);

        InputSource input = new InputSource(in);
        reader.parse(input);
    }

    private static XMLReader createReader(RuleParsingOptions options)
    {
        XMLReader reader = null;
        try {
            reader = XMLUtil.createXMLReader(options.disableDTD, options.disableExternalEntities);
        } catch (Exception e) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to ensure exceptions during sax factory reader fetching don't cause the process to fail." // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during sax factory reader creating don't cause the process to fail."
            Logger.getLogger().error("Error while getting sax factory reader", e); //$NON-NLS-1$
        }

        Logger.getLogger().debug("Using reader for parsing rules/mappings: " + reader); //$NON-NLS-1$
        return reader;
    }

    /**
     * @param in
     * @param handler
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @deprecated for security reasons use {@link #saxParse(InputStream, DefaultHandler, RuleParsingOptions)}
     */
    @Deprecated
    private static void saxParse(InputStream in, DefaultHandler handler)
        throws ParserConfigurationException, SAXException, IOException
    {
        SAXParser parser = XMLUtil.createSAXParser();
        parser.parse(in, handler);
    }

    public static final class RuleParsingOptions
    {
        public boolean disableDTD = true;
        public boolean disableExternalEntities = true;

        @Override
        public String toString()
        {
            return "RuleParsingOptions [disableDTD=" + disableDTD + ", disableExternalEntities=" //$NON-NLS-1$ //$NON-NLS-2$
                + disableExternalEntities + "]"; //$NON-NLS-1$
        }
    }

}
