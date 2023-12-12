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

package com.parasoft.findings.utils.rules;

import java.util.HashMap;
import java.util.Map;

import com.parasoft.findings.utils.common.util.StringUtil;

public class RuleDescription
{
    private String _sRuleId = null;
    private String _sHeader = null;

    private int _severity = -1;
    private char _separator = '.';

    private Map<String, String> _attributes = null;
    private RuleDescriptionBody _body = null;

    /**
     * Creates empty rule description
     */
    protected RuleDescription()
    {
    }

    public String getRuleId()
    {
        return _sRuleId;
    }

    public String getCategoryId()
    {
        String sCategoryId = RuleUtil.getParentId(_sRuleId, _separator);
        if (StringUtil.isEmptyTrimmed(sCategoryId)) {
            return CategoryDescription.UNKNOWN_CATEGORY;
        }
        return sCategoryId;
    }

    public String getHeader()
    {
        return _sHeader;
    }

    public int getSeverity()
    {
        return _severity;
    }

    public char getSeparator()
    {
        return _separator;
    }

    public RuleDescriptionBody getBody()
    {
        return _body;
    }

    protected void setRuleId(String sRuleId)
    {
        _sRuleId = sRuleId;
    }

    protected void setHeader(String sHeader)
    {
        if (sHeader != null) {
            sHeader = sHeader.trim();
            if (sHeader.endsWith(".")) {  //$NON-NLS-1$
                sHeader = sHeader.substring(0, sHeader.length()-1);
            }
        }
        _sHeader = sHeader;
    }

    protected void setSeverity(int severity)
    {
        _severity = severity;
    }

    protected void setSeparator(char separator)
    {
        _separator = separator;
    }

    protected void addAttribute(String sName, String sValue)
    {
        if (_attributes == null) { // parasoft-suppress TRS.ILI "Verified"
            _attributes = new HashMap<String, String>();
        }
        _attributes.put(sName, sValue);
    }

    protected synchronized void setBody(RuleDescriptionBody body)
    {
        _body = body;
    }

    @Override
    public String toString()
    {
        return "Rule: " + getRuleId(); //$NON-NLS-1$
    }

}
