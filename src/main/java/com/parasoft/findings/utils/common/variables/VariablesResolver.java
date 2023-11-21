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

package com.parasoft.findings.utils.common.variables;

/**
 * Base implementation of <code>IVariablesResolver</code>.
 */
public class VariablesResolver {
    private IVariablesProvider _variablesProvider = null;

    public VariablesResolver(IVariablesProvider variablesProvider) {
        _variablesProvider = variablesProvider;
    }

    public String performSubstitution(String sExpression) {
        if ((sExpression == null) || (sExpression.length() < 1)) {
            return sExpression;
        }
        return new StringSubstitutionEngine(_variablesProvider).performStringSubstitution(sExpression, false, true);
    }

}