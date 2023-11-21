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

import com.parasoft.findings.utils.common.IStringConstants;
import com.parasoft.findings.utils.common.ParasoftConstants;
import com.parasoft.findings.utils.results.xml.factory.IResultsIdentifiersConstants;
import com.parasoft.findings.utils.results.xml.factory.UResults;
import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.location.IResultLocationsReader;
import com.parasoft.findings.utils.results.location.ResultLocationsReader;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.location.LegacyResultLocationsReader;
import com.parasoft.findings.utils.results.xml.factory.ResultFactoriesManager;
import com.parasoft.findings.utils.results.xml.factory.DefaultCodingStandardsResultFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 * The reader of violations from xml report.
 */
public class XmlReportReader
        extends DefaultHandler {
    private final Map<String, StaticXmlStorage> _sessionStoragesMap = new HashMap<String, StaticXmlStorage>();

    private LinkedList<String> _parentElemNamesStack = null;

    private boolean _bScope = false;

    private final FileImportPreferences _preferences;

    private ResultVersionsManager _versionsManager = null;

    private ResultLocationsReader _locationsManager = null;

    private IViolationsSAXReader _reader = null;

    private ContentHandler _versionsReader = null;

    private ContentHandler _locationsReader = null;

    private final List<IViolation> _importedViolations = new ArrayList<IViolation>();

    private final List<IViolation> _violationsBatch = new ArrayList<IViolation>();

    private final IImportResultCollector _target;

    private final RulesImportHandler _rulesImportHandler = new RulesImportHandler();

    private final LegacyResultLocationsReader _legacyLocationsManager;

    private boolean areUsedLegacyLocations = false;

    /**
     * Constructor.
     *
     * @param preferences
     * @param locationMatcher
     * @pre preferences != null
     */
    public XmlReportReader(FileImportPreferences preferences, ITestableInputLocationMatcher locationMatcher) {
        this(preferences, locationMatcher, null);
    }

    /**
     * Constructor.
     *
     * @param preferences
     * @param locationMatcher
     * @param target
     * @pre preferences != null
     */
    public XmlReportReader(FileImportPreferences preferences,
                           ITestableInputLocationMatcher locationMatcher, IImportResultCollector target) {
        _preferences = preferences;
        _parentElemNamesStack = new LinkedList<String>();
        _versionsManager = new ResultVersionsManager();
        _locationsManager = new ResultLocationsReader(locationMatcher);
        _legacyLocationsManager = new LegacyResultLocationsReader(locationMatcher);
        ResultFactoriesManager factoriesManager = UResults.getResultFactoriesManager();
        initStoragesMap(factoriesManager);

        _target = target;
    }

    public List<IViolation> getImportedViolations() {
        return _importedViolations;
    }

    public int getTestConfigId() {
        return 0;
    }

    @Override
    public void startElement(String sUri, String sLocalName, String sQName, Attributes attributes)
            throws SAXException {
        _parentElemNamesStack.addLast(sQName);
        if (_parentElemNamesStack.size() == HANDLER_ELEMENTS_DEPTH) {
            if (IXmlTagsAndAttributes.VERSIONS_TAG.equals(sQName)) {
                _versionsReader = _versionsManager.getVersionsSAXReader();
            } else if (IXmlTagsAndAttributes.SCOPE_SECTION_TAG.equals(sQName)) {
                _bScope = true;
            } else if (IXmlTagsAndAttributes.LOCATIONS_TAG.equals(sQName)) {
                areUsedLegacyLocations = true;
                _locationsReader = _legacyLocationsManager;
            } else {
                _reader = getViolationsReader(sQName, attributes);
                return; // return here - no need to pass this element to reader
            }
        }
        if (_reader != null) {
            _reader.startElement(sUri, sLocalName, sQName, attributes);
        } else if (_versionsReader != null) {
            _versionsReader.startElement(sUri, sLocalName, sQName, attributes);
        } else if (_locationsReader != null) {
            _locationsReader.startElement(sUri, sLocalName, sQName, attributes);
        } else if (_bScope && (_parentElemNamesStack.size() == (HANDLER_ELEMENTS_DEPTH + 1))) {
            if (IXmlTagsAndAttributes.LOCATIONS_TAG.equals(sQName)) {
                _locationsReader = _locationsManager;
            }
        } else if (_parentElemNamesStack.size() == 1) {
            verifyToolVersion(sQName, attributes);
        }
    }

    @Override
    public void endElement(String sUri, String sLocalName, String sQName)
            throws SAXException {
        if ((_parentElemNamesStack.size() == HANDLER_ELEMENTS_DEPTH)) {
            if (_reader != null) {
                flushReader();
                _reader = null;
            } else if (_versionsReader != null) {
                _versionsReader.endElement(sUri, sLocalName, sQName);
                _versionsReader = null;
            } else if (IXmlTagsAndAttributes.SCOPE_SECTION_TAG.equals(sQName)) {
                _bScope = false;
            } else if ((_locationsReader != null) && IXmlTagsAndAttributes.LOCATIONS_TAG.equals(sQName) && areUsedLegacyLocations) {
                _locationsReader.endElement(sUri, sLocalName, sQName);
                _locationsReader = null;
            }
        } else if (_reader != null) {
            _reader.endElement(sUri, sLocalName, sQName);
            IViolation violation = _reader.getNextViolation();
            while (violation != null) {
                collectViolation(violation);
                violation = _reader.getNextViolation();
            }
        } else if (_versionsReader != null) {
            _versionsReader.endElement(sUri, sLocalName, sQName);
        } else if (_locationsReader != null) {
            if ((_parentElemNamesStack.size() == (HANDLER_ELEMENTS_DEPTH + 1)) && !areUsedLegacyLocations) {
                _locationsReader.endElement(sUri, sLocalName, sQName);
                _locationsReader = null;
            }
        }
        _parentElemNamesStack.removeLast();
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        if (_reader != null) {
            _reader.setDocumentLocator(locator);
        } else if (_versionsReader != null) {
            _versionsReader.setDocumentLocator(locator);
        } else if (_locationsReader != null) {
            _locationsReader.setDocumentLocator(locator);
        }
    }

    @Override
    public void startDocument() {
        // does nothing
    }

    @Override
    public void endDocument() {
        // does nothing
    }

    @Override
    public void startPrefixMapping(String sPrefix, String sUri)
            throws SAXException {
        if (_reader != null) {
            _reader.startPrefixMapping(sPrefix, sUri);
        } else if (_versionsReader != null) {
            _versionsReader.startPrefixMapping(sPrefix, sUri);
        } else if (_locationsReader != null) {
            _locationsReader.startPrefixMapping(sPrefix, sUri);
        }
    }

    @Override
    public void endPrefixMapping(String sPrefix)
            throws SAXException {
        if (_reader != null) {
            _reader.endPrefixMapping(sPrefix);
        } else if (_versionsReader != null) {
            _versionsReader.endPrefixMapping(sPrefix);
        } else if (_locationsReader != null) {
            _locationsReader.endPrefixMapping(sPrefix);
        }
    }

    @Override
    public void characters(char[] aChars, int start, int length)
            throws SAXException {
        if (_reader != null) {
            _reader.characters(aChars, start, length);
        } else if (_versionsReader != null) {
            _versionsReader.characters(aChars, start, length);
        } else if (_locationsReader != null) {
            _locationsReader.characters(aChars, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] aChars, int start, int length)
            throws SAXException {
        if (_reader != null) {
            _reader.ignorableWhitespace(aChars, start, length);
        } else if (_versionsReader != null) {
            _versionsReader.ignorableWhitespace(aChars, start, length);
        } else if (_locationsReader != null) {
            _locationsReader.ignorableWhitespace(aChars, start, length);
        }
    }

    @Override
    public void processingInstruction(String sTarget, String sData)
            throws SAXException {
        if (_reader != null) {
            _reader.processingInstruction(sTarget, sData);
        } else if (_versionsReader != null) {
            _versionsReader.processingInstruction(sTarget, sData);
        } else if (_locationsReader != null) {
            _locationsReader.processingInstruction(sTarget, sData);
        }
    }

    @Override
    public void skippedEntity(String sName)
            throws SAXException {
        if (_reader != null) {
            _reader.skippedEntity(sName);
        } else if (_versionsReader != null) {
            _versionsReader.skippedEntity(sName);
        } else if (_locationsReader != null) {
            _locationsReader.skippedEntity(sName);
        }
    }

    public void itemRead() {
    }

    private IViolationsSAXReader getViolationsReader(String sMainTag, Attributes attributes) {
        String sResultId = attributes.getValue(IXmlTagsAndAttributes.RESULT_ID_ATTR);
        if (sResultId == null) {
            if (IXmlTagsAndAttributes.CODING_STANDARDS_SECTION_TAG.equals(sMainTag)) {
                sResultId = IResultsIdentifiersConstants.CODING_STANDARDS_RESULT_ID;
            } else if (IXmlTagsAndAttributes.EXECUTION_SECTION_V2_TAG.equals(sMainTag)) {
                sResultId = IResultsIdentifiersConstants.EXECUTION_VIOLATION_ID;
            } else {
                return null;
            }
        }
        StaticXmlStorage resultsSessionXmlStorage = _sessionStoragesMap.get(sResultId);
        if (resultsSessionXmlStorage == null) {
            Logger.getLogger().error("No storage with result id " + sResultId + " found");  //$NON-NLS-1$//$NON-NLS-2$
            return null;
        }
        IViolationsSAXReader resultReader = resultsSessionXmlStorage.getViolationsReader(_versionsManager, _preferences);
        if (resultReader != null) {
            resultReader.setResourceHashVerifification(true);
            resultReader.setParentReader(this);
            resultReader.setLocations(getLocationReader());
            if (resultReader instanceof RuleViolationsReader) {
                ((RuleViolationsReader) resultReader).setRulesImportHandler(_rulesImportHandler);
            }
        }
        return resultReader;
    }

    private void initStoragesMap(ResultFactoriesManager factoriesManager) {
        DefaultCodingStandardsResultFactory resultFactory = factoriesManager.getResultFactory();

        StaticXmlStorage resultsSessionXmlStorage = new StaticXmlStorage(resultFactory.getViolationStorages());
        IResultXmlStorage[] resultStorages = resultFactory.getResultStorages();
        if (resultsSessionXmlStorage != null) {
            for (IResultXmlStorage resultStorage : resultStorages) {
                String sResultId = resultStorage.getResultId();

                _sessionStoragesMap.put(sResultId, resultsSessionXmlStorage);
            }
        }
    }

    protected void flushReader() {
        if (_target != null) {
            IViolation[] aViolations = new IViolation[_violationsBatch.size()];
            aViolations = _violationsBatch.toArray(aViolations);
            _violationsBatch.clear();
            _target.collect(aViolations);
        }
    }

    protected void collectViolation(IViolation violation) {
        _importedViolations.add(violation);
        if (_target != null) {
            _violationsBatch.add(violation);
        }
    }

    private static void verifyToolVersion(String sQName, Attributes attributes)
            throws SAXException {
        if (!IXmlTagsAndAttributes.RESULTS_SESSION_ROOT_TAG.equals(sQName)) {
            throw new SAXException("ResultsSession main tag expected."); //$NON-NLS-1$
        }

        // verify tool name
        String sToolName = attributes.getValue(IXmlTagsAndAttributes.TOOL_NAME_V2_ATTR);
        if (sToolName == null) {
            sToolName = attributes.getValue(IXmlTagsAndAttributes.TOOL_NAME_V1_ATTR);
        }
        if (sToolName == null) {
            throw new SAXException("Tool name attribute not found."); //$NON-NLS-1$
        }
        Set<String> parasoftToolNames = new HashSet<String>();
        parasoftToolNames.add(ParasoftConstants.JTEST_TOOL_NAME);
        parasoftToolNames.add(ParasoftConstants.CPPTEST_TOOL_NAME);
        parasoftToolNames.add(ParasoftConstants.DOTTEST_TOOL_NAME);
        parasoftToolNames.add(ParasoftConstants.SOATEST_TOOL_NAME);
        if (!parasoftToolNames.contains(sToolName)) {
            return;
        }

        // verify tool version
        String sToolVersion = attributes.getValue(IXmlTagsAndAttributes.TOOL_VERSION_V2_ATTR);
        if (sToolVersion == null) {
            sToolVersion = attributes.getValue(IXmlTagsAndAttributes.TOOL_VERSION_V1_ATTR);
        }
        if (sToolVersion == null) {
            throw new SAXException("Tool version attribute not found."); //$NON-NLS-1$
        }
        int dotIdx = sToolVersion.indexOf(IStringConstants.CHAR_DOT);
        if (dotIdx == -1) {
            throw new SAXException("Illegal tool version attribute value."); //$NON-NLS-1$
        }
    }

    /**
     *
     */
    public interface IImportResultCollector {
        /**
         * @param aViolations
         * @pre aViolations != null
         */
        void collect(IViolation[] aViolations);
    }

    private final static int HANDLER_ELEMENTS_DEPTH = 2;


    public RulesImportHandler getRulesImportHandler() {
        return _rulesImportHandler;
    }

    private IResultLocationsReader getLocationReader() {
        if (!areUsedLegacyLocations) {
            return _locationsManager;
        } else {
            return _legacyLocationsManager;
        }
    }

}