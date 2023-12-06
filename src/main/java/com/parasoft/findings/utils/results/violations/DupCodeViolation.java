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

import java.util.*;

import com.parasoft.findings.utils.common.IStringConstants;
import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.results.testableinput.IFileTestableInput;
import com.parasoft.findings.utils.results.testableinput.IProjectTestableInput;
import com.parasoft.findings.utils.results.testableinput.ITestableInput;
import com.parasoft.findings.utils.results.testableinput.ProjectFileTestableInput;
import com.parasoft.findings.utils.common.util.PathUtil;
import com.parasoft.findings.utils.common.nls.NLS;

public class DupCodeViolation
        extends AbstractCodingStandardsViolation
        implements IRuleViolation {

    private final String _sRuleId;

    private final IPathElement[] _aElementDescriptors;

    public DupCodeViolation(String sRuleId, String sAnalyzerId, ResultLocation resultLocation,
                            String sMessage, String _sLanguageId, IPathElement[] aPathElements) {
        super(sAnalyzerId, _sLanguageId, resultLocation, sMessage);
        _aElementDescriptors = aPathElements;
        _sRuleId = sRuleId;
    }

    @Override
    public String getRuleId() {
        return _sRuleId;
    }

    public IPathElement[] getPathElements() {
        return _aElementDescriptors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getMessage());
        sb.append(IStringConstants.LINE_SEPARATOR);

        for (IPathElement pathElement : getPathElements()) {
            sb.append(toString(pathElement.getLocation()));
            sb.append(IStringConstants.LINE_SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = result * 31 + ((getRuleId() == null) ? 0 : getRuleId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DupCodeViolation)) {
            return false;
        }
        DupCodeViolation violation = (DupCodeViolation) obj;
        if (!super.equals(violation) || !StringUtil.equals(getRuleId(), violation.getRuleId())) {
            return false;
        }
        Set<IPathElement> pathElems1 = new HashSet<IPathElement>();
        pathElems1.addAll(Arrays.asList(getPathElements()));
        Set<IPathElement> pathElems2 = new HashSet<IPathElement>();
        pathElems2.addAll(Arrays.asList(violation.getPathElements()));
        return pathElems1.equals(pathElems2);
    }

    private static String toString(ResultLocation loc) {
        StringBuilder sb = new StringBuilder();
        if (loc == null) {
            return sb.toString();
        }

        ITestableInput input = loc.getTestableInput();
        SourceRange range = loc.getSourceRange();

        String path = getPath(input, false);
        sb.append("   + "); //$NON-NLS-1$
        sb.append(path);
        sb.append(NLS.getFormatted("  [{0}:{1} - {2}:{3}]",  //$NON-NLS-1$
                range.getStartLine(),
                range.getStartLineOffset(),
                range.getEndLine(),
                range.getEndLineOffset()));
        return sb.toString();
    }

    /**
     * @param testable
     * @param bAbsolute
     * @return path for testable input. This
     * <p>
     * Returned paths has normalized separators.
     * @post $result != null
     */
    private static String getPath(ITestableInput testable, boolean bAbsolute) {
        String sPath;
        if (testable instanceof ProjectFileTestableInput) {
            ProjectFileTestableInput input = (ProjectFileTestableInput) testable;
            String projPath = input.getProjectPath();
            String projRelativePath = input.getProjectRelativePath();
            if (projRelativePath == null) {
                Logger.getLogger().warn("Unexpected null project relative path in testable input"); //$NON-NLS-1$
                projRelativePath = IStringConstants.EMPTY;
            }
            sPath = projPath + IProjectTestableInput.PATH_SEPARATOR + projRelativePath;

        } else if (testable instanceof IFileTestableInput) {
            if (bAbsolute) {
                sPath = ((IFileTestableInput) testable).getFileLocation().getAbsolutePath();
            } else {
                sPath = ((IFileTestableInput) testable).getFileLocation().getPath();
            }
        } else {
            sPath = testable.getName();
        }
        return PathUtil.normalizePath(sPath);
    }
}