package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.doc.remote.DtpUrlException;
import com.parasoft.findings.utils.doc.remote.NotSupportedDtpVersionException;
import com.parasoft.findings.utils.doc.remote.RulesRestClient;

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
        } catch (Exception e) {
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
        RuleDocumentationHelper ruleDocHelper = new RuleDocumentationHelper(ruleId, analyzer, _rulesRestClient, _settings);
        return ruleDocHelper.getRuleDocLocation();
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
}