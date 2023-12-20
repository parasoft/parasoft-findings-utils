package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FileTestableInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class RuleViolationTest {
    private ResultLocation resultLocation;

    @BeforeEach
    public void setUp() {
        resultLocation = new ResultLocation(new FileTestableInput(new File("path/to/source/file")), new SourceRange(1, 2));
    }

    @Test
    public void testConstructorAndGetters() {
        String sAnalyzerId = "ANALYZER001";
        String sLanguageId = "JAVA";
        ResultLocation location = this.resultLocation;
        String sMessage = "Violation message";
        String sRuleId = "RULE001";
        String sPackage = "com.example";

        RuleViolation ruleViolation = new RuleViolation(
                sAnalyzerId, sLanguageId, location, sMessage, sRuleId, sPackage);

        assertEquals(sAnalyzerId, ruleViolation.getAnalyzerId());
        assertEquals(sLanguageId, ruleViolation.getLanguageId());
        assertEquals(location, ruleViolation.getResultLocation());
        assertEquals(sRuleId, ruleViolation.getRuleId());
        assertEquals(sMessage, ruleViolation.getMessage());
        assertEquals(sPackage, ruleViolation.getNamespace());
    }

    @Test
    public void testHashCode() {
        RuleViolation viol1 = new RuleViolation(
                "ANALYZER001", "JAVA", this.resultLocation, "Message", "RULE001", "com.example");
        RuleViolation viol_sameAsViol1 = new RuleViolation(
                "ANALYZER001", "JAVA", this.resultLocation, "Message", "RULE001", "com.example");

        assertEquals(viol_sameAsViol1.hashCode(), viol1.hashCode());
    }

    @Test
    public void testEquals() {
        RuleViolation viol1 = new RuleViolation(
                "ANALYZER001", "JAVA", this.resultLocation, "Message", "RULE001", "com.example");
        RuleViolation viol_samePropsAsViol1 = new RuleViolation(
                "ANALYZER001", "JAVA", this.resultLocation, "Message", "RULE001", "com.example");
        RuleViolation viol_diffRuleIdWithViol1 = new RuleViolation(
                "ANALYZER001", "JAVA", this.resultLocation, "Message", "RULE002", "com.example");
        RuleViolation viol_diffAnalyzerIdWithViol1 = new RuleViolation(
                "ANALYZER002", "JAVA", this.resultLocation, "Message", "RULE001", "com.example");

        assertTrue(viol1.equals(viol1)); // Same object
        assertTrue(viol1.equals(viol_samePropsAsViol1)); // Same properties
        assertFalse(viol1.equals("")); // Different type
        assertFalse(viol1.equals(viol_diffRuleIdWithViol1)); // Different ruleId
        assertTrue(viol1.equals(viol_diffAnalyzerIdWithViol1)); // Different analyzerId
    }
}
