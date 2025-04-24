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
import java.net.URL;
import java.util.Properties;

import com.parasoft.findings.utils.common.IStringConstants;
import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.common.util.URLUtil;
import com.parasoft.findings.utils.common.util.FileUtil;
import com.parasoft.findings.utils.common.util.ZipFileUtil;
import com.parasoft.findings.utils.doc.remote.RulesRestClient;
import com.parasoft.findings.utils.doc.remote.RulesRestClient.RuleInfo;

class RuleDocumentationHelper {
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
    public RuleDocumentationHelper(String sRuleId, String sAnalyzerId, RulesRestClient client, Properties settings) {
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
            Logger.getLogger().debug("Custom doc dir does not exist, cannot use: " + docRoot.getAbsolutePath()); //$NON-NLS-1$
            return null;
        }

        File docsExactMatch = getRuleFile(docRoot, _sRuleId);
        if (docsExactMatch.isFile()) {
            return docsExactMatch.getAbsolutePath();
        }

        String docZipFilePath = ZipFileUtil.getDocZipFileInDir(docRoot.getAbsolutePath());
        if (docZipFilePath != null) {
            return docZipFilePath;
        }

        return guessRuleFile(docRoot, _sRuleId);
    }

    /**
     * @param root
     * @param sRuleId
     * @return
     * @pre root != null
     * @pre sRuleId != null
     * @post $result != null
     */
    public File getRuleFile(File root, String sRuleId) {
        return new File(root, sRuleId + IStringConstants.HTML_EXT);
    }

    /**
     * Try to locate rule docs file in given root dir, using rule id as foundation for the name of the file
     *
     * @param root
     * @param sRuleId
     * @return existing file or null if none could be guessed
     * @pre root != null
     * @pre sRuleId != null
     */
    public String guessRuleFile(File root, String sRuleId) {
        File guessedExactMatch = new File(root, sRuleId);
        if (guessedExactMatch.isFile()) {
            return guessedExactMatch.getAbsolutePath();
        }
        File[] sCandidateFiles = FileUtil.listFilesByName(root, sRuleId);
        if (sCandidateFiles.length != 1) {
            return root.getAbsolutePath();
        }

        return sCandidateFiles[0].getAbsolutePath();
    }

}