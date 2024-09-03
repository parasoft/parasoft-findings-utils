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
 * Simple implementation of path element annotation.
 */
public class PathElementAnnotation implements IPathElementAnnotation{
    private final String _sMessage;

    private final String _sKind;

    /**
     * Constructs annotation
     *
     * @param sMessage message
     * @param sKind    kind
     */
    public PathElementAnnotation(String sMessage, String sKind) {
        _sMessage = sMessage;
        _sKind = sKind;
    }

    public String getMessage() {
        return _sMessage;
    }

    public String getKind() {
        return _sKind;
    }

}