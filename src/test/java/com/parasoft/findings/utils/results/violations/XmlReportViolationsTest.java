package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FindingsLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.xml.FileImportPreferences;
import com.parasoft.findings.utils.results.xml.XmlReportReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

public class XmlReportViolationsTest {

    File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
    FileImportPreferences prefs = new FileImportPreferences(preparedFile);
    ITestableInputLocationMatcher matcher = new FindingsLocationMatcher();
    XmlReportReader reportReader = new  XmlReportReader(prefs, matcher);
    XmlReportViolations xmlReportViolations = new XmlReportViolations(preparedFile.toURI().toURL(), reportReader);

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
