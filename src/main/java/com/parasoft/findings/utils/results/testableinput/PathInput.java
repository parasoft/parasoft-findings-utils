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

public final class PathInput
        implements ITestableInput {
    private final String _sPath;
    private final String _sFSPath;
    private final String _sProjectName;
    private final String _sProjectPath;

    public PathInput(String sPath) {
        this(sPath, null, null, null);
    }

    public PathInput(String sPath, String sFSPath, String sProjectName, String sProjectPath) {
        _sPath = sPath;
        _sFSPath = sFSPath;
        _sProjectName = sProjectName;
        _sProjectPath = sProjectPath;
    }

    public String getName() {
        int lastSeparator = _sPath.lastIndexOf(ProjectFileTestableInput.PATH_SEPARATOR);
        return _sPath.substring(lastSeparator);
    }

    public String getPath() {
        return _sPath;
    }

    public String getProjectName() {
        return _sProjectName;
    }

    public String getProjectPath() {
        return _sProjectPath;
    }

    public String getFileSystemPath() {
        return _sFSPath;
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PathInput)) {
            return false;
        }
        return ((PathInput) obj).getPath().equals(getPath());
    }
}