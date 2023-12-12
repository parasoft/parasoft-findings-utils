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

import com.parasoft.findings.utils.common.util.StringUtil;

public class RuleViolation
        extends AbstractViolation
        implements IRuleViolation {
    private final String _sRuleId;

    public RuleViolation(String sAnalyzerId, String sLanguageId, ResultLocation location,
                         String sMessage, String sRuleId, String sPackage) {
        super(sAnalyzerId, sLanguageId, location, sMessage);
        _sRuleId = sRuleId;
        setPackage(sPackage);
    }

    @Override
    public String getRuleId() {
        return _sRuleId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = result * 31 + ((getRuleId() == null) ? 0 : getRuleId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RuleViolation)) {
            return false;
        }
        RuleViolation violation = (RuleViolation) obj;
        return super.equals(violation) && StringUtil.equals(getRuleId(), violation.getRuleId());
    }

}