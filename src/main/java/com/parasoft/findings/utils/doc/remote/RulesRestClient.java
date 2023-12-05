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

package com.parasoft.findings.utils.doc.remote;

import java.net.URI;
import java.util.Properties;


/**
 * Client for DTP rules service.
 */
public final class RulesRestClient
        extends RestClient {
    public static String DTP_URL = "dtp.url";

    /**
     * Private constructor.
     *
     * @param endpointURI
     */
    private RulesRestClient(URI endpointURI) {
        super(endpointURI);
    }

    /**
     * @return rest client or null if service is not available
     */
    public static CreationResult create(Properties settings) {
        String dtpUrl = settings.getProperty(DTP_URL);
        if (dtpUrl == null || dtpUrl.trim().length() == 0) {
            return new CreationResult(null, ClientStatus.DTP_URL_NOT_SPECIFIED);
        }
        dtpUrl = dtpUrl.trim();
        dtpUrl = dtpUrl.endsWith("/") ? dtpUrl : dtpUrl + "/";
        URI serviceURI = URI.create(dtpUrl + "grs/api/v1.6/rules");
        RulesRestClient client = new RulesRestClient(serviceURI);
        try {
            client.validateDtpDocService();
            return new CreationResult(client, ClientStatus.AVAILABLE);
        } catch (NotSupportedDtpVersionException e) {
            Logger.getLogger().info("Unable to retrieve the documentation for the rule from DTP. It is highly possible that the current version of DTP is older than the 2023.1 which is not supported.");  //$NON-NLS-1$
            return new CreationResult(null, ClientStatus.NOT_SUPPORTED_VERSION);
        } catch (Exception e) {
            Logger.getLogger().info("DTP server is not available: " + dtpUrl); //$NON-NLS-1$
            return new CreationResult(null, ClientStatus.NOT_AVAILABLE);
        }
    }

    /**
     * @exception NotSupportedDtpVersionException if DTP version is not supported
     * @exception DtpException if DTP doc service is not available
     * */
    private void validateDtpDocService() throws DtpException {
        URIBuilder builder = getEndpointBuilder(DOC_PATH);
        // Try to get rule info with a not existing ruleId and analyzerId pair, the expected response is 404 if DTP doc service is available
        String ruleId = "requestToCheckDTPServerAvailable";
        builder.addParameter("rule", ruleId); //$NON-NLS-1$ //$NON-NLS-2$
        builder.addParameter("analyzerId", "notExistingAnalyzerId"); //$NON-NLS-1$ //$NON-NLS-2$

        try {
            URI uri = builder.build();
            getString(uri);
        } catch (ResponseWithContentException e) {
            int statusCode = e.getCode();
            if ((statusCode == NOT_AUTHORIZED_1) || (statusCode == NOT_AUTHORIZED_2)) {
                throw new NotSupportedDtpVersionException(e);
            }
            if (statusCode == RULE_NOT_FOUND_2) {
                ResponseContent content = e.getResponseContent();
                String contentString = content.toString();
                if (!ResponseContent.NO_CONTENT.equals(content)
                        && contentString.contains("\"status\":404")
                        && contentString.contains("\"message\"")
                        && contentString.contains(ruleId)
                        && contentString.contains("\"moreInfoUrl\"")
                ) {
                    return;  // DTP doc service available
                }
            }
            throw new DtpException(e);
        } catch (Exception e) {
            throw new DtpException(e);
        }
    }

    /**
     * @param sRuleId
     * @param sAnalyzerId
     * @return info for any available version, null if exception happens
     */
    public synchronized RuleInfo getRuleInfo(String sRuleId, String sAnalyzerId) {
        return getRuleInfo(sRuleId, sAnalyzerId, null);
    }

    /**
     * @param sRuleId
     * @param sAnalyzerId
     * @param sAnalyzerVersion
     * @return info about given url, null if exception happens
     */
    public synchronized RuleInfo getRuleInfo(String sRuleId, String sAnalyzerId, String sAnalyzerVersion) {
        URIBuilder builder = getEndpointBuilder(DOC_PATH);
        builder.addParameter(RULE_ATTR, sRuleId); //$NON-NLS-1$
        builder.addParameter(ANALYZER_ID_ATTR, sAnalyzerId); //$NON-NLS-1$
        if (sAnalyzerVersion != null) {
            builder.addParameter(ANALYZER_VERSION_ATTR, sAnalyzerVersion); //$NON-NLS-1$
        }
        try {
            Logger.getLogger().info("Loading info for rule " + sRuleId + " from DTP..."); //$NON-NLS-1$ //$NON-NLS-2$
            URI uri = builder.build();
            String text = getString(uri);
            JacksonObjectImpl result = new JacksonObjectImpl(text);
            return new RuleInfo(result.getString(RULE_ID_ATTR), result.getString(ANALYZER_ID_ATTR), result.getString(ANALYZER_VERSION_ATTR),
                    result.getString(DOCS_URL_ATTR));
        } catch (Exception e) {
            return null;
        }
    }

    public static class RuleInfo {
        public final String _sId;
        public final String _sAnalyzerId;
        public final String _sAnalyzerVersion;
        public final String _sDocUrl;

        public RuleInfo(String sId, String sAnalyzerId, String sAnalyzerVersion, String sDocUrl) {
            _sId = sId;
            _sAnalyzerId = sAnalyzerId;
            _sAnalyzerVersion = sAnalyzerVersion;
            _sDocUrl = sDocUrl;
        }
    }

    public static class CreationResult {
        private final RulesRestClient _client;
        private final ClientStatus _clientStatus;
        private CreationResult(RulesRestClient client, ClientStatus clientStatus) {
            this._client = client;
            this._clientStatus = clientStatus;
        }

        public RulesRestClient getClient() {
            return _client;
        }

        public ClientStatus getClientStatus() {
            return _clientStatus;
        }
    }

    public enum ClientStatus {
        AVAILABLE,
        NOT_SUPPORTED_VERSION,
        NOT_AVAILABLE,
        DTP_URL_NOT_SPECIFIED
    }

    private static final String DOC_PATH = "doc"; //$NON-NLS-1$

    private static final int RULE_NOT_FOUND_2 = 404;

    private static final int NOT_AUTHORIZED_1 = 401;

    private static final int NOT_AUTHORIZED_2 = 403;

    private static final String RULE_ATTR = "rule"; //$NON-NLS-1$

    private static final String RULE_ID_ATTR = "ruleId"; //$NON-NLS-1$

    private static final String ANALYZER_ID_ATTR = "analyzerId"; //$NON-NLS-1$

    private static final String ANALYZER_VERSION_ATTR = "analyzerVersion"; //$NON-NLS-1$

    private static final String DOCS_URL_ATTR = "docsUrl"; //$NON-NLS-1$
}