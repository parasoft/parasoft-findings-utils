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

package com.parasoft.findings.utils.results.violations;

import java.util.Arrays;

public class FlowAnalysisViolation
        extends AbstractCodingStandardsViolation
        implements IFlowAnalysisViolation {
    private final String _sRuleId;

    private final String _sCauseMessage;
    private final String _sPointMessage;

    private final IFlowAnalysisPathElement[] _aElementDescriptors;

    public FlowAnalysisViolation(String sRuleId, String sAnalyzerId, String sLanguageId,
                                 ResultLocation resultLocation,
                                 String sMessage, String sPackage, String sCauseMessage,
                                 String sPointMessage,
                                 IFlowAnalysisPathElement[] aElementDescriptors) {
        super(sAnalyzerId, sLanguageId, resultLocation, sMessage);

        _sRuleId = sRuleId;
        _sCauseMessage = sCauseMessage;
        _sPointMessage = sPointMessage;
        _aElementDescriptors = aElementDescriptors;
        setPackage(sPackage);
    }

    @Override
    public String getRuleId() {
        return _sRuleId;
    }

    public IFlowAnalysisPathElement[] getPathElements() {
        return _aElementDescriptors;
    }

    public String getCauseMessage() {
        return _sCauseMessage;
    }

    public String getPointMessage() {
        return _sPointMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FlowAnalysisViolation)) {
            return false;
        }
        FlowAnalysisViolation violation = (FlowAnalysisViolation) obj;
        return super.equals(violation) && Arrays.equals(getPathElements(), violation.getPathElements());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}