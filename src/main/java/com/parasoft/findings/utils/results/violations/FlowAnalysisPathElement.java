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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.parasoft.findings.utils.common.util.ObjectUtil;
import com.parasoft.findings.utils.common.util.StringUtil;

public class FlowAnalysisPathElement
        implements IFlowAnalysisPathElement {
    private final String _sDescription;

    private final ResultLocation _location;

    private final Map<String, String> _attributes;

    private final IFlowAnalysisPathElement[] _aChildren;

    private final String _sThrownTypes;

    private final String _sThrowingMethod;

    private final List<PathElementAnnotation> _annotations;

    private final Type _type;

    public FlowAnalysisPathElement(String sDescription, ResultLocation location, Map<String, String> attributes,
                                   IFlowAnalysisPathElement[] aChildren, Type type, String sThrownTypes, String sThrowingMethod,
                                   List<PathElementAnnotation> annotations) {
        _sDescription = sDescription;
        _location = location;
        _attributes = (attributes == null) ? new LinkedHashMap<String, String>() : attributes;
        _aChildren = aChildren;
        _type = type;
        _sThrownTypes = sThrownTypes;
        _sThrowingMethod = sThrowingMethod;
        _annotations = (annotations == null) ? new ArrayList<PathElementAnnotation>() : annotations;
    }

    public String getDescription() {
        return _sDescription;
    }

    public ResultLocation getLocation() {
        return _location;
    }

    public void addAttribute(String sName, String sValue) {
        _attributes.put(sName, sValue);
    }

    public String getAttribute(String sName) {
        return _attributes.get(sName);
    }

    public IFlowAnalysisPathElement[] getChildren() {
        return _aChildren;
    }

    public List<PathElementAnnotation> getAnnotations() {
        return _annotations;
    }

    public String getThrownTypes() {
        return _sThrownTypes;
    }

    public String getThrowingMethod() {
        return _sThrowingMethod;
    }

    public Type getType() {
        return _type;
    }

    @Override
    public int hashCode() {
        return computeHashCode(_type, _sDescription, _location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FlowAnalysisPathElement)) {
            return false;
        }
        FlowAnalysisPathElement pathElem = (FlowAnalysisPathElement) obj;
        return ObjectUtil.equals(_type, pathElem._type) && StringUtil.equals(_sDescription, pathElem._sDescription)
                && ObjectUtil.equals(_location, pathElem._location) && Arrays.equals(_aChildren, pathElem._aChildren);
    }

    private static int computeHashCode(Type type, String sDescription, ResultLocation location) {
        return type.hashCode() + 11 * (sDescription != null ? sDescription.hashCode() : 0) + 31 * (location != null ? location.hashCode() : 0);
    }

    public static class TypeImpl
            implements IFlowAnalysisPathElement.Type {
        private String _sType = null;

        /**
         * @param sType
         * @pre sType != null
         */
        public TypeImpl(String sType) {
            _sType = sType;
        }

        public String getIdentifier() {
            return _sType;
        }

        @Override
        public int hashCode() {
            return _sType.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IFlowAnalysisPathElement.Type)) {
                return false;
            }
            String sId = ((IFlowAnalysisPathElement.Type) obj).getIdentifier();
            return _sType.equals(sId);
        }
    }
}