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

import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;

final public class ViolationRuleUtil {
    /**
     * The informational severity level constant
     */
    public static final int SEVERITY_LOWEST = 5;

    /**
     * The informational severity level constant
     */
    public static final int SEVERITY_LOW = 4;

    /**
     * The informational severity level constant
     */
    public static final int SEVERITY_MEDIUM = 3;

    /**
     * The informational severity level constant
     */
    public static final int SEVERITY_HIGH = 2;

    /**
     * The severe violation severity level constant
     */
    public static final int SEVERITY_HIGHEST = 1;

    /**
     * The invalid severity constant
     */
    public static int INVALID_SEVERITY = -1;

    private ViolationRuleUtil() {
    }

    public static int getSeverity(IViolation violation) {
        int severity = INVALID_SEVERITY;
        if (violation == null) {
            return severity;
        }
        String sSeverity = violation.getAttribute(IXmlTagsAndAttributes.SEVERITY_ATTR);
        if ((sSeverity != null) && (sSeverity.trim().length() > 0)) {
            try {
                severity = Integer.parseInt(sSeverity);
            } catch (NumberFormatException nfe) {
                Logger.getLogger().warn("Integer severity value expected but got: " + sSeverity); //$NON-NLS-1$
            }
        }
        return severity;
    }

    public static void setSeverity(IViolation violation, int severity) {
        if ((severity > SEVERITY_LOWEST) || (severity < SEVERITY_HIGHEST)) {
            violation.addAttribute(IXmlTagsAndAttributes.SEVERITY_ATTR, null);
        }
        violation.addAttribute(IXmlTagsAndAttributes.SEVERITY_ATTR, Integer.toString(severity));
    }

    public static String getRuleCategory(IViolation violation) {
        return violation.getAttribute(IXmlTagsAndAttributes.RULE_CATEGORY_ATTR);
    }

    public static String getRuleTitle(IViolation violation) {
        return violation.getAttribute(IXmlTagsAndAttributes.RULE_HEADER_ATTR);
    }

}