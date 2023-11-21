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

import com.parasoft.findings.utils.common.util.IntegerUtil;
import com.parasoft.findings.utils.results.xml.factory.ILegacySupportResultXmlStorage;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.*;

/**
 * The manager of result storage versions.
 */
public class ResultVersionsManager {
    private boolean _bInitialized = false;

    private final Map<String, Integer> _versionsMap = new HashMap<String, Integer>();

    private final Map<String, Integer> _legacyVersionsMap = new HashMap<String, Integer>();

    /**
     * Constructor.
     */
    public ResultVersionsManager() {
    }

    /**
     * Returns the version for given results storage.
     *
     * @param storage the storage
     * @return the version value
     */
    public int getVersion(IResultXmlStorage storage) {
        if (!_bInitialized) {
            return 1;
        }
        Integer versionInt = _versionsMap.get(storage.getResultId());
        if (versionInt == null) {
            return 1;
        }
        return versionInt.intValue();
    }

    public int getLegacyVersion(IResultXmlStorage storage) {
        Integer legacyVersion = _legacyVersionsMap.get(storage.getResultId());
        if (legacyVersion == null) {
            return -1;
        }
        return legacyVersion;
    }

    /**
     * Checks if given storage id compatible with one registered in manager.
     *
     * @param storage the storage instance
     * @return <code>true</code> if storage compatible, <code>false</code> otherwise
     * @pre storage != null
     */
    public boolean isCompatible(IResultXmlStorage storage) {
        if (!_bInitialized) {
            if (storage.getVersion() == 1) {
                return true;
            }
            return storage.isCompatible(1);
        }
        Integer versionInt = _versionsMap.get(storage.getResultId());
        if (versionInt == null) {
            return false;
        }
        int version = versionInt.intValue();
        if (storage.getVersion() == version) {
            return true;
        }
        if (version < 0) {
            return isLegacyCompatible(storage);
        }
        return storage.isCompatible(version);

    }

    private boolean isLegacyCompatible(IResultXmlStorage storage) {
        if (!(storage instanceof ILegacySupportResultXmlStorage)) {
            return false;
        }
        Integer legacyVersion = _legacyVersionsMap.get(storage.getResultId());
        if (legacyVersion == null) {
            return false;
        }
        ILegacySupportResultXmlStorage legacyStorage = (ILegacySupportResultXmlStorage) storage;
        if (legacyStorage.getLegacyVersion() == legacyVersion) {
            return true;
        }
        return legacyStorage.isLegacyCompatible(legacyVersion);
    }

    void setVersionInfos(VersionInfo[] aVersionInfos) {
        _versionsMap.clear();
        _legacyVersionsMap.clear();
        for (VersionInfo versionInfo : aVersionInfos) {
            String sResultId = versionInfo.getResultId();
            _versionsMap.put(sResultId, versionInfo.getVersion());
            _legacyVersionsMap.put(sResultId, versionInfo.getLegacyVersion());
        }
        _bInitialized = true;
    }

    /**
     * Gets the sax reader of versions xml element.
     *
     * @return the instance of versioms sax reader
     * @post $result != null
     */
    public ContentHandler getVersionsSAXReader() {
        return new VersionsReader();
    }

    private final class VersionsReader
            implements ContentHandler {

        private List<VersionInfo> _versionInfoList = null;

        /**
         * Package-private constructor.
         */
        VersionsReader() {
            super();
        }

        public void startElement(String sUri, String sLocalName, String sQName, Attributes attributes)
                throws SAXException {
            if (IXmlTagsAndAttributes.VERSIONS_TAG.equals(sQName)) {
                if (_versionInfoList != null) {
                    throw new SAXException("Reading already started."); //$NON-NLS-1$
                }
                _versionInfoList = new ArrayList<VersionInfo>();
            } else if (IXmlTagsAndAttributes.VERSION_INFO_TAG.equals(sQName)) {
                if (_versionInfoList == null) {
                    throw new SAXException("Illegal element spotted."); //$NON-NLS-1$
                }
                String sStoreId = attributes.getValue(IXmlTagsAndAttributes.RESULT_STORAGE_ID_ATTR);
                String sVersion = attributes.getValue(IXmlTagsAndAttributes.VERSION_ATTR);
                String sLegacyVersion = attributes.getValue(IXmlTagsAndAttributes.LEGACY_VERSION_ATTR);
                if ((sStoreId == null) || ((sVersion == null) && (sLegacyVersion == null))) {
                    throw new SAXException("Expected attribute not found."); //$NON-NLS-1$
                }
                int version = IntegerUtil.parseInt(sVersion);
                int legacyVersion = IntegerUtil.parseInt(sLegacyVersion);
                VersionInfo info = new VersionInfo(sStoreId, version, legacyVersion);
                _versionInfoList.add(info);
            } else {
                throw new SAXException(IResultSAXReader.ILLEGAL_TAG_MESSAGE);
            }
        }

        public void endElement(String sUri, String sLocalName, String sQName)
                throws SAXException {
            if (IXmlTagsAndAttributes.VERSIONS_TAG.equals(sQName)) {
                if (_versionInfoList == null) {
                    throw new SAXException("Illegal element spotted."); //$NON-NLS-1$
                }
                VersionInfo[] aVersionInfos = new VersionInfo[_versionInfoList.size()];
                aVersionInfos = _versionInfoList.toArray(aVersionInfos);
                setVersionInfos(aVersionInfos);
                _versionInfoList = null;
            } else if (IXmlTagsAndAttributes.VERSION_INFO_TAG.equals(sQName)) {
                // does nothing
            } else {
                throw new SAXException(IResultSAXReader.ILLEGAL_TAG_MESSAGE);
            }
        }

        public void setDocumentLocator(Locator arg0) {
            // nothing to do
        }

        public void startDocument() {
            // nothing to do
        }

        public void endDocument() {
            // nothing to do
        }

        public void startPrefixMapping(String arg0, String arg1) {
            // nothing to do
        }

        public void endPrefixMapping(String arg0) {
            // nothing to do
        }

        public void characters(char[] arg0, int arg1, int arg2) {
            // nothing to do
        }

        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) {
            // nothing to do
        }

        public void processingInstruction(String arg0, String arg1) {
            // nothing to do
        }

        public void skippedEntity(String arg0) {
            // nothing to do
        }

    } // class VersionsReader

    private static final class VersionInfo {
        private final String _sResultId;
        private final int _version;
        private final int _legacyVersion;

        private VersionInfo(String sResultId, int version, int legacyVersion) {
            _sResultId = sResultId;
            _version = version;
            _legacyVersion = legacyVersion;
        }

        private String getResultId() {
            return _sResultId;
        }

        private int getVersion() {
            return _version;
        }

        private int getLegacyVersion() {
            return _legacyVersion;
        }
    } // class VersionInfo

}
