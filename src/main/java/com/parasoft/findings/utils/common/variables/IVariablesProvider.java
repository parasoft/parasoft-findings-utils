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
 * Provides available variables.
 */
public interface IVariablesProvider {
    /**
     * Returns a variable for the specified name
     *
     * @param sName name of the variable to return
     * @return the value variable with the specified name or null
     * @pre sName != null
     */
    StaticVariable getVariable(String sName);

}
