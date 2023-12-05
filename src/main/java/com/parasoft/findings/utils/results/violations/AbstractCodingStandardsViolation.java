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

import com.parasoft.findings.utils.common.util.ObjectUtil;

/**
 * Abstract base class for coding standard violations.
 */
public abstract class AbstractCodingStandardsViolation
        extends AbstractViolation
        implements IRuleViolation {
    /**
     * @param sToolId       the tool identifier
     * @param location      the violation location
     * @param sErrorMessage the error message
     * @pre sToolId != null
     * @pre sLanguageId != null
     * @pre location != null
     * @pre sErrorMessage != null
     */
    protected AbstractCodingStandardsViolation(final String sToolId, final String sLanguageId,
                                               final ResultLocation location,
                                               final String sErrorMessage) {
        super(sToolId, sLanguageId, location, sErrorMessage);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractCodingStandardsViolation)) {
            return false;
        }
        AbstractCodingStandardsViolation violation = (AbstractCodingStandardsViolation) obj;

        return getMessage().equals(violation.getMessage())
                && ObjectUtil.equals(getRuleId(), violation.getRuleId())
                && areLocationsEqual(violation);
    }

    /**
     * TODO correct comparison of location would be welcome.
     * Currently, this default implementation try to use model source range equals method
     * which is incorrect, since, f. e., loaded from XML violation location would be identical,
     * but have different class and this check will return false...
     *
     * @param violation
     * @return <code>true</code> if given violation location equals to this violation location
     * @pre violation != null
     */
    public boolean areLocationsEqual(AbstractCodingStandardsViolation violation) {
        return ObjectUtil.equals(getResultLocation(), violation.getResultLocation());
    }

    @Override
    public int hashCode() {
        int result = getMessage().hashCode();
        result = 31 * result + (getRuleId() == null ? 0 : getRuleId().hashCode());
        result = 31 * result + (getResultLocation() == null ? 0 : getResultLocation().hashCode());
        return result;
    }

}