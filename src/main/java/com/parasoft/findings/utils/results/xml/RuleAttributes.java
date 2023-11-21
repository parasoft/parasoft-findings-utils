package com.parasoft.findings.utils.results.xml;

public class RuleAttributes {
    private final String _sRuleCategory;

    private final String _sRuleHeader;

    private final String _ruleScope;

    public RuleAttributes(String sRuleCategory, String sRuleHeader,
                          String ruleScope) {
        _sRuleCategory = sRuleCategory;
        _sRuleHeader = sRuleHeader;
        _ruleScope = ruleScope;
    }

    public String getRuleCategory() {
        return _sRuleCategory;
    }

    public String getRuleHeader() {
        return _sRuleHeader;
    }

    public String getRuleScope() {
        return _ruleScope;
    }
}
