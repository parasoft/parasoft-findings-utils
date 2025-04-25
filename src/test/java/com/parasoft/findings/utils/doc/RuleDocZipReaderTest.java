package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RuleDocZipReaderTest {
    private final String zipFilePath = "src/test/resources/ruledoc/compressedRuleDoc/doc.zip";
    private final String ruleFileLocation = "doc/APSC_DV-000160-a.html";
    private RuleDocZipReader ruleDocZipReader;

    @BeforeEach
    public void setUp() {
        ruleDocZipReader = new RuleDocZipReader(zipFilePath);
    }

    @Test
    public void testReadRuleDocFileInZip_normal() {
        try {
            String ruleContent = ruleDocZipReader.readRuleDocFileInZip(ruleFileLocation, "UTF-8");
            assertNotEquals("", ruleContent);
            assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testReadRuleDocFileInZip_incorrectRuleId() {
        try {

            String ruleContent = ruleDocZipReader.readRuleDocFileInZip("doc/APSC_DV.html", "UTF-8");
            assertEquals("", ruleContent);
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testReadRuleDocFileInZip_useDefaultEncoding() {
        try {
            RuleDocZipReader reader = new RuleDocZipReader(zipFilePath);
            String ruleContent = reader.readRuleDocFileInZip(ruleFileLocation, null);
            assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testGetRuleDocFileLocationInZip_incorrectZipFilePath() {
        try (MockedStatic<Logger> mockedStaticLogger = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStaticLogger.when(Logger::getLogger).thenReturn(logger);

            RuleDocZipReader reader = new RuleDocZipReader("src/test/resources/doc.zip");
            String ruleDocFileLocation = reader.getRuleDocFileLocationInZip("APSC_DV-000160-a");

            assertNull(ruleDocFileLocation);
            verify(logger, times(1)).error(anyString(), any(IOException.class));
        }
    }
}
