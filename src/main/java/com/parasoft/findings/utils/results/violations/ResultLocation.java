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

import com.parasoft.findings.utils.results.testableinput.ITestableInput;

/**
 * Simple result location implementation
 */
public class ResultLocation {
    /**
     * Testable input associated with this location
     */
    private final ITestableInput _testableInput;

    /**
     * Source range details associated with this location
     */
    private final SourceRange _sourceRange;

    /**
     * Creates new testable based location
     *
     * @param testableInput testable location
     * @param sourceRange   source range details
     * @pre testableInput != null
     */
    public ResultLocation(ITestableInput testableInput, SourceRange sourceRange) {
        _testableInput = testableInput;
        _sourceRange = sourceRange;
    }

    /**
     * Returns testable input
     */
    public ITestableInput getTestableInput() {
        return _testableInput;
    }

    public SourceRange getSourceRange() {
        return _sourceRange;
    }

    @Override
    public int hashCode() {
        return _testableInput.hashCode()
                + (_sourceRange != null ? _sourceRange.hashCode() * 31 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ResultLocation)) {
            return false;
        }
        ITestableInput input = ((ResultLocation) obj).getTestableInput();
        if (!_testableInput.equals(input)) {
            return false;
        }
        SourceRange sr = ((ResultLocation) obj).getSourceRange();
        // parasoft-suppress PB.CUB.UEIC "Reviewed."
        return (sr == _sourceRange) || ((_sourceRange != null) && (_sourceRange.equals(sr)));
    }

}