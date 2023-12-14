package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PathInputTest {

    private final PathInput pathInput = new PathInput("test/project/file");
    private PathInput testResults;

    @BeforeEach
    public void prepareTestVariables() throws LocationsException {
        FindingsLocationMatcher findingsLocationMatcher = new FindingsLocationMatcher();
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.LOC_ATTR, "test/project/file");
        testResults = (PathInput) findingsLocationMatcher.matchLocation(properties, false);
    }

    @Test
    public void testHashCode() {
        assertEquals(pathInput.hashCode(), testResults.hashCode());
    }

    @Test
    public void testEquals_withPathInputObject() {
        assertTrue(testResults.equals(pathInput));
    }

    @Test
    public void testEquals_withOtherObject() {
        assertFalse(testResults.equals("testEqualsFunction"));
    }
}
