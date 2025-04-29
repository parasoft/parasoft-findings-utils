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

import com.parasoft.findings.utils.common.IStringConstants;
import com.parasoft.findings.utils.common.util.FileUtil;
import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.common.util.URLUtil;
import com.parasoft.findings.utils.doc.remote.DtpUrlException;
import com.parasoft.findings.utils.doc.remote.NotSupportedDtpVersionException;
import com.parasoft.findings.utils.doc.remote.RulesRestClient;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import static com.parasoft.findings.utils.doc.remote.RulesRestClient.DTP_URL;

public class RuleDocumentationProvider {
    private final Properties _settings;
    private final RulesRestClient _rulesRestClient;
    private ClientStatus _clientStatus;

    /**
     * @param settings the settings to configure access to the documentation
     */
    public RuleDocumentationProvider(Properties settings) {
        _settings = settings;
        _rulesRestClient = getRulesRestClient();
    }

    private RulesRestClient getRulesRestClient() {
        RulesRestClient rulesRestClient;
        try {
            rulesRestClient = RulesRestClient.create(_settings);
            _clientStatus = ClientStatus.AVAILABLE;
        } catch (DtpUrlException e) {
            Logger.getLogger().info("Null or empty dtp.url value is specified.");  //$NON-NLS-1$
            rulesRestClient = null;
            _clientStatus = ClientStatus.DTP_URL_NOT_SPECIFIED;
        } catch (NotSupportedDtpVersionException e) {
            Logger.getLogger().warn("Unable to retrieve the documentation for the rule from DTP. It is highly possible that the current version of DTP is older than the 2023.1 which is not supported.");  //$NON-NLS-1$
            rulesRestClient = null;
            _clientStatus = ClientStatus.NOT_SUPPORTED_VERSION;
        } catch (Exception e) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to ensure exceptions during rules rest client fetching don't cause the process to fail."
            Logger.getLogger().warn("DTP server is not available: " + _settings.getProperty(DTP_URL)); //$NON-NLS-1$
            rulesRestClient = null;
            _clientStatus = ClientStatus.NOT_AVAILABLE;
        }
        return rulesRestClient;
    }

    /**
     * @param analyzer the analyzer connected with the given rule, could be null if using local rules
     * @param ruleId   the rule identifier
     * @return url of rule docs or null
     */
    public String getRuleDocLocation(String analyzer, String ruleId) {
        RuleDocumentationLocationHelper ruleDocHelper = new RuleDocumentationLocationHelper(ruleId, analyzer, _rulesRestClient, _settings);
        return ruleDocHelper.getRuleDocLocation();
    }

    /**
     * Get the rule doc content from the given location.</br>
     * It supports retrieving the doc from DTP, zip file or folder depending on the location type detected by the ruleDocLocation parameter.</br>
     * @param ruleDocLocation the value should get from {@link #getRuleDocLocation(String, String)}<br/>
     *                        Examples: <br/>
     *                          - jar:file:/path/to/doc.zip!/doc/zh_CN/ruleId.html <br/>
     *                          - file:/path/to/doc/ruleId.html <br/>
     *                          - http://hostname:port/grs/dtp/rulesdoc/ruleId.html <br/>
     * @return rule doc content, empty string if the location is not found.
     *
     */
    public String getRuleDocContent(String ruleDocLocation) {
        String contents = IStringConstants.EMPTY;
        if (StringUtil.isNonEmpty(ruleDocLocation)) {
            switch (getRuleDocLocationType(ruleDocLocation)) {
                case ZIP:
                    contents = getRuleDocContentFromZip(ruleDocLocation);
                    break;
                case DTP:
                    contents = getRuleDocContentFromDtp(ruleDocLocation);
                    break;
                case FOLDER:
                    contents = getRuleDocContentFromFolder(ruleDocLocation);
                    break;
                default:
                    contents = IStringConstants.EMPTY;
            }
        }
        return contents;
    }

    RuleDocLocationType getRuleDocLocationType(String ruleDocLocation) {
        if (ruleDocLocation == null) {
            return RuleDocLocationType.UNKNOWN;
        }
        if (ruleDocLocation.startsWith("jar:")) {
            return RuleDocLocationType.ZIP;
        }
        if (ruleDocLocation.startsWith("http")) {
            return RuleDocLocationType.DTP;
        }
        if (URLUtil.getLocalFile(URLUtil.toURL(ruleDocLocation)) != null) {
            return RuleDocLocationType.FOLDER;
        }
        return RuleDocLocationType.UNKNOWN;
    }

    /**
     * @param docUrl the value should get from {@link #getRuleDocLocation(String, String)}
     * @return rule doc content of URL, empty string if client is not available or URL is not from {@link #getRuleDocLocation(String, String)}
     */
    String getRuleDocContentFromDtp(String docUrl) {
        if (_clientStatus != ClientStatus.AVAILABLE) {
            Logger.getLogger().warn(_clientStatus.toString());
            return IStringConstants.EMPTY;
        }
        return _rulesRestClient.getRuleContent(docUrl);
    }

    /**
     * @param docLocationInZip the value should get from {@link #getRuleDocLocation(String, String)}
     * @return rule doc content in zip file, empty string if the location is not found or the location is not from {@link #getRuleDocLocation(String, String)}
     */
    String getRuleDocContentFromZip(String docLocationInZip) {
        if (docLocationInZip != null) {
            try {
                URL url = new URL(docLocationInZip);
                try (InputStream is = url.openStream();
                     InputStreamReader isr = new InputStreamReader(is, IStringConstants.UTF_8);
                     BufferedReader reader = new BufferedReader(isr)) {
                    return FileUtil.readFile(reader);
                }
            } catch (IOException e) {
                Logger.getLogger().error("Error while getting rule doc file: " + e.getMessage(), e); //$NON-NLS-1$
            }
        }
        return IStringConstants.EMPTY;
    }

    /**
     * @param docLocationInFolder the value should get from {@link #getRuleDocLocation(String, String)}
     * @return rule doc content in folder, empty string if the location is not found or the location is not from {@link #getRuleDocLocation(String, String)}
     */
    String getRuleDocContentFromFolder(String docLocationInFolder) {
        File localFile = URLUtil.getLocalFile(URLUtil.toURL(docLocationInFolder));
        if (localFile != null) {
            try {
                return FileUtil.readFile(localFile, IStringConstants.UTF_8);
            } catch (IOException e) {
                Logger.getLogger().error(e);
            }
        } else {
            Logger.getLogger().error("Failed to determine local file for rule doc location: " + docLocationInFolder); //$NON-NLS-1$
        }
        return IStringConstants.EMPTY;
    }

    public ClientStatus getDtpDocServiceStatus() {
        return _clientStatus;
    }

    public enum ClientStatus {
        AVAILABLE("DTP server is available"),
        NOT_SUPPORTED_VERSION("Unable to retrieve the documentation for the rule from DTP. Please ensure that a DTP version 2023.1 or later is used"),
        NOT_AVAILABLE("DTP server is not available"),
        DTP_URL_NOT_SPECIFIED("DTP URL is not specified");

        private final String description;

        ClientStatus(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public enum RuleDocLocationType {
        DTP,
        ZIP,
        FOLDER,
        UNKNOWN
    }

}