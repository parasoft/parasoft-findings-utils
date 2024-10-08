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

import com.parasoft.findings.utils.results.violations.IResult;
import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.xml.factory.ILegacySupportResultXmlStorage;
import com.parasoft.findings.utils.results.location.IResultLocationsReader;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 * Default implementation of <code>IViolationsSAXReader</code>.
 */
public class ViolationsReader
        extends DefaultHandler
        implements IViolationsSAXReader {
    private IResultLocationsReader _locationsReader = null;

    /**
     * currently used concrete reader
     */
    private IViolationSAXReader _currentReader = null;

    /**
     * the main tag of currently read violation
     */
    private String _sCurrentViolationTag = null;

    /**
     * the queue containing already read violations
     */
    private final Queue<IViolation> _readViolations;

    /**
     * the import preferences to use
     */
    private FileImportPreferences _importPreferences = null;

    /**
     * contains mapping: main violation's tag -> violation storage
     */
    private final Map<String, IResultXmlStorage> _storagesMap = new HashMap<String, IResultXmlStorage>();

    /**
     * contains mapping: main violation's tag -> storage version
     */
    private final Map<String, Integer> _storagesVersionsMap = new HashMap<String, Integer>();

    /**
     * contains mapping: main violation's tag -> storage legacy version
     */
    private final Map<String, Integer> _storagesLegacyVersionsMap = new HashMap<String, Integer>();

    private XmlReportReader _parentReader = null;


    /**
     * Constructor.
     *
     * @param aStorages               the results storages to use, each storage in this array corresponds to a tag in <code>asViolationTags</code> array
     * @param aStoragesVersions       xml storages versions
     * @param aStoragesLegacyVersions legacy xml storages versions
     * @param asViolationTags         the associated violation tag, each tag in this array corresponds to a storage in <code>aStorages</code> array
     * @param importPreferences       the import preferences to use
     * @pre asViolationTags != null
     * @pre aStorages != null
     * @pre aStoragesVersions != null
     * @pre asViolationTags.length == aStorages.length
     * @pre aStoragesVersions.length == aStorages.length
     * @pre importPreferences != null
     */
    public ViolationsReader(IResultXmlStorage[] aStorages, int[] aStoragesVersions, int[] aStoragesLegacyVersions, String[] asViolationTags,
                            FileImportPreferences importPreferences) {
        _readViolations = new LinkedList<IViolation>();
        int length = aStorages.length;
        if ((length != asViolationTags.length) || (length != aStoragesVersions.length)) {
            Logger.getLogger().error("Unequal lengths of tags, storages arrays and storages versions."); //$NON-NLS-1$
            length = Math.min(length, asViolationTags.length);
            length = Math.min(length, aStoragesVersions.length);
        }
        for (int i = 0; i < length; i++) {
            _storagesMap.put(asViolationTags[i], aStorages[i]);
            _storagesVersionsMap.put(asViolationTags[i], aStoragesVersions[i]);
        }
        if ((aStoragesLegacyVersions != null) && (aStoragesLegacyVersions.length >= length)) {
            for (int i = 0; i < length; i++) {
                _storagesLegacyVersionsMap.put(asViolationTags[i], aStoragesLegacyVersions[i]);
            }
        }
        _importPreferences = importPreferences;
    }

    public IViolation getNextViolation() {
        return _readViolations.poll();
    }


    /**
     * Sets if getReadViolations should return only those violations with unchanged hash
     *
     * @param bEnabled new flag state
     */
    public void setResourceHashVerifification(boolean bEnabled) {
        // nothing to do, implement when UI importing needed
    }

    @Override
    public void startElement(String sUri, String sLocalName, String sQName, Attributes attributes)
            throws SAXException {
        IResultXmlStorage storage = _storagesMap.get(sQName);
        if (storage != null) {
            setCurrentReader(sQName, storage);
        }
        if (_currentReader != null) {
            try {
                _currentReader.startElement(sUri, sLocalName, sQName, attributes);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    private void setCurrentReader(String sQName, IResultXmlStorage storage)
            throws SAXException {
        Integer storageVersion = _storagesVersionsMap.get(sQName);
        if (storageVersion == null) {
            Logger.getLogger().errorTrace("Missing storage version, using current version"); //$NON-NLS-1$
            storageVersion = storage.getVersion();
        }
        IResultSAXReader reader = null;
        if (storageVersion == -1) {
            Integer storageLegacyVersion = _storagesLegacyVersionsMap.get(sQName);
            reader = getLegacyReader(storage, storageLegacyVersion);
        } else {
            reader = storage.getReader(storageVersion.intValue());
        }
        if (reader == null) {
            throw new SAXException("Null reader from result storage " + storage.getResultId()); //$NON-NLS-1$
        }
        if (!(reader instanceof IViolationSAXReader)) {
            throw new SAXException("Reader of illegal type from result storage " + storage.getResultId()); //$NON-NLS-1$
        }
        _currentReader = (IViolationSAXReader) reader;
        initializeCurrentReader(_currentReader);

        _sCurrentViolationTag = sQName;
    }

    private static IResultSAXReader getLegacyReader(IResultXmlStorage storage, Integer storageLegacyVersion) {
        if ((storage instanceof ILegacySupportResultXmlStorage) && (storageLegacyVersion != null)) {
            return ((ILegacySupportResultXmlStorage) storage).getLegacyReader(storageLegacyVersion);
        }
        return null;
    }

    protected void initializeCurrentReader(IViolationSAXReader currentReader) {
        currentReader.setLocations(_locationsReader);
    }

    @Override
    public void endElement(String sUri, String sLocalName, String sQName)
            throws SAXException {
        if (_currentReader != null) {
            try {
                _currentReader.endElement(sUri, sLocalName, sQName);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
        if (sQName.equals(_sCurrentViolationTag)) {
            if (_currentReader != null) {
                try {
                    addViolation();
                } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                    Logger.getLogger().warn(t);
                }
                _currentReader = null;
                _sCurrentViolationTag = null;
            } else {
                Logger.getLogger().warn("Failed to read violation."); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // nothing to do
    }

    @Override
    public void startDocument() {
        // nothing to do
    }

    @Override
    public void endDocument() {
        // nothing to do
    }

    @Override
    public void startPrefixMapping(String sPrefix, String sUri) {
        if (_currentReader != null) {
            try {
                _currentReader.startPrefixMapping(sPrefix, sUri);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    @Override
    public void endPrefixMapping(String sPrefix) {
        if (_currentReader != null) {
            try {
                _currentReader.endPrefixMapping(sPrefix);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    @Override
    public void characters(char[] aChars, int start, int length)
            throws SAXException {
        if (_currentReader != null) {
            try {
                _currentReader.characters(aChars, start, length);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    @Override
    public void ignorableWhitespace(char[] aChars, int start, int length) {
        if (_currentReader != null) {
            try {
                _currentReader.ignorableWhitespace(aChars, start, length);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    @Override
    public void processingInstruction(String sTarget, String sData) {
        if (_currentReader != null) {
            try {
                _currentReader.processingInstruction(sTarget, sData);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    @Override
    public void skippedEntity(String sName) {
        if (_currentReader != null) {
            try {
                _currentReader.skippedEntity(sName);
            } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations reading don't cause the process to fail."
                Logger.getLogger().warn(t);
                _currentReader = null;
                _sCurrentViolationTag = null;
            }
        }
    }

    /**
     * @pre _currentReader != null
     */
    private void addViolation() {
        if (_parentReader != null) {
            _parentReader.itemRead();
        }

        IResult result = null;
        try {
            result = _currentReader.getReadResult();
        } catch (Throwable t) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violations adding don't cause the process to fail."
            Logger.getLogger().error(t);
        }
        if (result == null) {
            return;
        }
        if (!(result instanceof IViolation)) {
            return;
        }
        IViolation violation = (IViolation) result;

        if (_parentReader != null) {
            int testConfigId = _parentReader.getTestConfigId();
            violation.addAttribute(IXmlTagsAndAttributes.TEST_CONFIG_ATTR, Integer.toString(testConfigId));
        }

        _readViolations.add(violation);
    }

    public void setParentReader(XmlReportReader parentViolationsReader) {
        _parentReader = parentViolationsReader;
    }

    public void setLocations(IResultLocationsReader locationsReader) {
        _locationsReader = locationsReader;
    }

}