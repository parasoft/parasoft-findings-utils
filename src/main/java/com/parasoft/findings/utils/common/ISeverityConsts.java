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

package com.parasoft.findings.utils.common;

/**
 * Defines violation severity levels.
 */
public interface ISeverityConsts
{
    // Note: Severity is repeated at com.parasoft.grs.integration.codereview2.ESeverity
    //       please notify Concerto team if something will change here

    // violation severity levels

    /** The informational severity level constant */
    int SEVERITY_LOWEST = 5;

    /** The possible violation severity level constant */
    int SEVERITY_LOW = 4;

    /** The violation severity level constant */
    int SEVERITY_MEDIUM = 3;

    /** The possible severe violation severity level constant */
    int SEVERITY_HIGH = 2;

    /** The severe violation severity level constant */
    int SEVERITY_HIGHEST = 1;

    /** Sorted list of all possible severity values. Sorted from most important to less important */
    int[] SEVERITY_LIST = {
            SEVERITY_HIGHEST,
            SEVERITY_HIGH,
            SEVERITY_MEDIUM,
            SEVERITY_LOW,
            SEVERITY_LOWEST
    };

    /** The invalid severity constant */
    int INVALID_SEVERITY = -1;

} // interface ISeverityConsts
