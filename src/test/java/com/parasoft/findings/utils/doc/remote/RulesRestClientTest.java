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
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNotNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.AVAILABLE, client.getClientStatus());
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testCreate_normal_withSpaces() {
        Properties settings = new Properties();
        settings.put(DTP_URL, " " + dtpUrl + " ");
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNotNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.AVAILABLE, client.getClientStatus());
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testCreate_dtpUrlNotFound() {
        Properties settings = new Properties();
        settings.put(DTP_URL, dtpUrl + "/notFundPage");
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.NOT_AVAILABLE, client.getClientStatus());
    }

    @Test
    public void testCreate_incorrectDtpUrl() {
        Properties settings = new Properties();
        settings.put(DTP_URL, "https://incorrectDtpUrl");
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.NOT_AVAILABLE, client.getClientStatus());
    }

    @Test
    public void testCreate_emptyDtpUrl1() {
        Properties settings = new Properties();
        settings.put(DTP_URL, "  ");
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.DTP_URL_NOT_SPECIFIED, client.getClientStatus());
    }

    @Test
    public void testCreate_emptyDtpUrl2() {
        Properties settings = new Properties();
        settings.put(DTP_URL, "");
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.DTP_URL_NOT_SPECIFIED, client.getClientStatus());
    }

    @Test
    public void testCreate_noDtpUrl() {
        Properties settings = new Properties();
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);

        assertNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.DTP_URL_NOT_SPECIFIED, client.getClientStatus());
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
        RulesRestClient.CreationResult client = RulesRestClient.create(settings);
        assertNotNull(client.getClient());
        assertEquals(RulesRestClient.ClientStatus.AVAILABLE, client.getClientStatus());
        return client.getClient();
    }

    boolean hasTestDtpUrlSystemProperty() {
        return dtpUrl != null && !dtpUrl.trim().isEmpty();
    }
}