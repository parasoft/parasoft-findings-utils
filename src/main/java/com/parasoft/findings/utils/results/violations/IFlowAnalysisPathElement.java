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

import java.util.List;
import java.util.Map;

public interface IFlowAnalysisPathElement
        extends IPathElement {
    char CAUSE = 'C';
    char IMPORTANT_ELEMENT = '!';
    char POINT = 'P';
    char RULE = 'I';
    char THROWING_CHAR = 'E';

    IFlowAnalysisPathElement[] getChildren();

    /**
     * Returns list of annotations for this element.
     *
     * @return list of annotations
     */
    List<PathElementAnnotation> getAnnotations();

    /**
     * Gets additional descriptor's properties that may be useful to the end-user.
     * <p>
     * Properties are pairs of strings where key is the name of the property and
     * value is its value. Property name is guaranteed to be prepared for the end
     * user (it is already i18ned, for example).
     *
     * @return The properties mappings.
     * @post $result != null
     * <p>
     * /**
     * @deprecated reason this method is deprecated </br>
     * {will be removed in 10.6.x version} </br>
     * as properties will not be supported (annotations instead)
     */
    @Deprecated
    Map<String, String> getProperties();

    /**
     * @return list of thrown type, <code>null</code> if nothing is thrown
     */
    String getThrownTypes();

    /**
     * @return String the throwing method
     */
    String getThrowingMethod();

    // LINE ======================================

    /*
     * DTP : Above the LINE are placed method that should be in interface
     * Below the line we have got method and interfaces that should be to get code compilable before fully re-factoring it.
     * There could be also methods that could be moved above the line if we assume that there are important.
     */
    interface Type // parasoft-suppress JAVADOC.PJDC "Temporary api for internal purpose."
    {
        String getIdentifier(); // parasoft-suppress JAVADOC.PJDM "Temporary api for internal purpose."
    }

    Type getType(); // parasoft-suppress JAVADOC.PJDM "Temporary api for internal purpose."
}