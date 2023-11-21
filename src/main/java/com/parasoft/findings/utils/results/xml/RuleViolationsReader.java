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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class RuleViolationsReader
        extends ViolationsReader
        implements IViolationsSAXReader {
    private RulesImportHandler _rulesImportHandler = null;

    private ContentHandler _rulesReader = null;

    public RuleViolationsReader(IResultXmlStorage[] aStorages, int[] aStoragesVersions, int[] aLegacyStoragesVersions,
                                String[] asViolationTags, FileImportPreferences importPreferences) {
        super(aStorages, aStoragesVersions, aLegacyStoragesVersions, asViolationTags, importPreferences);
    }

    public void setRulesImportHandler(RulesImportHandler rulesManager) {
        _rulesImportHandler = rulesManager;
    }

    @Override
    public void startElement(String sUri, String sLocalName, String sQName, Attributes attributes)
            throws SAXException {
        super.startElement(sUri, sLocalName, sQName, attributes);
        if (IXmlTagsAndAttributes.RULES_TAG.equals(sQName)) {
            _rulesReader = _rulesImportHandler.getRulesSAXReader();// rulesreader
        }

        if (_rulesReader != null) {
            _rulesReader.startElement(sUri, sLocalName, sQName, attributes);
        }
    }

    @Override
    protected void initializeCurrentReader(IViolationSAXReader currentReader) {
        super.initializeCurrentReader(currentReader);
        if (currentReader instanceof IRuleViolationSAXReader) {
            ((IRuleViolationSAXReader) currentReader).setRulesImportHandler(_rulesImportHandler);
        }
    }

    @Override
    public void endElement(String sUri, String sLocalName, String sQName)
            throws SAXException {
        super.endElement(sUri, sLocalName, sQName);
        if (IXmlTagsAndAttributes.RULES_TAG.equals(sQName)) {
            _rulesReader = null;
        }
    }

}
