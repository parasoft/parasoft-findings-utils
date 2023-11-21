package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.doc.remote.RulesRestClient;

import java.util.Properties;

public class RuleDocumentationProvider {
    private final Properties _settings;

    private final RulesRestClient _client;

    /**
     * @param settings the settings to configure access to the documentation
     */
    public RuleDocumentationProvider(Properties settings) {
        _settings = settings;
        _client = RulesRestClient.create(settings);
    }

    /**
     * @param analyzer the analyzer connected with the given rule, could be null if using local rules
     * @param ruleId   the rule identifier
     * @return url of rule docs or null
     */
    public String getRuleDocLocation(String analyzer, String ruleId) {
        RuleDocumentationHelper ruleDocHelper = new RuleDocumentationHelper(ruleId, analyzer, _client, _settings);
        return ruleDocHelper.getRuleDocLocation();
    }
}