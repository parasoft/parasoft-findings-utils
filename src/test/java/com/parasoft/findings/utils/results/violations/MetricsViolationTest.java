package com.parasoft.findings.utils.results.violations;//package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FileTestableInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class MetricsViolationTest {
    private ResultLocation resultLocation;

    @BeforeEach
    public void setUp() {
        resultLocation = new ResultLocation(new FileTestableInput(new File("path/to/source/file")), new SourceRange(1, 2));
    }

    @Test
    public void testConstructorAndGetters() {
        String sRuleId = "RULE001";
        String sAnalyzerId = "ANALYZER001";
        ResultLocation resultLocation = this.resultLocation;
        String sMessage = "Violation message";
        String sLanguageId = "JAVA";

        MetricsViolation metricsViolation = new MetricsViolation(
                sRuleId, sAnalyzerId, resultLocation, sMessage, sLanguageId);

        assertEquals(sRuleId, metricsViolation.getRuleId());
        assertEquals(sAnalyzerId, metricsViolation.getAnalyzerId());
        assertEquals(resultLocation, metricsViolation.getResultLocation());
        assertEquals(sMessage, metricsViolation.getMessage());
        assertEquals(sLanguageId, metricsViolation.getLanguageId());
    }

    @Test
    public void testHashCode() {
        MetricsViolation viol1 = new MetricsViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA");
        MetricsViolation viol_sameAsViol1 = new MetricsViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA");

        assertEquals(viol_sameAsViol1.hashCode(), viol1.hashCode());
    }

    @Test
    public void testEquals() {
        MetricsViolation viol1 = new MetricsViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA");
        MetricsViolation viol_samePropsAsViol1 = new MetricsViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA");
        MetricsViolation viol_diffRuleIdWithViol1 = new MetricsViolation(
                "RULE002", "ANALYZER001", this.resultLocation, "Message", "JAVA");
        MetricsViolation viol_diffAnalyzerIdWithViol1 = new MetricsViolation(
                "RULE001", "ANALYZER002", this.resultLocation, "Message", "JAVA");

        assertTrue(viol1.equals(viol1)); // Same object
        assertTrue(viol1.equals(viol_samePropsAsViol1)); // Same properties
        assertFalse(viol1.equals("")); // Different type
        assertFalse(viol1.equals(viol_diffRuleIdWithViol1)); // Different ruleId
        assertTrue(viol1.equals(viol_diffAnalyzerIdWithViol1)); // Different analyzerId
    }

    @Test
    public void testToString() {
        MetricsViolation viol = new MetricsViolation(
                "RULE001", "ANALYZER001", resultLocation, "Message", "JAVA");

        String expectedString = "Message,RULE001,FileTestableInput [path\\to\\source\\file],[1,2-2,0]";
        assertEquals(expectedString, viol.toString());
    }
}