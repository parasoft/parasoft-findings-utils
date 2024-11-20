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

import java.util.*;

public class RuleDescriptionBody
{
    protected final List<BodyElement> _elements = new ArrayList<BodyElement>();

    public List<BodyElement> getElements()
    {
        return _elements;
    }

    public boolean isEmpty()
    {
        return _elements.isEmpty();
    }

    public static class BodyElement
    {
        private final String _sName;

        private Map<String, String> _attributes = null;
        private List<BodyElement> _children = null;

        public BodyElement(String sName)
        {
            _sName = sName;
        }

        public String getName()
        {
            return _sName;
        }

        public Map<String, String> getAttributes()
        {
            if (_attributes == null) {
                return Collections.emptyMap();
            }
            return _attributes;
        }

        public List<BodyElement> getChildren()
        {
            if (_children == null) {
                return Collections.emptyList();
            }
            return _children;
        }

        public void addAttribute(String sName, String sValue)
        {
            if (_attributes == null) {
                _attributes = new HashMap<String, String>();
            }
            _attributes.put(sName, sValue);
        }

        public void addChild(BodyElement element)
        {
            if (_children == null) {
                _children = new ArrayList<BodyElement>();
            }
            _children.add(element);
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append('<').append(getName());
            for (Map.Entry<String, String> entry : getAttributes().entrySet()) {
                sb.append(' ').append(entry.getKey());
                sb.append('=').append(entry.getValue());
            }
            sb.append('>');
            return sb.toString();
        }
    }
}
