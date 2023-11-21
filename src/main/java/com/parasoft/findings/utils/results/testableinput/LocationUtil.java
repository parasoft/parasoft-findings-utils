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
import com.parasoft.findings.utils.results.violations.IAttributedEntity;
import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.common.util.ArraysUtil;
import org.xml.sax.Attributes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public final class LocationUtil {
    private LocationUtil() {
    }

    public static URI getURI(Properties storedLocation) {
        if (storedLocation == null) {
            return null;
        }
        String sUri = storedLocation.getProperty(IXmlTagsAndAttributes.URI_ATTR);
        if (sUri == null) {
            Logger.getLogger().debug("No URI property in stored location. Creating generic URI."); //$NON-NLS-1$
            return getGenericURI(storedLocation);
        }
        try {
            return new URI(sUri);
        } catch (URISyntaxException urise) {
            Logger.getLogger().warn("Failed to parse location URI."); //$NON-NLS-1$
        }
        return null;
    }

    private static URI getGenericURI(Properties storedLocation) {
        String projPath = storedLocation.getProperty(IXmlTagsAndAttributes.PROJECT_ID_ATTR);
        String projRelativePath = storedLocation.getProperty(IXmlTagsAndAttributes.RESOURCE_PROJECT_RELATIVE_PATH_ATTR);
        if (!StringUtil.isEmptyTrimmed(projPath) && !StringUtil.isEmptyTrimmed(projRelativePath)) {
            return getGenericFileURI(projPath + IProjectTestableInput.PATH_SEPARATOR + projRelativePath);
        }
        String scPath = storedLocation.getProperty(IXmlTagsAndAttributes.SOURCE_CONTROL_PATH_ATTR);
        if (!StringUtil.isEmptyTrimmed(scPath)) {
            return getGenericFileURI(scPath);
        }
        Logger.getLogger().warn("Failed to create generic URI for stored location."); //$NON-NLS-1$
        return null;
    }

    public static URI getGenericFileURI(String location) {
        try {
            URI uri = removeHost(new URI(location));
            return new File(uri).toURI();
        } catch (URISyntaxException | IllegalArgumentException use) {
            return new File(location).toURI();
        }
    }

    public static URI toFileURI(URI uri) {
        try {
            return removeHost(uri);
        } catch (URISyntaxException e) {
            Logger.getLogger().errorTrace(e);
        }
        return null;
    }

    public static URI removeHost(URI fileUri)
            throws URISyntaxException {
        return new URI(fileUri.getScheme(), fileUri.getUserInfo(), null, fileUri.getPort(), fileUri.getPath(), null, null);
    }

    public static ITestableInput createTestableInput(Properties storedLocation)
            throws LocationsException {
        URI uri = getURI(storedLocation);
        if (uri == null) {
            throw new LocationsException("Failed to get URI for stored location."); //$NON-NLS-1$
        }
        ITestableInput input = null;
        String symbolId = storedLocation.getProperty(IXmlTagsAndAttributes.SYMBOL_ID_ATTR);
        if (StringUtil.isNonEmpty(symbolId)) {
            throw new RuntimeException("Need to check and refactor");
        } else {
            String projId = storedLocation.getProperty(IXmlTagsAndAttributes.PROJECT_ID_ATTR);
            String project = storedLocation.getProperty(IXmlTagsAndAttributes.PROJECT_ATTR);
            String projPath = storedLocation.getProperty(IXmlTagsAndAttributes.PROJECT_PATH_ATTR);
            String resProjPath = storedLocation.getProperty(IXmlTagsAndAttributes.RESOURCE_PROJECT_RELATIVE_PATH_ATTR);
            String symbols = storedLocation.getProperty(IXmlTagsAndAttributes.SYMBOLS_ATTR);
            input = createTestableInput(uri, projId, project, projPath, resProjPath, symbols);
        }

        for (String sKey : getAdditionalAttributes(storedLocation.keySet())) {
            ((IAttributedEntity) input).addAttribute(sKey, storedLocation.getProperty(sKey));
        }
        return input;
    }

    private static ITestableInput createTestableInput(URI uri, String projId, String project, String projPath, String resProjPath, String symbols)
            throws LocationsException {
        if (uri == null) {
            throw new LocationsException("Location ref or uri attribute not found."); //$NON-NLS-1$
        }
        if (isRemote(uri.getScheme())) {
            if (project == null) {
                Logger.getLogger().warn("Project attribute not found for RemoteTestableInput."); //$NON-NLS-1$
            }
            if (projId == null) {
                Logger.getLogger().warn("Project id attribute not found for RemoteTestableInput."); //$NON-NLS-1$
            }
            return new RemoteTestableInput(uri.toString(), project);
        }
        URI fileURI = toFileURI(uri);
        if (fileURI == null) {
            throw new LocationsException("Invalid location ref or uri."); //$NON-NLS-1$
        }
        File file = new File(fileURI);
        ITestableInput input;
        if (projId == null) {
            // FileTestableInput
            input = new FileTestableInput(file);
        } else {
            // ProjectFileTestableInput
            if (project == null) {
                Logger.getLogger().warn("Project attribute not found. Cannot create ProjectFileTestableInput."); //$NON-NLS-1$
            }
            if (projPath == null) {
                Logger.getLogger().warn("Project path not found. Cannot create ProjectFileTestableInput."); //$NON-NLS-1$
            }
            if (resProjPath == null) {
                Logger.getLogger().warn("Project relative path not found. Cannot create ProjectFileTestableInput."); //$NON-NLS-1$
            }
            if ((project != null) && (projPath != null) && (resProjPath != null)) {
                if (symbols != null) {
                    input = new FileTestableInput(file);
                } else {
                    input = new ProjectFileTestableInput(file, projId, project, projPath, resProjPath);
                }
            } else {
                input = new FileTestableInput(file);
            }
        }
        return input;
    }

    private static boolean isRemote(String scheme) {
        return "http".equals(scheme) || "https".equals(scheme); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static Iterable<String> getAdditionalAttributes(Set<?> keys) {
        List<String> result = new ArrayList<>();
        for (Object key : keys) {
            if (!(key instanceof String)) {
                continue;
            }
            String sKey = (String) key;
            if (ArraysUtil.contains(KNOWN_ATTRIBUTES, sKey)) {
                continue;
            }
            result.add(sKey);
        }
        return result;
    }

    public static Properties readStoredLocation(Attributes attributes) {
        Properties storedLocation = new Properties();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            String sName = attributes.getQName(i);
            String sValue = attributes.getValue(i);
            storedLocation.setProperty(sName, sValue);
        }
        storedLocation.remove(IXmlTagsAndAttributes.LOC_REF_ATTR);
        return storedLocation;
    }

    private static final String[] KNOWN_ATTRIBUTES = new String[]{
            IXmlTagsAndAttributes.REP_REF_ATTR,
            IXmlTagsAndAttributes.SOURCE_CONTROL_PATH_ATTR,
            IXmlTagsAndAttributes.SOURCE_CONTROL_DISPLAY_PATH_ATTR,
            IXmlTagsAndAttributes.REVISION_ATTR,
            IXmlTagsAndAttributes.SOURCE_CONTROL_BRANCH_ATTR,
            IXmlTagsAndAttributes.LOC_REF_ATTR,
            IXmlTagsAndAttributes.PROJECT_ID_ATTR,
            IXmlTagsAndAttributes.PROJECT_ATTR,
            IXmlTagsAndAttributes.PROJECT_PATH_ATTR,
            IXmlTagsAndAttributes.RESOURCE_PROJECT_RELATIVE_PATH_ATTR,
            IXmlTagsAndAttributes.RESOURCE_HASH_ATTR,
            IXmlTagsAndAttributes.URI_ATTR
    };

}