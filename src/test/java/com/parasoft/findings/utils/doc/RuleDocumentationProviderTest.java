package com.parasoft.findings.utils.doc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class RuleDocumentationProviderTest {

    private final String dtpUrl = System.getProperty("testDtpUrl"); // Use the same key as in maven-surefire-plugin configuration.

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    void getRuleDocLocationFromDtp_normal() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl);

        final String analyzer = "com.parasoft.xtest.cpp.analyzer.static.pattern";
        final String ruleId = "APSC_DV-000160-a";

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation(analyzer, ruleId);

        assertTrue(remoteRuleDocURL != null && remoteRuleDocURL.contains(analyzer) && remoteRuleDocURL.contains(ruleId));
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    void getRuleDocLocationFromDtp_incorrectAnalyzer() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl);

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("incorrectAnalyzer", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    void getRuleDocLocationFromDtp_incorrectRuleId() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl);

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "incorrectRuleId");
        assertNull(remoteRuleDocURL);
    }

    @Test
    void getRuleDocLocationFromDtp_emptyDtpUrl() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", "");

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }


    @Test
    void getRuleDocLocationFromDtp_noDtpUrl() {
        Properties properties = new Properties();

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }

    @Test
    void getRuleDocLocationFromLocalDir() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", new File("src/test/resources/ruledoc").getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-a");
        assertEquals(new File("src/test/resources/ruledoc", "APSC_DV-000160-a.html").getAbsolutePath(), localRuleDocLocation);
    }

    boolean hasTestDtpUrlSystemProperty() {
        return dtpUrl != null && !dtpUrl.trim().isEmpty();
    }
}
