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
 * The interface for path elements.
 */
public interface IPathElement
        extends IAttributedEntity {

    /**
     * Gets path element description
     *
     * @return element description, <code>null</code> if not available.
     */
    String getDescription();

    /**
     * Gets the associated result location
     *
     * @return element location or <code>null</code> if element does not refer to source.
     */
    ResultLocation getLocation();

    /**
     * Gets child elements.
     *
     * @return the child elements array, if no children empty array is returned
     * @post $result != null
     */
    IPathElement[] getChildren();

}