package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteTestableInputTest {
    private final Properties properties = new Properties();
    private final String uriForTest = "http://test/project";
    private final String projectForTest = "project";
    RemoteTestableInput testResults;

    @BeforeEach
    public void prepareTestVariables() throws LocationsException {
        properties.put(IXmlTagsAndAttributes.URI_ATTR, uriForTest);
        properties.put(IXmlTagsAndAttributes.PROJECT_ID_ATTR, "projectId");
        properties.put(IXmlTagsAndAttributes.PROJECT_ATTR, projectForTest);
        testResults = (RemoteTestableInput) LocationUtil.createTestableInput(properties);
    }

    @Test
    public void testGetAttribute() throws LocationsException {
        properties.put("attributeForTest", "files");

        RemoteTestableInput results = (RemoteTestableInput) LocationUtil.createTestableInput(properties);

        assertEquals("files", results.getAttribute("attributeForTest"));
    }

    @Test
    public void testGetName()  {
        assertEquals(uriForTest, testResults.getName());
    }

    @Test
    public void testGetProjectName() {
        assertEquals(projectForTest, testResults.getProjectName());
    }

    @Test
    public void testGetProjectPath() {
        assertEquals(projectForTest, testResults.getProjectPath());
    }

    @Test
    public void testGetProjectRelativePath() {
        assertEquals(uriForTest, testResults.getProjectRelativePath());
    }
}
