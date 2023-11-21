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

import java.text.MessageFormat;

public class SourceRange {
    private final int _startPosition;
    private final int _startLine;
    private final int _endLine;
    private final int _endPosition;

    /**
     * Creates source range which contains the specified line form the given offset to the end of line.
     *
     * @param line   the line number
     * @param offset the offset
     */
    public SourceRange(int line, int offset) {
        this(line, offset, line + 1, 0);
    }

    /**
     * Creates source range for the given parameters.
     *
     * @param startLine       the start line
     * @param startLineOffset the start line offset
     * @param endLine         the end line
     * @param endLineOffset   the end line offset
     */
    public SourceRange(int startLine, int startLineOffset, int endLine, int endLineOffset) {
        _startLine = startLine;
        _startPosition = startLineOffset;
        _endLine = endLine;
        _endPosition = endLineOffset;
    }

    public int getStartLine() {
        return _startLine;
    }

    public int getStartLineOffset() {
        return _startPosition;
    }

    public int getEndLine() {
        return _endLine;
    }

    public int getEndLineOffset() {
        return _endPosition;
    }

    @Override
    public int hashCode() {
        return (((((_startLine * 31) + _endLine) * 31) + _startPosition) * 31) + _endPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SourceRange)) {
            return false;
        }
        SourceRange range = (SourceRange) obj;
        return (_startLine == range._startLine) && (_endLine == range._endLine) &&
                (_startPosition == range._startPosition) && (_endPosition == range._endPosition);
    }

    @Override
    public String toString() {
        return MessageFormat.format("[{0},{1}-{2},{3}]", _startLine, _startPosition, _endLine, _endPosition); //$NON-NLS-1$
    }
}