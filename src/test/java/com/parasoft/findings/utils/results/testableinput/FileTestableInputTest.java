package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class FileTestableInputTest {

    private final FileTestableInput fileTestableInput = new FileTestableInput(new File("/projectName"));
    private FileTestableInput testResults;

    @BeforeEach
    public void prepareTestVariables() throws LocationsException {
        FindingsLocationMatcher findingsLocationMatcher = new FindingsLocationMatcher();
        PathInput pathInput = new PathInput("/projectName");
        testResults = (FileTestableInput) findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);
    }

    @Test
    public void testToString() {
        assertEquals(fileTestableInput.toString(), testResults.toString());
    }

    @Test
    public void testHashCode() {
        assertEquals(fileTestableInput.hashCode(), testResults.hashCode());
    }

    @Test
    public void testEquals_withFileTestableInputObject() {
        assertTrue(testResults.equals(fileTestableInput));
    }

    @Test
    public void testEquals_withOtherObject() {
        assertFalse(testResults.equals("testEqualsFunction"));
    }
}
