package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FileTestableInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DupCodeViolationTest {
    private ResultLocation resultLocation;

    @BeforeEach
    public void setUp() {
        resultLocation = new ResultLocation(new FileTestableInput(new File("path/to/source/file")), new SourceRange(1, 2));
    }

    @Test
    public void testConstructorAndGetters() {
        String ruleId = "RULE001";
        String analyzerId = "ANALYZER001";
        ResultLocation resultLocation = this.resultLocation;
        String message = "Violation message";
        String languageId = "JAVA";
        IPathElement[] pathElements = new IPathElement[0];

        DupCodeViolation violation = new DupCodeViolation(
                ruleId, analyzerId, this.resultLocation, message, languageId, pathElements);

        assertEquals(ruleId, violation.getRuleId());
        assertEquals(analyzerId, violation.getAnalyzerId());
        assertEquals(resultLocation, violation.getResultLocation());
        assertEquals(message, violation.getMessage());
        assertEquals(languageId, violation.getLanguageId());
        assertEquals(pathElements, violation.getPathElements());
    }

    @Test
    public void testHashCode() {
        DupCodeViolation viol1 = new DupCodeViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA", new IPathElement[0]);
        DupCodeViolation viol_sameAsViol1 = new DupCodeViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA", new IPathElement[0]);

        assertEquals(viol1.hashCode(), viol_sameAsViol1.hashCode());
    }

    @Test
    public void testEquals() {
        DupCodeViolation viol1 = new DupCodeViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA", new IPathElement[0]);
        DupCodeViolation viol_samePropsAsViol1 = new DupCodeViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA", new IPathElement[0]);
        DupCodeViolation viol_diffRuleIdWithViol1 = new DupCodeViolation(
                "RULE002", "ANALYZER001", this.resultLocation, "Message", "JAVA", new IPathElement[0]);
        DupCodeViolation viol_diffAnalyzerIdWithViol1 = new DupCodeViolation(
                "RULE001", "ANALYZER002", this.resultLocation, "Message", "JAVA", new IPathElement[0]);
        DupCodeViolation viol_diffPathElementWithViol1 = new DupCodeViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA", new IPathElement[1]);

        assertTrue(viol1.equals(viol1)); // Same object
        assertFalse(viol1.equals("")); // Different type
        assertTrue(viol1.equals(viol_samePropsAsViol1)); // Same properties
        assertFalse(viol1.equals(viol_diffRuleIdWithViol1)); // Different ruleId
        assertTrue(viol1.equals(viol_diffAnalyzerIdWithViol1)); // Different analyzerId
        assertFalse(viol1.equals(viol_diffPathElementWithViol1)); // Different pathElements
    }

    @Test
    public void testToString() {
        IPathElement[] pathElements = new IPathElement[1];
        pathElements[0] = new DupCodePathElement("DESC", this.resultLocation);
        DupCodeViolation viol = new DupCodeViolation(
                "RULE001", "ANALYZER001", this.resultLocation, "Message", "JAVA", pathElements);
        System.out.println(viol);
        String expectedToString = "Message" + System.lineSeparator() + "   + path/to/source/file  [1:2 - 2:0]" + System.lineSeparator();
        assertEquals(expectedToString, viol.toString());
    }
}