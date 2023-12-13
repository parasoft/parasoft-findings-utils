package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.Test;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class LocationUtilTest {

    @Test
    public void testCreateTestableInput_projIdAndResProjPathAttributesInProperties() throws LocationsException {
        Properties properties = new Properties();
        properties.put("projId", "project");
        properties.put("resProjPath", "test");

        ITestableInput result = LocationUtil.createTestableInput(properties);

        assertEquals("test", result.getName());
        assertTrue(((FileTestableInput) result).getFileLocation().getAbsolutePath().contains("project\\test"));
    }

    @Test
    public void testCreateTestableInput_scPathAttributeInProperties() throws LocationsException {
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.SOURCE_CONTROL_PATH_ATTR, "project");
        properties.put("test", "files");

        ITestableInput result = LocationUtil.createTestableInput(properties);

        assertEquals("project", result.getName());
        assertEquals("files", ((FileTestableInput) result).getAttribute("test"));
    }

    @Test
    public void testCreateTestableInput_SymIdAttributeInProperties() {
        try {
            Properties properties = new Properties();
            properties.put(IXmlTagsAndAttributes.SOURCE_CONTROL_PATH_ATTR, "project");
            properties.put(IXmlTagsAndAttributes.SYMBOL_ID_ATTR, "test");
            LocationUtil.createTestableInput(properties);
        } catch (Exception e){
            assertEquals("Need to check and refactor", e.getMessage());
        }
    }

    @Test
    public void testCreateTestableInput_noSymIdAttributeInProperties() throws LocationsException {
        // When properties include uri and scPath attributes
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.SOURCE_CONTROL_PATH_ATTR, "project");
        properties.put(IXmlTagsAndAttributes.URI_ATTR, "http://test/project");
        properties.put("test", "files");

        ITestableInput result = LocationUtil.createTestableInput(properties);

        assertEquals("http://test/project", result.getName());
        assertEquals("http://test/project", ((RemoteTestableInput) result).getProjectRelativePath());
        assertNull(((RemoteTestableInput) result).getProjectName());
        assertEquals("files", ((RemoteTestableInput) result).getAttribute("test"));

        // When properties also include projId and project attributes
        properties.put(IXmlTagsAndAttributes.PROJECT_ID_ATTR, "projectId");
        properties.put(IXmlTagsAndAttributes.PROJECT_ATTR, "test");

        result = LocationUtil.createTestableInput(properties);

        assertEquals("test", ((RemoteTestableInput) result).getProjectName());
        assertEquals("test", ((RemoteTestableInput) result).getProjectPath());
    }

    @Test
    public void testCreateTestableInput_emptyProperties()  {
        try {
            LocationUtil.createTestableInput(new Properties());
        } catch (LocationsException le){
            assertEquals("Failed to get URI for stored location.", le.getMessage());
        }
    }

    @Test
    public void testCreateTestableInput_nullProperties() {
        try {
            LocationUtil.createTestableInput(null);
        } catch (LocationsException le){
            assertEquals("Failed to get URI for stored location.", le.getMessage());
        }
    }
}
