package com.parasoft.findings.utils.doc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class RuleDocumentationProviderTest {

    private final String dtpUrl = System.getProperty("testDtpUrl"); // Use the same key as in maven-surefire-plugin configuration.
    private final String xtestVersion = System.getProperty("testXtestVersion");

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    void testGetDtpDocServiceStatus_normal() {
        createRDPWithAvailableClientDtpDocService();
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    void testGetDtpDocServiceStatus_dtpUrlNotFound() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl + "/notFundPage");
        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);

        assertEquals(RuleDocumentationProvider.ClientStatus.NOT_AVAILABLE, underTest.getDtpDocServiceStatus());
    }

    @Test
    void testGetDtpDocServiceStatus_incorrectDtpUrl() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", "https://incorrectDtpUrl");
        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);

        assertEquals(RuleDocumentationProvider.ClientStatus.NOT_AVAILABLE, underTest.getDtpDocServiceStatus());
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleDocLocationFromDtp_normal() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        final String analyzer = "com.parasoft.xtest.cpp.analyzer.static.pattern";
        final String ruleId = "APSC_DV-000160-a";
        String remoteRuleDocURL = underTest.getRuleDocLocation(analyzer, ruleId);

        assertTrue(remoteRuleDocURL != null && remoteRuleDocURL.contains(analyzer) && remoteRuleDocURL.contains(ruleId));
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleDocLocationFromDtp_incorrectAnalyzer() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String remoteRuleDocURL = underTest.getRuleDocLocation("incorrectAnalyzer", "APSC_DV-000160-a");

        assertNull(remoteRuleDocURL);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleDocLocationFromDtp_incorrectRuleId() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "incorrectRuleId");

        assertNull(remoteRuleDocURL);
    }

    @Test
    public void testGetRuleDocLocationFromDtp_emptyDtpUrl() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", "");

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }


    @Test
    public void testGetRuleDocLocationFromDtp_noDtpUrl() {
        Properties properties = new Properties();

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }

    @Test
    public void testGetRuleDocLocationFromLocalDir() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", new File("src/test/resources/ruledoc").getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-a");
        assertEquals(new File("src/test/resources/ruledoc", "APSC_DV-000160-a.html").getAbsolutePath(), localRuleDocLocation);
    }

    @Test
    public void testGetCompressedRuleDocLocation_normal() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", new File("src/test/resources/ruledoc/compressedRuleDoc").getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-a");
        assertEquals(new File("src/test/resources/ruledoc/compressedRuleDoc/doc.zip").getAbsolutePath(), localRuleDocLocation);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_normal() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String dtpUrl = this.dtpUrl.endsWith("/") ? this.dtpUrl : this.dtpUrl + "/";
        assertNotNull(xtestVersion, "System property 'testXtestVersion' is not specified");
        String ruleContent = underTest.getDtpRuleDocContent(dtpUrl + "grs/dtp/rulesdoc/com.parasoft.xtest.cpp.analyzer.static.pattern/" + xtestVersion + "/zh_CN/APSC_DV-000160-a.html");

        // If the test fails, check if the rule content is still available at the specified URL,
        // you may need to change the "testXtestVersion" system property, since different DTP version uses different value.
        assertNotEquals("", ruleContent);
        assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
    }

    @Test
    public void getRemoteRuleContent_dtpDocServiceNotAvailable() {
        Properties properties = new Properties();
        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        assertEquals(RuleDocumentationProvider.ClientStatus.DTP_URL_NOT_SPECIFIED, underTest.getDtpDocServiceStatus());

        String ruleContent = underTest.getDtpRuleDocContent("https://anyUrl");

        assertEquals("", ruleContent);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_notARuleDocUrl1() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String dtpUrl = this.dtpUrl.endsWith("/") ? this.dtpUrl : this.dtpUrl + "/";
        // Url pattern is not "${dtpUrl}/grs/dtp/rulesdoc"
        String ruleContent = underTest.getDtpRuleDocContent(dtpUrl + "incorrect/pattern/com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.3/zh_CN/APSC_DV-000160-a.html");

        assertEquals("", ruleContent);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_notARuleDocUrl2() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        // Url pattern is not "${dtpUrl}/grs/dtp/rulesdoc"
        String ruleContent = underTest.getDtpRuleDocContent("https://incorrectDtpUrl/grs/dtp/rulesdoc/com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.3/zh_CN/APSC_DV-000160-a.html");

        assertEquals("", ruleContent);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_urlWithSpaces() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String dtpUrl = this.dtpUrl.endsWith("/") ? this.dtpUrl : this.dtpUrl + "/";
        // Url with spaces
        String ruleContent = underTest.getDtpRuleDocContent(dtpUrl + "grs/dtp/rulesdoc/      com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.3/zh_CN/APSC_DV-000160-a.html");
        assertEquals("", ruleContent);
    }

    boolean hasTestDtpUrlSystemProperty() {
        return dtpUrl != null && !dtpUrl.trim().isEmpty();
    }

    private RuleDocumentationProvider createRDPWithAvailableClientDtpDocService() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", dtpUrl);
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        assertEquals(RuleDocumentationProvider.ClientStatus.AVAILABLE, ruleDocumentationProvider.getDtpDocServiceStatus());
        return ruleDocumentationProvider;
    }
}
