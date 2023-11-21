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

import java.util.HashMap;
import java.util.Map;

import com.parasoft.findings.utils.common.util.ObjectUtil;
import com.parasoft.findings.utils.common.util.IntegerUtil;
import com.parasoft.findings.utils.common.util.StringUtil;

/**
 * Abstract violation implementation.
 */
public abstract class AbstractViolation
        implements IResult, IViolation, Comparable {
    /**
     * Result attributes
     */
    private final Map<String, String> _attributes = new HashMap<String, String>();

    /**
     * The package/namespace of this violation or <tt>null</tt> if value will be computed base
     * on IModelElement or IResultLocation
     */
    private String _sPackage = null;

    /**
     * location for this violation
     */
    private ResultLocation _location = null;

    private final String _sAnalyzerId;

    /**
     * The result error message.
     */
    protected String _sMessage;

    /**
     * Constructor.
     *
     * @param sAnalyzerId the tool identifier
     * @param location    violation's location
     * @param sMessage    the error message
     * @pre sLanguageId != null
     * @pre location != null
     * @pre sMessage != null
     * @pre sAnalyzerId != null
     */
    protected AbstractViolation(String sAnalyzerId, final ResultLocation location, final String sMessage) {
        super();
        if (sMessage == null) {
            throw new IllegalArgumentException("Illegal null violation message."); //$NON-NLS-1$
        }
        _sMessage = sMessage;
        _sAnalyzerId = sAnalyzerId;
        _location = location;
    }

    public void addAttribute(String sName, String sValue) {
        _attributes.put(sName, sValue);
    }

    public String getAttribute(String sName) {
        return _attributes.get(sName);
    }

    @Override
    public String getAnalyzerId() {
        return _sAnalyzerId;
    }

    @Override
    public String getMessage() {
        return _sMessage;
    }

    public String getNamespace() {
        return _sPackage;
    }

    /**
     * @param sPackage
     */
    public void setPackage(String sPackage) {
        _sPackage = sPackage;
    }

    @Override
    public int compareTo(Object object) {
        int other_severity = ViolationRuleUtil.getSeverity((IViolation) object);
        return IntegerUtil.compare(ViolationRuleUtil.getSeverity(this), other_severity);
    }

    @Override
    public ResultLocation getResultLocation() {
        return _location;
    }

    public String getRuleId() {
        return null;
    }

    @Override
    public int hashCode() {
        int result = (getMessage() == null ? 0 : getMessage().hashCode());
        result = 31 * result + (getResultLocation() == null ? 0 : getResultLocation().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractViolation)) {
            return false;
        }
        AbstractViolation violation = (AbstractViolation) obj;
        return StringUtil.equals(getMessage(), violation.getMessage()) &&
                ObjectUtil.equals(getResultLocation(), violation.getResultLocation());
    }
}