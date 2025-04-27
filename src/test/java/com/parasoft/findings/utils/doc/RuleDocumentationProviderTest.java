package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

public class RuleDocumentationProviderTest {

    private final String dtpUrl = System.getProperty("testDtpUrl"); // Use the same key as in maven-surefire-plugin configuration.
    private final String xtestVersion = System.getProperty("testXtestVersion");
    private final File docZipFilePath = new File("src/test/resources/ruledoc/compressedRuleDoc/doc.zip");
    private final File docsZipFilePath = new File("src/test/resources/ruledoc/compressedRuleDoc/docs.zip");
    private final File doxZipFilePath = new File("src/test/resources/ruledoc/compressedRuleDoc/dox.zip");
    private final File illegalZipFilePath = new File("src/test/resources/ruledoc/compressedRuleDoc/illegalZip.zip");

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
    public void testGetRuleDocLocation_fromDtp_normal() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        final String analyzer = "com.parasoft.xtest.cpp.analyzer.static.pattern";
        final String ruleId = "APSC_DV-000160-a";
        String remoteRuleDocURL = underTest.getRuleDocLocation(analyzer, ruleId);

        assertTrue(remoteRuleDocURL != null && remoteRuleDocURL.contains(analyzer) && remoteRuleDocURL.contains(ruleId));
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleDocLocation_fromDtp_incorrectAnalyzer() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String remoteRuleDocURL = underTest.getRuleDocLocation("incorrectAnalyzer", "APSC_DV-000160-a");

        assertNull(remoteRuleDocURL);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void testGetRuleDocLocation_fromDtp_incorrectRuleId() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "incorrectRuleId");

        assertNull(remoteRuleDocURL);
    }

    @Test
    public void testGetRuleDocLocation_fromDtp_emptyDtpUrl() {
        Properties properties = new Properties();
        properties.setProperty("dtp.url", "");

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }


    @Test
    public void testGetRuleDocLocation_fromDtp_noDtpUrl() {
        Properties properties = new Properties();

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String remoteRuleDocURL = underTest.getRuleDocLocation("com.parasoft.xtest.cpp.analyzer.static.pattern", "APSC_DV-000160-a");
        assertNull(remoteRuleDocURL);
    }

    @Test
    public void testGetRuleDocLocation_fromLocalDir() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", new File("src/test/resources/ruledoc").getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-a");
        assertEquals(new File("src/test/resources/ruledoc", "APSC_DV-000160-a.html").getAbsolutePath(), localRuleDocLocation);
    }

    @Test
    public void testGetRuleDocLocation_fromLocalDir_noDoc() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", new File("src/test/resources/ruledoc").getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-b");
        assertNull(localRuleDocLocation);
    }

    @Test
    public void testGetRuleDocLocation_fromZipFile_normal() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", docZipFilePath.getAbsolutePath());
        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String ruleDocFileLocation = underTest.getRuleDocLocation(null,"APSC_DV-000160-a");

        String expectedDocLocationInZip = "jar:" + this.docZipFilePath.toURI() + "!/" + "doc/APSC_DV-000160-a.html";
        assertEquals(expectedDocLocationInZip, ruleDocFileLocation);

        properties = new Properties();
        properties.setProperty("report.rules", docsZipFilePath.getAbsolutePath());
        underTest = new RuleDocumentationProvider(properties);
        ruleDocFileLocation = underTest.getRuleDocLocation(null,"APSC_DV-000160-a");

        expectedDocLocationInZip = "jar:" + this.docsZipFilePath.toURI() + "!/" + "docs/APSC_DV-000160-a.html";
        assertEquals(expectedDocLocationInZip, ruleDocFileLocation);
    }

    @Test
    public void testGetRuleDocLocation_fromZipFile_incorrectRuleId() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", docsZipFilePath.getAbsolutePath());
        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String ruleDocFileLocation = underTest.getRuleDocLocation(null,"APSC_DV-000160-b");

        assertNull(ruleDocFileLocation);
    }

    @Test
    public void testGetRuleDocLocation_fromZipFile_invalidZipFile() {
        try (MockedStatic<Logger> mockedStaticLogger = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStaticLogger.when(Logger::getLogger).thenReturn(logger);
            Properties properties = new Properties();
            properties.setProperty("report.rules", doxZipFilePath.getAbsolutePath());
            RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
            String ruleDocFileLocation = underTest.getRuleDocLocation(null,"APSC_DV-000160-a");

            assertNull(ruleDocFileLocation);
            verify(logger, times(1)).error(startsWith("Invalid doc zip file:"));
        }
    }

    @Test
    public void testGetRuleDocLocation_fromZipFile_illegalZipFilePath() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", illegalZipFilePath.getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-a");
        assertNull(localRuleDocLocation);
    }

    @Test
    public void testGetRuleDocLocation_fromZipFile_noDoc() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", docZipFilePath.getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-b");
        assertNull(localRuleDocLocation);
    }


    @Test
    public void testGetRuleDocLocation_error() {
        Properties properties = new Properties();
        properties.setProperty("report.rules", new File("src/test/resources/ruledoc/APSC_DV-000160-a.html").getAbsolutePath());

        RuleDocumentationProvider underTest = new RuleDocumentationProvider(properties);
        String localRuleDocLocation = underTest.getRuleDocLocation(null, "APSC_DV-000160-a");
        assertNull(localRuleDocLocation);
    }

    @Test
    void testGetRuleDocContent_fromFolder() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);

        String ruleContent = ruleDocumentationProvider.getRuleDocContent("file:/" + new File("src/test/resources/ruledoc", "APSC_DV-000160-a.html").getAbsolutePath());
        assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));

        ruleContent = ruleDocumentationProvider.getRuleDocContent(new File("src/test/resources/ruledoc", "APSC_DV-000160-a.html").getAbsolutePath());

        assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
    }

    @Test
    void testGetRuleDocContent_fromFolder_noDoc() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        String ruleContent = ruleDocumentationProvider.getRuleDocContent("file:/" + new File("src/test/resources/ruledoc", "APSC_DV-000160-b.html").getAbsolutePath());

        assertEquals("", ruleContent);
    }

    @Test
    void testGetRuleDocContent_fromZip() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        String docLocationInZip = "jar:" + docZipFilePath.toURI() + "!/" + "doc/APSC_DV-000160-a.html";
        String ruleContent = ruleDocumentationProvider.getRuleDocContent(docLocationInZip);

        assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
    }

    @Test
    public void testGetRuleDocContent_fromZip_incorrectRuleDocLoc() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        String docLocationInZip = "jar:" + docZipFilePath.toURI() + "!/" + "doc/APSC_DV-000160-b.html";
        String ruleContent = ruleDocumentationProvider.getRuleDocContent(docLocationInZip);

        assertEquals("", ruleContent);
    }

    @Test
    void testGetRuleDocContent_fromDtp() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        String docLocationInZip = "http:doc/APSC_DV-000160-a.html";
        String ruleContent = ruleDocumentationProvider.getRuleDocContent(docLocationInZip);

        assertTrue(ruleContent.isEmpty());
    }

    @Test
    void testGetRuleDocContent_invalidLocation() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        String ruleContent = ruleDocumentationProvider.getRuleDocContent("invalidLocation");

        assertTrue(ruleContent.isEmpty());
    }

    @Test
    void testGetRuleDocLocationType_nullLocation() {
        Properties properties = new Properties();
        RuleDocumentationProvider ruleDocumentationProvider = new RuleDocumentationProvider(properties);
        RuleDocumentationProvider.RuleDocLocationType result = ruleDocumentationProvider.getRuleDocLocationType(null);

        assertSame(result, RuleDocumentationProvider.RuleDocLocationType.UNKNOWN);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_normal() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String dtpUrl = this.dtpUrl.endsWith("/") ? this.dtpUrl : this.dtpUrl + "/";
        assertNotNull(xtestVersion, "System property 'testXtestVersion' is not specified");
        String ruleContent = underTest.getRuleDocContentFromDtp(dtpUrl + "grs/dtp/rulesdoc/com.parasoft.xtest.cpp.analyzer.static.pattern/" + xtestVersion + "/zh_CN/APSC_DV-000160-a.html");

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

        String ruleContent = underTest.getRuleDocContentFromDtp("https://anyUrl");

        assertEquals("", ruleContent);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_notARuleDocUrl1() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String dtpUrl = this.dtpUrl.endsWith("/") ? this.dtpUrl : this.dtpUrl + "/";
        // Url pattern is not "${dtpUrl}/grs/dtp/rulesdoc"
        String ruleContent = underTest.getRuleDocContentFromDtp(dtpUrl + "incorrect/pattern/com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.3/zh_CN/APSC_DV-000160-a.html");

        assertEquals("", ruleContent);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_notARuleDocUrl2() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        // Url pattern is not "${dtpUrl}/grs/dtp/rulesdoc"
        String ruleContent = underTest.getRuleDocContentFromDtp("https://incorrectDtpUrl/grs/dtp/rulesdoc/com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.3/zh_CN/APSC_DV-000160-a.html");

        assertEquals("", ruleContent);
    }

    @Test
    @EnabledIf(value = "hasTestDtpUrlSystemProperty", disabledReason = "No testDtpUrl system property")
    public void getRemoteRuleContent_urlWithSpaces() {
        RuleDocumentationProvider underTest = createRDPWithAvailableClientDtpDocService();

        String dtpUrl = this.dtpUrl.endsWith("/") ? this.dtpUrl : this.dtpUrl + "/";
        // Url with spaces
        String ruleContent = underTest.getRuleDocContentFromDtp(dtpUrl + "grs/dtp/rulesdoc/      com.parasoft.xtest.cpp.analyzer.static.pattern/10.6.3/zh_CN/APSC_DV-000160-a.html");
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
