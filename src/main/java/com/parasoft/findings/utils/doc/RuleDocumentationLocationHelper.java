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

package com.parasoft.findings.utils.doc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.parasoft.findings.utils.common.IStringConstants;
import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.common.util.URLUtil;
import com.parasoft.findings.utils.doc.remote.RulesRestClient;
import com.parasoft.findings.utils.doc.remote.RulesRestClient.RuleInfo;

class RuleDocumentationLocationHelper {
    private final String _sRuleId;
    private final String _sAnalyzerId;
    private final Properties _settings;
    private final RulesRestClient _client;

    /**
     * @param sRuleId
     * @param sAnalyzerId
     * @param client
     * @pre sRuleId != null
     */
    public RuleDocumentationLocationHelper(String sRuleId, String sAnalyzerId, RulesRestClient client, Properties settings) {
        _sRuleId = sRuleId;
        _sAnalyzerId = sAnalyzerId;
        _settings = settings;
        _client = client;
    }


    public String getRuleDocLocation() {
        String sUrl = null;
        if (_settings != null) {
            sUrl = createCustomLoc();
        }

        if (sUrl == null) {
            sUrl = createDtpLocation();
        }

        return sUrl;
    }

    private String createDtpLocation() {
        if (_client == null) {
            Logger.getLogger().debug("No DTP rules client available, cannot show DTP docs for " + _sRuleId); //$NON-NLS-1$
            return null;
        }
        if (_sAnalyzerId == null) {
            Logger.getLogger().debug("Rule " + _sRuleId + " has no analyzer id, cannot show docs on DTP."); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }

        RuleInfo ruleInfo = _client.getRuleInfo(_sRuleId, _sAnalyzerId);
        if (ruleInfo == null) {
            Logger.getLogger().debug("Rule " + _sRuleId + " not found on DTP."); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }

        if (ruleInfo._sDocUrl == null) {
            Logger.getLogger().debug("No doc for rule " + _sRuleId + " on DTP."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return ruleInfo._sDocUrl;
    }

    private String createCustomLoc() {
        String localDocLocation = (String) _settings.get("report.rules");
        if (StringUtil.isEmptyTrimmed(localDocLocation)) {
            return null;
        }

        URL url = URLUtil.toURL(localDocLocation);
        if (url == null) {
            Logger.getLogger().debug("Cannot create url from custom location: " + localDocLocation); //$NON-NLS-1$
            return null;
        }

        return createCustomLocalLoc(url);
    }

    private String createCustomLocalLoc(URL url) {
        File docRoot = URLUtil.toFile(url);
        if (!docRoot.exists()) {
            Logger.getLogger().debug("Custom doc location does not exist, cannot use: " + docRoot.getAbsolutePath()); //$NON-NLS-1$
            return null;
        }

        if (docRoot.isDirectory()) {
            return getRuleFileFromFolder(docRoot, _sRuleId);
        } else if(docRoot.isFile() && docRoot.getName().endsWith(IStringConstants.ZIP_EXT)) {
            return getRuleFileFromZip(docRoot, _sRuleId);
        } else {
            Logger.getLogger().debug("Invalid custom doc location: " + docRoot.getAbsolutePath()); //$NON-NLS-1$
        }
        return null;
    }

    private String getRuleFileFromFolder(File root, String sRuleId) {
        File doc = new File(root, sRuleId + IStringConstants.HTML_EXT);
        if (doc.exists() && doc.isFile()) {
            return doc.getAbsolutePath();
        }
        return null;
    }

    private String getRuleFileFromZip(File root, String sRuleId) {
        Map<String, String> languageSubdirMap = new HashMap<>();
        languageSubdirMap.put(Locale.CHINESE.getLanguage(), "zh_CN/");
        languageSubdirMap.put(Locale.JAPANESE.getLanguage(), "ja/");

        try (ZipFile zipFile = new ZipFile(root)) {
            String internalDocDir = null;
            if (zipFile.getEntry("doc") != null) {
                internalDocDir = "doc/";
            } else if (zipFile.getEntry("docs") != null) {
                internalDocDir = "docs/";
            }
            if (internalDocDir == null) {
                Logger.getLogger().error("Invalid doc zip file: " + root); //$NON-NLS-1$
                return null;
            }

            // Find the localization directory based on language environment
            String localizationDir = languageSubdirMap.getOrDefault(Locale.getDefault().getLanguage(), "");
            String internalRuleDocPath = internalDocDir + localizationDir + sRuleId + ".html";
            ZipEntry entry = zipFile.getEntry(internalRuleDocPath);
            if (entry != null) {
                URI ruleDocZipUri = root.toURI();
                return "jar:" + ruleDocZipUri + "!/" + entry.getName();
            }
            return null;
        } catch (IOException e) {
            // Zip file path errors or permission issues
            Logger.getLogger().error("Error while getting rule doc location in: " + e.getMessage(), e); //$NON-NLS-1$
            return null;
        }
    }

}
