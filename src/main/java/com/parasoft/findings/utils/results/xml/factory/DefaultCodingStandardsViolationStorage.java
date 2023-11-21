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

import com.parasoft.findings.utils.results.violations.RuleViolation;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.xml.*;
import com.parasoft.findings.utils.results.violations.IRuleViolation;
import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.common.util.XMLUtil;

/**
 * Default concrete storage for coding standards that returns null reader.
 */
class DefaultCodingStandardsViolationStorage
        implements IViolationXmlStorage, ILegacySupportResultXmlStorage {
    private static final int LEGACY_VERSION = 2;

    /**
     * Constructor.
     */
    public DefaultCodingStandardsViolationStorage() {
        super();
    }

    public final int getVersion() {
        return VERSION;
    }

    public boolean isCompatible(int version) {
        return version <= getVersion();
    }

    private final static int VERSION = 1;

    public IResultSAXReader getReader(int xmlVersion) {
        return new RuleViolationReader(false);
    }

    public String getResultId() {
        return IResultsIdentifiersConstants.CODING_STANDARDS_RESULT_ID;
    }

    public String getTagName(int xmlVersion) {
        return IXmlTagsAndAttributes.STANDARDS_VIOLATION_V2_TAG;
    }

    public int getLegacyVersion() {
        return LEGACY_VERSION;
    }

    public boolean isLegacyCompatible(int legacyVersion) {
        return legacyVersion == LEGACY_VERSION;
    }

    public IResultSAXReader getLegacyReader(int legacyVersion) {
        if (isLegacyCompatible(legacyVersion)) {
            return new RuleViolationReader(true);
        }
        return null;
    }

    /**
     * Reader of CodingStandardsViolations
     */
    public static class RuleViolationReader
            extends AbstractViolationReader
            implements IRuleViolationSAXReader {
        public RuleViolationReader(boolean bLegacySupport) {
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
        public String getElementTagQName() {
            return IXmlTagsAndAttributes.STANDARDS_VIOLATION_V2_TAG;
        }

        @Override
        protected IViolation createViolation(Map<String, String> map,
                                             String sAnalyzerId, ResultLocation location) {
            String sErrorMessage = getString(IXmlTagsAndAttributes.MESSAGE_V2_ATTR);
            String sRuleId = getString(IXmlTagsAndAttributes.RULE_ATTR);
            String sPackage = getString(IXmlTagsAndAttributes.PACKAGE_ATTR);

            IRuleViolation violation = new RuleViolation(sAnalyzerId, location, sErrorMessage, sRuleId, sPackage);

            return violation;
        }
    }

}