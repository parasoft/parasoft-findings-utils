package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.results.violations.LocationsException;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class FindingsLocationMatcherTest {

    private final FindingsLocationMatcher findingsLocationMatcher = new FindingsLocationMatcher();

    @Test
    public void testMatchLocation_withProperties() throws LocationsException {
        Properties properties = new Properties();
        properties.put(IXmlTagsAndAttributes.LOC_ATTR, "test/project/file");

        PathInput testResults = (PathInput) findingsLocationMatcher.matchLocation(properties, false);

        assertEquals("/file", testResults.getName());
        assertEquals("test/project/file", testResults.getPath());
    }

    @Test
    public void testMatchLocation_withNullProperties() throws LocationsException {
        assertNull(findingsLocationMatcher.matchLocation(null, false));
    }

    @Test
    public void testGetFilePath_nullFileTestableInput() {
        assertNull(FindingsLocationMatcher.getFilePath(null));
    }

    @Test
    public void testGetFilePath_nullFileLocation() {
        assertNull(FindingsLocationMatcher.getFilePath(mock(FileTestableInput.class)));
    }

    @Test
    public void testGetFilePath_fileExist() {
        File file = new File("src/test/resources/xml/staticanalysis/jtest_report_202001.xml");
        FileTestableInput fileTestableInput = new FileTestableInput(file);

        String testResults = FindingsLocationMatcher.getFilePath(fileTestableInput);

        assertEquals(file.getAbsolutePath(), testResults);
    }

    @Test
    public void testGetFilePath_fileNotExist_FileTestableInput() {
        File file = new File("test/file");
        FileTestableInput fileTestableInput = new FileTestableInput(file);

        String testResults = FindingsLocationMatcher.getFilePath(fileTestableInput);

        assertEquals(file.getAbsolutePath(), testResults);
    }

    @Test
    public void testGetFilePath_fileNotExist_ProjectFileTestableInput() {
        File file = new File("test/file");
        ProjectFileTestableInput projectFileTestableinput = new ProjectFileTestableInput(file,
                "project", "project", "/project", "relative/path");

        String testResults = FindingsLocationMatcher.getFilePath(projectFileTestableinput);

        assertEquals("project/relative/path", testResults);
    }

    @Test
    public void testCompute_onlyProjectNameInProjectPath() throws LocationsException {
        PathInput pathInput = new PathInput("/projectName");

        FileTestableInput testResults = (FileTestableInput) findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertEquals("projectName", testResults.getName());
    }

    @Test
    public void testCompute_oneSegmentInProjectPath() throws LocationsException {
        PathInput pathInput = new PathInput("test/project");

        ProjectFileTestableInput testResults = (ProjectFileTestableInput) findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertEquals("test", testResults.getProjectName());
        assertEquals("/test", testResults.getProjectPath());
        assertEquals("project", testResults.getProjectRelativePath());
    }

    @Test
    public void testCompute_multipleSegmentsInProjectPath() throws LocationsException {
        PathInput pathInput = new PathInput("test/project/files/file");

        ProjectFileTestableInput testResults = (ProjectFileTestableInput) findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertEquals("project", testResults.getProjectName());
        assertEquals("test/project", testResults.getProjectPath());
        assertEquals("files/file", testResults.getProjectRelativePath());
    }

    @Test
    public void testCompute_emptyFilePath() throws LocationsException {
        PathInput pathInput = new PathInput("");

        PathInput testResults = (PathInput) findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertNull(testResults.getProjectName());
        assertNull(testResults.getProjectPath());
    }

    @Test
    public void testCompute_nullOriginalInput() throws LocationsException {
        ITestableInput testResults = findingsLocationMatcher.matchLocation(null, Collections.singletonList(0L), null, null, false);
        assertNull(testResults);
    }
}
