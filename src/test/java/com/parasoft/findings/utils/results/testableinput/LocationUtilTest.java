package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.Test;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class LocationUtilTest {

    private final String projIdAttributeForTest = "projectId";
    private final String resProjPathAttributeForTest = "resProjPath";

    @Test
    public void testCreateTestableInput_withProjIdAndResProjPath() throws LocationsException {
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.PROJECT_ID_ATTR, projIdAttributeForTest);
        properties.put(IXmlTagsAndAttributes.RESOURCE_PROJECT_RELATIVE_PATH_ATTR, resProjPathAttributeForTest);

        FileTestableInput testResults = (FileTestableInput) LocationUtil.createTestableInput(properties);

        assertEquals(resProjPathAttributeForTest, testResults.getName());
        assertTrue(testResults.getFileLocation().getAbsolutePath().contains("projectId\\resProjPath"));
    }

    @Test
    public void testCreateTestableInput_withProjectAndSym() throws LocationsException {
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.PROJECT_ID_ATTR, projIdAttributeForTest);
        properties.put(IXmlTagsAndAttributes.RESOURCE_PROJECT_RELATIVE_PATH_ATTR, resProjPathAttributeForTest);
        properties.put(IXmlTagsAndAttributes.PROJECT_PATH_ATTR, "projPath");
        properties.put(IXmlTagsAndAttributes.PROJECT_ATTR, "project");
        properties.put(IXmlTagsAndAttributes.SYMBOLS_ATTR, "sym");

        FileTestableInput testResults = (FileTestableInput) LocationUtil.createTestableInput(properties);

        assertEquals(resProjPathAttributeForTest, testResults.getName());
        assertEquals("sym", testResults.getAttribute(IXmlTagsAndAttributes.SYMBOLS_ATTR));
    }

    @Test
    public void testCreateTestableInput_withScPath() throws LocationsException {
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.SOURCE_CONTROL_PATH_ATTR, "scPath");

        FileTestableInput testResults = (FileTestableInput) LocationUtil.createTestableInput(properties);

        assertEquals("scPath", testResults.getName());
    }

    @Test
    public void testCreateTestableInput_emptyProperties()  {
        LocationsException exception = assertThrows(LocationsException.class, () -> LocationUtil.createTestableInput(new Properties()));
        assertEquals("Failed to get URI for stored location.", exception.getMessage());
    }

    @Test
    public void testCreateTestableInput_nullProperties() {
        LocationsException exception = assertThrows(LocationsException.class, () -> LocationUtil.createTestableInput(null));
        assertEquals("Failed to get URI for stored location.", exception.getMessage());
    }

    @Test
    public void testCreateTestableInput_missingProperties() {
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.PROJECT_ID_ATTR, projIdAttributeForTest);

        LocationsException exception = assertThrows(LocationsException.class, () -> LocationUtil.createTestableInput(properties));
        assertEquals("Failed to get URI for stored location.", exception.getMessage());
    }
}
