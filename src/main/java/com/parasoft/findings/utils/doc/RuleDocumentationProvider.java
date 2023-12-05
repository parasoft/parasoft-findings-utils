package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.doc.remote.RulesRestClient;

import java.util.Properties;

public class RuleDocumentationProvider {
    private final Properties _settings;

    private final RulesRestClient.CreationResult _rulesRestClientResult;

    /**
     * @param settings the settings to configure access to the documentation
     */
    public RuleDocumentationProvider(Properties settings) {
        _settings = settings;
        _rulesRestClientResult = RulesRestClient.create(settings);
    }

    /**
     * @param analyzer the analyzer connected with the given rule, could be null if using local rules
     * @param ruleId   the rule identifier
     * @return url of rule docs or null
     */
    public String getRuleDocLocation(String analyzer, String ruleId) {
        RuleDocumentationHelper ruleDocHelper = new RuleDocumentationHelper(ruleId, analyzer, _rulesRestClientResult.getClient(), _settings);
        return ruleDocHelper.getRuleDocLocation();
    }

    public RulesRestClient.ClientStatus getDtpDocServiceStatus() {
        return _rulesRestClientResult.getClientStatus();
    }
}