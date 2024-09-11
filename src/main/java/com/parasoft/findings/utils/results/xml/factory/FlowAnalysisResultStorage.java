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

import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.results.violations.*;
import com.parasoft.findings.utils.results.xml.*;

import com.parasoft.findings.utils.common.util.XMLUtil;

/**
 * Flow analysis result storage implementation.
 */
final class FlowAnalysisResultStorage
        implements IViolationXmlStorage, ILegacySupportResultXmlStorage {

    @Override
    public IResultSAXReader getReader(int xmlVersion) {
        return new FlowAnalysisViolationReader(false);
    }

    @Override
    public String getResultId() {
        return IResultsIdentifiersConstants.FLOW_ANALYSIS_RESULT_ID;
    }

    @Override
    public int getVersion() {
        return FLOWANALYSIS_STORAGE_VERSION;
    }

    @Override
    public boolean isCompatible(int version) {
        return version <= getVersion();
    }

    @Override
    public int getLegacyVersion() {
        return FLOWANALYSIS_STORAGE_LEGACY_VERSION;
    }

    @Override
    public boolean isLegacyCompatible(int legacyVersion) {
        return legacyVersion == FLOWANALYSIS_STORAGE_LEGACY_VERSION;
    }

    @Override
    public IResultSAXReader getLegacyReader(int legacyVersion) {
        if (isLegacyCompatible(legacyVersion)) {
            return new FlowAnalysisViolationReader(true);
        }
        return null;
    }

    /**
     * Note: this class is public for testing only
     */
    public static class FlowAnalysisViolationReader
            extends AbstractViolationReader
            implements IRuleViolationSAXReader {
        /**
         * @param bLegacySupport
         */
        public FlowAnalysisViolationReader(boolean bLegacySupport) {
            super(bLegacySupport);
        }

        @Override
        protected SourceRange getSourceRange(Map<String, String> attributesMap, String sLocPrefix) {
            int startLine = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_START_LINE_V2_POSTFIX, attributesMap);
            int startLineOffset = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_START_POSITION_V2_POSTFIX, attributesMap);
            int endLine = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_END_LINE_V2_POSTFIX, attributesMap);
            int endLineOffset = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_END_POSITION_V2_POSTFIX, attributesMap);
            return new SourceRange(startLine, startLineOffset, endLine, endLineOffset);
        }

        @Override
        protected IViolation createViolation(Map map, String sAnalyzerId, String sLanguageId, ResultLocation location) {
            List<IPathElement> descriptors = getDescriptors();
            IFlowAnalysisPathElement[] aDescriptors = (descriptors == null) ? new IFlowAnalysisPathElement[0]
                    : descriptors.toArray(new IFlowAnalysisPathElement[descriptors.size()]);

            String sRuleId = getObligatoryString(IXmlTagsAndAttributes.RULE_ATTR);

            String sErrorMessage = getObligatoryString(IXmlTagsAndAttributes.MESSAGE_V2_ATTR);

            String sPackage = getString(IXmlTagsAndAttributes.PACKAGE_ATTR);
            return new FlowAnalysisViolation(sRuleId, sAnalyzerId, sLanguageId, location, sErrorMessage, sPackage,
                    aDescriptors);
        }

        @Override
        protected String getElementTagQName() {
            return IXmlTagsAndAttributes.FLOW_ANALYSIS_VIOLATION_V2_TAG;
        }

        @Override
        protected IPathElement createViolElemDesc(ResultLocation location, String sElemDescription, List<IPathElement> childDescriptors,
                                                  Map<String, String> attributesMap, List<PathElementAnnotation> annotations) {
            final String sType = attributesMap.remove(IXmlTagsAndAttributes.VIOLATION_ELEMENT_TYPE_V2_TAG);
            if (StringUtil.isEmpty(sType)) {
                throw new IllegalArgumentException("Illegal value for " + IXmlTagsAndAttributes.VIOLATION_ELEMENT_TYPE_V2_TAG + " attribute: " + sType);
            }
            String sThrownTypes = attributesMap.remove(IXmlTagsAndAttributes.VIOLATION_ELEMENT_THROWN_TYPES);
            String sThrowingMethod = attributesMap.remove(IXmlTagsAndAttributes.VIOLATION_ELEMENT_THROWING_METHOD);
            IFlowAnalysisPathElement[] aChildDescriptors = (childDescriptors == null) ? new IFlowAnalysisPathElement[0]
                    : childDescriptors.toArray(new IFlowAnalysisPathElement[childDescriptors.size()]);

            if (location != null) {
                String sHash = attributesMap.remove(IXmlTagsAndAttributes.SOURCE_RANGE_V2_ATTR + IXmlTagsAndAttributes.RESOURCE_HASH_ATTR);
                if (sHash != null) {
                    attributesMap.put(IXmlTagsAndAttributes.LOCATION_HASH_ATTR, sHash);
                }
            }
            return new FlowAnalysisPathElement(sElemDescription, location, attributesMap, aChildDescriptors,
                    new FlowAnalysisPathElement.TypeImpl(sType), sThrownTypes, sThrowingMethod, annotations);
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
        return IXmlTagsAndAttributes.FLOW_ANALYSIS_VIOLATION_V2_TAG;
    }

    public static final int FLOWANALYSIS_STORAGE_VERSION = 1;

    private static final int FLOWANALYSIS_STORAGE_LEGACY_VERSION = 2;

}