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

import com.parasoft.findings.utils.common.util.StringUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    public static RulesRestClient create(Properties settings) {
        String dtpUrl = settings.getProperty(DTP_URL);
        if (StringUtil.isEmpty(dtpUrl)) {
            return null;
        }
        dtpUrl = dtpUrl.endsWith("/") ? dtpUrl : dtpUrl + "/";
        URI serviceURI = URI.create(dtpUrl + "grs/api/v1.6/rules");
        RulesRestClient client = new RulesRestClient(serviceURI);
        try {
            client.getRuleInfo("requestToCheckDTPServerAvailable", "notExistingAnalyzerId");
        } catch (NotSupportedDtpVersionException e) {
            Logger.getLogger().info("Unable to retrieve the documentation for the rule from DTP. It is highly possible that the current version of DTP is older than the 2023.1 which is not supported.");  //$NON-NLS-1$
            client = null;
        } catch (Exception e) {
            Logger.getLogger().info("DTP server is not available: " + dtpUrl); //$NON-NLS-1$
            client = null;
        }
        return client;
    }

    /**
     * @param sRuleId
     * @param sAnalyzerId
     * @return info for any available version
     * @throws DtpException
     */
    public synchronized RuleInfo getRuleInfo(String sRuleId, String sAnalyzerId)
            throws DtpException {
        return getRuleInfo(sRuleId, sAnalyzerId, null);
    }

    /**
     * @param sRuleId
     * @param sAnalyzerId
     * @param sAnalyzerVersion
     * @return info about given url
     * @throws DtpException
     */
    public synchronized RuleInfo getRuleInfo(String sRuleId, String sAnalyzerId, String sAnalyzerVersion)
            throws DtpException {
        URIBuilder builder = getEndpointBuilder(DOC_PATH);
        builder.addParameter("rule", sRuleId); //$NON-NLS-1$
        builder.addParameter("analyzerId", sAnalyzerId); //$NON-NLS-1$
        if (sAnalyzerVersion != null) {
            builder.addParameter("analyzerVersion", sAnalyzerVersion); //$NON-NLS-1$
        }

        try {
            Logger.getLogger().info("Loading info for rule " + sRuleId + " from DTP..."); //$NON-NLS-1$ //$NON-NLS-2$
            URI uri = builder.build();
            String text = getString(uri);
            JacksonObjectImpl result = new JacksonObjectImpl(text);
            return new RuleInfo(result.getString(ID_ATTR), result.getString(ANALYZER_ID_ATTR), result.getString(ANALYZER_VERSION_ATTR),
                    result.getString(DOCS_URL_ATTR));
        } catch (ResponseWithContentException e) {
            int statusCode = e.getCode();
            if ((statusCode == RULE_NOT_FOUND_1) || (statusCode == RULE_NOT_FOUND_2)) {
                return null;
            } else if ((statusCode == NOT_AUTHORIZED_1) || (statusCode == NOT_AUTHORIZED_2)) {
                throw new NotSupportedDtpVersionException(e);
            } else {
                throw new DtpException(e);
            }
        } catch (JSONException | IOException e) {
            throw new DtpException(e);
        } catch (URISyntaxException e) {
            Logger.getLogger().error(e);
        }
        return null;
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

    private static final String DOC_PATH = "doc"; //$NON-NLS-1$

    private static final int RULE_NOT_FOUND_1 = 400;

    private static final int RULE_NOT_FOUND_2 = 404;

    private static final int NOT_AUTHORIZED_1 = 401;

    private static final int NOT_AUTHORIZED_2 = 403;

    private static final String ID_ATTR = "ruleId"; //$NON-NLS-1$

    private static final String ANALYZER_ID_ATTR = "analyzerId"; //$NON-NLS-1$

    private static final String ANALYZER_VERSION_ATTR = "analyzerVersion"; //$NON-NLS-1$

    private static final String DOCS_URL_ATTR = "docsUrl"; //$NON-NLS-1$
}