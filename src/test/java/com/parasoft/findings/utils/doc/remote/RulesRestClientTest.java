package com.parasoft.findings.utils.doc.remote;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.Properties;

import static com.parasoft.findings.utils.doc.remote.RulesRestClient.DTP_URL;
import static org.junit.jupiter.api.Assertions.*;

public class RulesRestClientTest {

    private final String dtpUrl = System.getProperty("testDtpUrl"); // Use the same key as in maven-surefire-plugin configuration.

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testCreate_normal() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl);

        RulesRestClient client = null;
        try {
            client = RulesRestClient.create(settings);
        } catch (DtpException e) {
            fail("Expect success");
        }

        assertNotNull(client);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testCreate_normal_withSpaces() {
        Properties settings = new Properties();
        settings.put(DTP_URL, " " + dtpUrl + " ");

        RulesRestClient client = null;
        try {
            client = RulesRestClient.create(settings);
        } catch (DtpException e) {
            fail("Expect success");
        }

        assertNotNull(client);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testCreate_dtpUrlNotFound() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl + "/notFundPage");

        try {
            RulesRestClient.create(settings);
            fail("Expect exception");
        } catch (DtpException e) {
            assertTrue(e.getMessage().contains("status code: 404"));
        }
    }

    @Test
    public void testCreate_incorrectDtpUrl() {
        Properties settings = new Properties();
        settings.put(DTP_URL, "http://incorrectDtpUrl");

        try {
            RulesRestClient.create(settings);
            fail("Expect exception");
        } catch (DtpException e) {
            assertTrue(e.getMessage().contains("incorrectDtpUrl"));
        }
    }

    @Test
    public void testCreate_emptyDtpUrl1() {
        Properties settings = new Properties();
        settings.put(DTP_URL, "  ");

        try {
            RulesRestClient.create(settings);
            fail("Expect exception");
        } catch (DtpException e) {
            assertTrue(e.getMessage().contains("Null or empty dtp.url value is specified."));
        }
    }

    @Test
    public void testCreate_emptyDtpUrl2() {
        Properties settings = new Properties();
        settings.put(DTP_URL, "");
        try {
            RulesRestClient.create(settings);
            fail("Expect exception");
        } catch (DtpException e) {
            assertTrue(e.getMessage().contains("Null or empty dtp.url value is specified."));
        }
    }

    @Test
    public void testCreate_noDtpUrl() {
        Properties settings = new Properties();
        try {
            RulesRestClient.create(settings);
            fail("Expect exception");
        } catch (DtpException e) {
            assertTrue(e.getMessage().contains("Null or empty dtp.url value is specified."));
        }
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleInfo_normal() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl);
        RulesRestClient client = createClient();

        final String analyzer = "com.parasoft.xtest.cpp.analyzer.static.pattern";
        final String ruleId = "APSC_DV-000160-a";
        RulesRestClient.RuleInfo ruleInfo = client.getRuleInfo(ruleId, analyzer);
        assertNotNull(ruleInfo);
        String remoteRuleDocURL = ruleInfo._sDocUrl;
        assertTrue(remoteRuleDocURL != null && remoteRuleDocURL.contains(analyzer) && remoteRuleDocURL.contains(ruleId));
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleInfo_incorrectRuleId() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl);
        RulesRestClient client = createClient();

        RulesRestClient.RuleInfo ruleInfo = client.getRuleInfo("incorrectRuleId", "com.parasoft.xtest.cpp.analyzer.static.pattern");
        assertNull(ruleInfo);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleInfo_incorrectAnalyzer() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl);
        RulesRestClient client = createClient();

        RulesRestClient.RuleInfo ruleInfo = client.getRuleInfo("APSC_DV-000160-a", "incorrectAnalyzer");
        assertNull(ruleInfo);
    }

    public RulesRestClient createClient() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl);
        RulesRestClient client = null;
        try {
            client = RulesRestClient.create(settings);
        } catch (DtpException e) {
            fail("Expect success");
        }
        assertNotNull(client);
        return client;
    }

    boolean hasTestDtpUrlSystemProperty() {
        return dtpUrl != null && !dtpUrl.trim().isEmpty();
    }
}