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

/**
 * Represent a violation, such as:
 * pattern based violation, flow analysis violation, etc..
 */
public interface IViolation
        extends IAttributedEntity, IResult {
    /**
     * Gets identifier of analyzer which generated this result
     *
     * @return analyzer identifier
     */
    String getAnalyzerId();

    /**
     * Gets location of the resource in which violation occurred.
     *
     * @return the resource location, cannot be <code>null</code>
     */
    ResultLocation getResultLocation();
}