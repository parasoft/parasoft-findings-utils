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

package com.parasoft.findings.utils.results.xml.factory;

import java.util.*;

import com.parasoft.findings.utils.results.violations.DupCodePathElement;
import com.parasoft.findings.utils.results.violations.DupCodeViolation;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.xml.*;
import com.parasoft.findings.utils.results.violations.IPathElement;
import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.violations.PathElementAnnotation;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.common.util.XMLUtil;

/**
 * Default concrete storage for coding standards that returns null reader.
 */
class DupcodeViolationStorage
        implements IViolationXmlStorage, ILegacySupportResultXmlStorage {

    @Override
    public IResultSAXReader getReader(int xmlVersion) {
        return new DupcodeViolationReader(false);
    }

    @Override
    public String getResultId() {
        return IResultsIdentifiersConstants.DUPCODE_RESULT_ID;
    }

    @Override
    public int getVersion() {
        return DUPCODE_STORAGE_VERSION;
    }

    @Override
    public boolean isCompatible(int version) {
        return version <= getVersion();
    }

    @Override
    public int getLegacyVersion() {
        return DUPCODE_STORAGE_LEGACY_VERSION;
    }

    @Override
    public boolean isLegacyCompatible(int legacyVersion) {
        return legacyVersion == DUPCODE_STORAGE_LEGACY_VERSION;
    }

    @Override
    public IResultSAXReader getLegacyReader(int legacyVersion) {
        if (isLegacyCompatible(legacyVersion)) {
            return new DupcodeViolationReader(true);
        }
        return null;
    }

    /**
     * Note: this class is public for testing only
     */
    public static class DupcodeViolationReader
            extends AbstractViolationReader
            implements IRuleViolationSAXReader {
        /**
         * @param bLegacySupport
         */
        public DupcodeViolationReader(boolean bLegacySupport) {
            super(bLegacySupport);
        }

        @Override
        protected SourceRange getSourceRange(Map<String, String> attributesMap, String sLocPrefix) {
            int startLine = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_START_LINE_V2_POSTFIX, attributesMap);
            int startLineOffset = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_START_POSITION_V2_POSTFIX,
                    attributesMap);
            int endLine = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_END_LINE_V2_POSTFIX, attributesMap);
            int endLineOffset = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_END_POSITION_V2_POSTFIX,
                    attributesMap);
            return new SourceRange(startLine, startLineOffset, endLine, endLineOffset);
        }

        @Override
        protected IViolation createViolation(Map map, String sAnalyzerId, String sLanguageId, ResultLocation location) {
            List<IPathElement> descriptors = getDescriptors();
            IPathElement[] aDescriptors = (descriptors == null) ? new IPathElement[0] : descriptors.toArray(new IPathElement[descriptors.size()]);

            String sRuleId = getObligatoryString(IXmlTagsAndAttributes.RULE_ATTR);
            String sErrorMessage = getObligatoryString(IXmlTagsAndAttributes.MESSAGE_V2_ATTR);
            return new DupCodeViolation(sRuleId, sAnalyzerId, location, sErrorMessage, sLanguageId, aDescriptors);
        }

        @Override
        protected String getElementTagQName() {
            return IXmlTagsAndAttributes.DUPLICATE_VIOLATION_TAG;
        }

        @Override
        protected IPathElement createViolElemDesc(ResultLocation location, String sElemDescription,
                                                  List<IPathElement> childDescriptors, Map<String, String> properties, Map<String, String> attributesMap,
                                                  List<PathElementAnnotation> annotations) {
            String sSuppressed = attributesMap.get(IXmlTagsAndAttributes.SUPPRESSED_ATTR);
            if (Boolean.parseBoolean(sSuppressed)) {
                return null;
            }
            String sHash = null;
            if (location != null) {
                sHash = attributesMap.remove(IXmlTagsAndAttributes.SOURCE_RANGE_V2_ATTR + IXmlTagsAndAttributes.RESOURCE_HASH_ATTR);
            }
            IPathElement pathElement = new DupCodePathElement(sElemDescription, location);
            if (sHash != null) {
                pathElement.addAttribute(IXmlTagsAndAttributes.LOCATION_HASH_ATTR, sHash);
            }
            return pathElement;
        }

        /**
         * Returns the string value for an attribute.
         *
         * @param sKey String key.
         * @return String value.
         * @pre sKey != null
         * @post $result != null
         */
        private String getObligatoryString(String sKey) {
            String sResult = getString(sKey);
            if (sResult == null) {
                throw new IllegalArgumentException("Could not find parameter for key: " + sKey); //$NON-NLS-1$
            }
            return sResult;
        }
    }

    @Override
    public String getTagName(int xmlVersion) {
        return IXmlTagsAndAttributes.DUPLICATE_VIOLATION_TAG;
    }

    public static final int DUPCODE_STORAGE_VERSION = 1;
    private static final int DUPCODE_STORAGE_LEGACY_VERSION = 2;
}