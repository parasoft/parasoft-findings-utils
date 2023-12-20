package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.xml.FileImportPreferences;
import com.parasoft.findings.utils.results.xml.XmlReportReader;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class XmlReportViolationsTest {
    XmlReportReader reportReader = new  XmlReportReader(mock(FileImportPreferences.class), mock(ITestableInputLocationMatcher.class));
    XmlReportViolations xmlReportViolations = new XmlReportViolations(new URL("file://path/to/report"), reportReader);

    public XmlReportViolationsTest() throws MalformedURLException {
    }

    @Test
    public void testGetRulesImportHandler() {
        assertEquals(reportReader.getRulesImportHandler(), xmlReportViolations.getRulesImportHandler());
    }

    @Test
    public void testRemove() {
        assertThrows(UnsupportedOperationException.class, xmlReportViolations::remove);
    }
}
