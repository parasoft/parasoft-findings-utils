package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ZipFileUtilTest {
    private final String zipFilePath = "src/test/resources/ruledoc/compressedRuleDoc/doc.zip";

    @Test
    public void testReadRuleDocFileInZip_normal() {
        try {
            ZipFileUtil.setDocZipFilePath(zipFilePath);
            String ruleContent = ZipFileUtil.readRuleDocFileInZip("doc/APSC_DV-000160-a.html", "UTF-8");
            assertNotEquals("", ruleContent);
            assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testReadRuleDocFileInZip_incorrectRuleId() {
        try {
            ZipFileUtil.setDocZipFilePath(zipFilePath);
            String ruleContent = ZipFileUtil.readRuleDocFileInZip("doc/APSC_DV.html", "UTF-8");
            assertEquals("", ruleContent);
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testGetRuleDocFileLocationInZip_incorrectZipFilePath() {
        try (MockedStatic<Logger> mockedStaticLogger = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStaticLogger.when(Logger::getLogger).thenReturn(logger);

            String ruleDocFileLocation = ZipFileUtil.getRuleDocFileLocationInZip("src/test/resources/doc.zip", "APSC_DV-000160-a");

            assertNull(ruleDocFileLocation);
            verify(logger, times(1)).error(any());
        }
    }
}
