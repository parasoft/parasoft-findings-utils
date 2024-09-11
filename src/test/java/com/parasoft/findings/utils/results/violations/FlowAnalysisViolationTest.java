package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FileTestableInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FlowAnalysisViolationTest {
    private ResultLocation resultLocation;

    @BeforeEach
    public void setUp() {
        resultLocation = new ResultLocation(new FileTestableInput(new File("path/to/source/file")), new SourceRange(1, 2));
    }

    @Test
    public void testConstructorAndGetters() {
        String ruleId = "RULE001";
        String analyzerId = "ANALYZER001";
        String languageId = "JAVA";
        ResultLocation resultLocation = this.resultLocation;
        String message = "Violation message";
        String sPackage = "com.example";
        IFlowAnalysisPathElement[] elementDescriptors = new IFlowAnalysisPathElement[0];

        FlowAnalysisViolation violation = new FlowAnalysisViolation(
                ruleId, analyzerId, languageId, resultLocation, message, sPackage,
                elementDescriptors);

        assertEquals(ruleId, violation.getRuleId());
        assertEquals(analyzerId, violation.getAnalyzerId());
        assertEquals(languageId, violation.getLanguageId());
        assertEquals(resultLocation, violation.getResultLocation());
        assertEquals(message, violation.getMessage());
        assertEquals(elementDescriptors, violation.getPathElements());
    }

    @Test
    public void testHashCode() {
        FlowAnalysisViolation viol1 = new FlowAnalysisViolation(
                "RULE001", "ANALYZER001", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[0]);
        FlowAnalysisViolation viol_sameAsViol1 = new FlowAnalysisViolation(
                "RULE001", "ANALYZER001", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[0]);

        assertEquals(viol_sameAsViol1.hashCode(), viol1.hashCode());
    }

    @Test
    public void testEquals() {
        FlowAnalysisViolation viol1 = new FlowAnalysisViolation(
                "RULE001", "ANALYZER001", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[0]);
        FlowAnalysisViolation viol_samePropsAsViol1 = new FlowAnalysisViolation(
                "RULE001", "ANALYZER001", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[0]);
        FlowAnalysisViolation viol_diffRuleIdWithViol1 = new FlowAnalysisViolation(
                "RULE002", "ANALYZER001", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[0]);
        FlowAnalysisViolation viol_diffAnalyzerIdWithViol1 = new FlowAnalysisViolation(
                "RULE001", "ANALYZER002", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[0]);
        FlowAnalysisViolation viol_diffPathElementWithViol1 = new FlowAnalysisViolation(
                "RULE001", "ANALYZER001", "JAVA", this.resultLocation, "Message",
                "com.example", new IFlowAnalysisPathElement[1]);

        assertTrue(viol1.equals(viol1)); // Same object
        assertTrue(viol1.equals(viol_samePropsAsViol1)); // Same properties
        assertFalse(viol1.equals("")); // Different type
        assertFalse(viol1.equals(viol_diffRuleIdWithViol1)); // Different ruleId
        assertTrue(viol1.equals(viol_diffAnalyzerIdWithViol1)); // Different analyzerId
        assertFalse(viol1.equals(viol_diffPathElementWithViol1)); // Different pathElements
    }
}