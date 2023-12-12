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

/**
 * Available scopes that can be declared by rule.
 * @since 10.0
 */
public enum RuleScope
{
    line,
    method,
    clazz("class"),  //$NON-NLS-1$
    file,
    namespace("package"),  //$NON-NLS-1$
    project;

    private final String [] _valueVariants;

    RuleScope(String ... valueVariants)
    {
        _valueVariants = valueVariants;
    }

    /**
     * @return all available naming variants of a scope
     */
    public String [] variants()
    {
        return _valueVariants;
    }

    /**
     * Adapts given string to one of known scopes or its variants.
     *
     * @param sValue the value to adapt
     * @return corresponding scope that matches given value or null
     */
    public static RuleScope adapt(String sValue)
    {
        if (sValue == null) {
            return null;
        }

        sValue = sValue.toLowerCase();
        for (RuleScope scope : values()) {

            if (sValue.equals(scope.name())) {
                return scope;
            }

            for (String sVariant : scope.variants()) {
                if (sValue.equals(sVariant)) {
                    return scope;
                }
            }
        }

        return null;
    }
}