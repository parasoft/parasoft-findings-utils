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

public class DupCodePathElement
        implements IPathElement {
    private final ResultLocation _location;
    private final String _description;
    private final Map<String, String> _attrs = new HashMap<String, String>();

    public DupCodePathElement(String desc, ResultLocation location) {
        _description = desc;
        _location = location;
    }

    public String getDescription() {
        return _description;
    }

    public ResultLocation getLocation() {
        return _location;
    }

    public IPathElement[] getChildren() {
        return new IPathElement[0];
    }

    public void addAttribute(String sName, String sValue) {
        _attrs.put(sName, sValue);
    }

    public String getAttribute(String sName) {
        return _attrs.get(sName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DupCodePathElement)) {
            return false;
        }
        DupCodePathElement dupCodeElem = (DupCodePathElement) obj;
        if (!ObjectUtil.equals(dupCodeElem.getDescription(), _description)) {
            return false;
        }
        return ObjectUtil.equals(dupCodeElem.getLocation(), _location);
    }

    @Override
    public int hashCode() {
        return ((_description != null) ? _description.hashCode() : 0) +
                31 * ((_location != null) ? _location.hashCode() : 0);
    }

}