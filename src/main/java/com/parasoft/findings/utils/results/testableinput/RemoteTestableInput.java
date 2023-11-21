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

import java.util.HashMap;
import java.util.Map;

public class RemoteTestableInput
        implements IAttributedEntity, ITestableInput, IProjectTestableInput {

    private final String _url;
    private final Map<String, String> _attrs;

    private final String _projectName;

    public RemoteTestableInput(String url, String projectName) {
        _url = url;
        _attrs = new HashMap<>();
        _projectName = projectName;
    }

    @Override
    public void addAttribute(String key, String value) {
        _attrs.put(key, value);
    }

    @Override
    public String getAttribute(String key) {
        return _attrs.get(key);
    }

    @Override
    public String getName() {
        return _url;
    }

    @Override
    public String getProjectName() {
        return _projectName;
    }

    @Override
    public String getProjectPath() {
        return _projectName;
    }

    @Override
    public String getProjectRelativePath() {
        return _url;
    }

}