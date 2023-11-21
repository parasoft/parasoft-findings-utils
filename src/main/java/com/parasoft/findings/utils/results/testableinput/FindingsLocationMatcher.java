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

import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import com.parasoft.findings.utils.common.util.PathUtil;
import com.parasoft.findings.utils.results.violations.LocationsException;

import java.io.File;
import java.util.*;

public class FindingsLocationMatcher
        extends DefaultLocationMatcher {
    private final Map<String, ITestableInput> _inputsMap = new HashMap<>();

    @Override
    public ITestableInput matchLocation(Properties storedLocation, boolean bAcceptModified)
            throws LocationsException {
        if (storedLocation == null) {
            return null;
        }
        // detect legacy mode
        String sPath = storedLocation.getProperty(IXmlTagsAndAttributes.LOC_ATTR);
        if (sPath == null) {
            return super.matchLocation(storedLocation, bAcceptModified);
        }
        String sFSPath = storedLocation.getProperty(IXmlTagsAndAttributes.FS_PATH);
        String sProjectPath = storedLocation.getProperty(IXmlTagsAndAttributes.PROJECT_PATH_ATTR);
        String sProject = storedLocation.getProperty(IXmlTagsAndAttributes.PROJECT_ATTR);
        return new PathInput(sPath, sFSPath, sProject, sProjectPath);
    }

    @Override
    public ITestableInput matchLocation(ITestableInput originalInput, List<Long> hashes,
                                        String sRepositoryPath, String sBranch, boolean bAcceptModified)
            throws LocationsException {
        if (originalInput instanceof PathInput) {
            PathInput pathInput = (PathInput) originalInput;
            String sPath = pathInput.getPath();
            ITestableInput input = _inputsMap.get(sPath);
            if (input != null) {
                return input;
            }
            ITestableInput matchedInput = compute(pathInput);
            _inputsMap.put(sPath, matchedInput);
            return matchedInput;
        }
        return super.matchLocation(originalInput, hashes, sRepositoryPath, sBranch, bAcceptModified);
    }

    public static String getFilePath(IFileTestableInput input) {
        if (input == null) {
            return null;
        }
        File fileLocation = input.getFileLocation();
        if (fileLocation == null) {
            return null;
        }
        if (fileLocation.exists()) {
            return fileLocation.getAbsolutePath();
        }
        if (input instanceof ProjectFileTestableInput) {
            String sFilePath = ((ProjectFileTestableInput) input).getProjectPath() + IProjectTestableInput.PATH_SEPARATOR
                    + ((ProjectFileTestableInput) input).getProjectRelativePath();
            if (sFilePath.startsWith(IProjectTestableInput.PATH_SEPARATOR)) {
                sFilePath = sFilePath.substring(1);
            }
            return sFilePath;
        }
        return fileLocation.getAbsolutePath();
    }

    private ITestableInput compute(PathInput pathInput) {
        String sPath = pathInput.getPath();
        String sFSPath = pathInput.getFileSystemPath();
        File inputFile = getFile(sFSPath, sPath);
        if (inputFile == null) {
            return pathInput;
        }
        String sProjectPath = pathInput.getProjectPath();
        String sProjectName = pathInput.getProjectName();
        String sResPrjRelativePath = null;
        if ((sProjectPath != null) && sPath.startsWith(sProjectPath)) {
            sResPrjRelativePath = sPath.substring(sProjectPath.length());
        } else {
            String[] aParts = PathUtil.splitPath(sPath);
            if ((aParts.length > 2) && !sPath.startsWith(IProjectTestableInput.PATH_SEPARATOR)) {
                // get two segments - it is VS project path
                sProjectName = aParts[1];
                sProjectPath = aParts[0] + IProjectTestableInput.PATH_SEPARATOR + aParts[1];
                sResPrjRelativePath = concatProjectPathSegments(aParts, 2);
            } else if (aParts.length > 1) {
                // get only the first part as project path - it is project name too
                sProjectName = aParts[0];
                sProjectPath = IProjectTestableInput.PATH_SEPARATOR + aParts[0];
                sResPrjRelativePath = concatProjectPathSegments(aParts, 1);
            }
        }
        if (sResPrjRelativePath == null) {
            return new FileTestableInput(inputFile);
        }
        return new ProjectFileTestableInput(inputFile,
                sProjectName, sProjectName, sProjectPath, sResPrjRelativePath);
    }

    private static String concatProjectPathSegments(String[] aSegments, int startIdx) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIdx; i < aSegments.length; i++) {
            if (sb.length() > 0) {
                sb.append(IProjectTestableInput.PATH_SEPARATOR);
            }
            sb.append(aSegments[i]);
        }
        return sb.toString();
    }

    private static File getFile(String sFSPath, String sPath) {
        if (StringUtil.isNonEmptyTrimmed(sFSPath)) {
            return new File(sFSPath);
        }
        if (StringUtil.isNonEmptyTrimmed(sPath)) {
            return new File(sPath);
        }
        return null;
    }
}
