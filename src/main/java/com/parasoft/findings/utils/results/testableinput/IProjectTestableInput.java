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

public interface IProjectTestableInput
        extends IAttributedEntity, ITestableInput {
    /**
     * Returns name of the project.
     * E.g. "MyEclipseProject" or "MyVisualStudioProject"
     * <p>
     * The name must not contain '/' character.
     *
     * @return project name
     * @post $result != null
     */
    String getProjectName();

    /**
     * Returns a path of this project in the workspace/solution.
     * The path shows relation in workspace/solution, but not necessarily relation in the file system.
     * <p>
     * The path uses '/' separators, but it does not end with '/'.
     * The path contains project name as last element.
     * <p>
     * If path contains only one element which is project name, it starts with '/'
     * otherwise starts from first path segment element name.
     * Returns e.g. "/MyProject"
     * or "MySolution/MySolutionFolder/MyProject"
     *
     * @return path of project in the workspace/solution
     * @post $result != null
     */
    String getProjectPath();

    /**
     * Returns a path of this file relative to the parent project.
     * The path shows relation in workspace/solution, but not necessarily relation in the file system.
     * Relation in the file system may be different, because files may be linked inside projects.
     * The last element of the path is file name.
     * <p>
     * The path uses '/' separators, but it neither starts nor ends with '/'.
     * <p>
     * Returns e.g. "MyJavaClass.java" or "MyFolderFoo/MyFolderBar/MyFile.cs"
     *
     * @return path of file relative to the parent project
     * @post $result != null
     */
    String getProjectRelativePath();

    /**
     * The path separator.
     */
    String PATH_SEPARATOR = "/"; //$NON-NLS-1$
}