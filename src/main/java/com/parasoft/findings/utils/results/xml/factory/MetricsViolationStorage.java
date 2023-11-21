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

import java.util.Map;

import com.parasoft.findings.utils.results.violations.MetricsViolation;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.xml.*;
import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.common.util.XMLUtil;

/**
 * Implementation of IResultXmlStorage for metrics violations.
 */

class MetricsViolationStorage
        implements IViolationXmlStorage, ILegacySupportResultXmlStorage {

    @Override
    public String getResultId() {
        return IResultsIdentifiersConstants.METRICS_VIOLATION_ID;
    }

    @Override
    public IResultSAXReader getReader(int xmlVersion) {
        return new MetricsViolationReader(false);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MetricsViolationStorage)) {
            return false;
        }
        return MetricsViolationStorage.class.equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return MetricsViolationStorage.class.getName().hashCode();
    }

    /**
     * Reader of MetricsViolationReader
     */
    protected static class MetricsViolationReader
            extends AbstractViolationReader
            implements IRuleViolationSAXReader {
        protected MetricsViolationReader(boolean bLegacySupport) {
            super(bLegacySupport);
        }

        @Override
        protected String getElementTagQName() {
            return IXmlTagsAndAttributes.METRICS_VIOLATION_TAG;
        }

        @Override
        protected IViolation createViolation(Map<String, String> map, String sAnalyzerId,
                                             ResultLocation location) {
            String sRuleId = getObligatoryString(IXmlTagsAndAttributes.RULE_ATTR);
            String sErrorMessage = getObligatoryString(IXmlTagsAndAttributes.MESSAGE_V2_ATTR);
            return new MetricsViolation(sRuleId, sAnalyzerId, location, sErrorMessage);
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
    }

    @Override
    public String getTagName(int xmlVersion) {
        return IXmlTagsAndAttributes.METRICS_VIOLATION_TAG;
    }

    @Override
    public int getVersion() {
        return METRICS_STORAGE_VERSION;
    }

    @Override
    public boolean isCompatible(int version) {
        return version <= getVersion();
    }

    @Override
    public int getLegacyVersion() {
        return METRICS_STORAGE_LEGACY_VERSION;
    }

    @Override
    public boolean isLegacyCompatible(int legacyVersion) {
        return legacyVersion == METRICS_STORAGE_LEGACY_VERSION;
    }

    @Override
    public IResultSAXReader getLegacyReader(int legacyVersion) {
        if (isLegacyCompatible(legacyVersion)) {
            return new MetricsViolationReader(true);
        }
        return null;
    }

    public static final int METRICS_STORAGE_VERSION = 1;
    private static final int METRICS_STORAGE_LEGACY_VERSION = 2;

}