package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ZipFileUtilTest {
    private final String zipFilePath = "src/test/resources/ruledoc/compressedRuleDoc/doc.zip";

    @Test
    public void testReadRuleDocFileInZip_normal() {
        try {
            String ruleContent = ZipFileUtil.readRuleDocFileInZip(zipFilePath, "APSC_DV-000160-a");

            assertNotEquals("", ruleContent);
            assertTrue(ruleContent.toUpperCase().contains("<HTML>") && ruleContent.contains("[APSC_DV-000160-a]"));
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testReadRuleDocFileInZip_incorrectRuleId() {
        try {
            String ruleContent = ZipFileUtil.readRuleDocFileInZip(zipFilePath, "APSC_DV");
            assertEquals("", ruleContent);
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void testGetDocZipFileInDir_normal() {
        String result = ZipFileUtil.getDocZipFileInDir("src/test/resources/ruledoc/compressedRuleDoc");
        assertEquals(new File(zipFilePath).getAbsolutePath(), result);
    }

    @Test
    public void testGetDocZipFileInDir_noZipFileFound() {
        String result = ZipFileUtil.getDocZipFileInDir("src/test/resources/ruledoc");
        assertNull(result);
    }

    @Test
    public void testIsZip_normal() {
        boolean result = ZipFileUtil.isZipFile(zipFilePath);
        assertTrue(result);
    }

    @Test
    public void testIsZip_NotAZipFile() {
        boolean result = ZipFileUtil.isZipFile("src/test/resources/ruledoc/APSC_DV-000160-a.html");
        assertFalse(result);
    }

    @Test
    public void testIsZip_incorrectZipFilePath() {
        try (MockedStatic<Logger> mockedStaticLogger = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStaticLogger.when(Logger::getLogger).thenReturn(logger);

            boolean result = ZipFileUtil.isZipFile("src/test/resources/doc.zip");

            assertFalse(result);
            verify(logger, times(1)).debug(any());
        }
    }
}
