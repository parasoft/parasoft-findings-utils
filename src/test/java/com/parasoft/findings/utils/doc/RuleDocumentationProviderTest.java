package com.parasoft.findings.utils.doc;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class RuleDocumentationProviderTest {

    private final String dtpUrl = "https://dtp.parasoftcn.com/202301SS";

    @Test
    void getRuleDocLocationFromDtp_normal() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl);

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertEquals("https://dtp.parasoftcn.com/202301SS/grs/dtp/rulesdoc/com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.2/zh_CN/APSC_DV-000160-a.html",
                      remoteRuleDocURL);
    }

    @Test
    void getRuleDocLocationFromDtp_incorrectAnalyzer() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl);

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("incorrectAnalyzer", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }

    @Test
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
}
