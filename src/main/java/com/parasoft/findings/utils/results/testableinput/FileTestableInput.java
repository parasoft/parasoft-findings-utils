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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of {@link IFileTestableInput}
 */
public class FileTestableInput
        implements IFileTestableInput {
    private final File _file;
    private final Map<String, String> _attrs;

    /**
     * Creates file testable for given file.
     *
     * @param file local file
     * @pre file != null
     */
    public FileTestableInput(File file) {
        _file = file;
        _attrs = new HashMap<>();
    }

    @Override
    public String getName() {
        return _file.getName(); // parasoft-suppress BD.EXCEPT.NP "reviewed"
    }

    @Override
    public void addAttribute(String sName, String sValue) {
        _attrs.put(sName, sValue);
    }

    @Override
    public String getAttribute(String sName) {
        return _attrs.get(sName);
    }

    @Override
    public File getFileLocation() {
        return _file;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + _file + ']'; //$NON-NLS-1$
    }

    @Override
    public int hashCode() {
        return _file.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileTestableInput)) {
            return false;
        }
        return _file.equals(((FileTestableInput) obj)._file);
    }
}