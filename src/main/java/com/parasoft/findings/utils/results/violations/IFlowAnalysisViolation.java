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

public interface IFlowAnalysisViolation
        extends IRuleViolation {
    /**
     * Gets path element descriptors.
     *
     * @return the array with path flow analysis element descriptors
     * @post $result != null
     * @post $result.length > 0
     */
    IFlowAnalysisPathElement[] getPathElements();

    /**
     * @return Message describing violation cause.
     * @post $result != null
     * @deprecated As 10.4.1 has annotations instead
     */
    @Deprecated
    String getCauseMessage();

    /**
     * @return Message describing violation point.
     * @post $result != null
     * @deprecated As 10.4.1 has annotations instead
     */
    @Deprecated
    String getPointMessage();

}