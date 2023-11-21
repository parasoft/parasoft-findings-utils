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

package com.parasoft.findings.utils.results.xml;

import org.xml.sax.ContentHandler;

import com.parasoft.findings.utils.results.violations.IResult;

/**
 * Interface for a sax reader of IResults.
 */
public interface IResultSAXReader
        extends ContentHandler {
    /**
     * This method should return a concreate IResult readed by this reader.
     * Should return an instance after last xml ending tag of this result is read,
     * otherwise <code>null</code>.
     *
     * @return a concreate IResult readed by this reader.
     */
    IResult getReadResult();

    String ILLEGAL_TAG_MESSAGE = "Tag with illegal name spotted.";  //$NON-NLS-1$

    String ATTRIBUTE_MISSING = "Expected attribute missing"; //$NON-NLS-1$
}