package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectFileTestableInputTest {

    private final ProjectFileTestableInput projectFileTestableInput = new ProjectFileTestableInput(new File("test/project"),
            "test", "project", "/project", "project");
    private ProjectFileTestableInput testResults;

    @BeforeEach
    public void prepareTestVariables() throws LocationsException {
        FindingsLocationMatcher findingsLocationMatcher = new FindingsLocationMatcher();
        PathInput pathInput = new PathInput("test/project");
        testResults = (ProjectFileTestableInput) findingsLocationMatcher.matchLocation(pathInput,
                Collections.singletonList(0L), null, null, false);
    }

    @Test
    public void testHashCode() {
        assertEquals(projectFileTestableInput.hashCode(), testResults.hashCode());
    }

    @Test
    public void testEquals_withSameObject() {
        assertTrue(testResults.equals(testResults));
    }

    @Test
    public void testEquals_withProjectFileTestableInputObject() {
        assertTrue(testResults.equals(projectFileTestableInput));
    }

    @Test
    public void testEquals_withOtherObject() {
        assertFalse(testResults.equals("testEqualsFunction"));
    }
}
