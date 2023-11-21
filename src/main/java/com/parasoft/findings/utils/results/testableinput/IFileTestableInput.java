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

package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.IAttributedEntity;

import java.io.File;

/**
 * Represents an element of a testable scope, such as local file.
 */
public interface IFileTestableInput
        extends IAttributedEntity, ITestableInput {
    /**
     * Returns location of this element in file system.
     *
     * @return file system location of this element
     * @post $result != null
     */
    File getFileLocation();

}