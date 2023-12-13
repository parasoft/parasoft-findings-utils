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
        properties.put(IXmlTagsAndAttributes.FS_PATH, "test/project");
        properties.put(IXmlTagsAndAttributes.PROJECT_PATH_ATTR, "/project");
        properties.put(IXmlTagsAndAttributes.PROJECT_ATTR, "project");

        ITestableInput result = findingsLocationMatcher.matchLocation(properties, false);

        assertEquals("/file", ((PathInput) result).getName());

        // Verify override functions in PathInput
        PathInput pathInput = new PathInput("test/project/file");

        assertEquals(pathInput.hashCode(), result.hashCode());
        // When compared object is not PathInput type
        assertFalse(result.equals("test"));
        // When compared object is same as result
        assertTrue(result.equals(pathInput));
    }

    @Test
    public void testMatchLocation_withNullProperties() throws LocationsException {
        ITestableInput result = findingsLocationMatcher.matchLocation(null, false);

        assertNull(result);
    }

    @Test
    public void testGetFilePath_nullFileTestableInput() {
        String result = FindingsLocationMatcher.getFilePath(null);
        assertNull(result);
    }

    @Test
    public void testGetFilePath_nullFileLocation() {
        String result = FindingsLocationMatcher.getFilePath(mock(FileTestableInput.class));
        assertNull(result);
    }

    @Test
    public void testGetFilePath_fileExist() {
        File file = new File("src/test/resources/xml/staticanalysis/jtest_report_202001.xml");
        FileTestableInput fileTestableInput = new FileTestableInput(file);

        String result = FindingsLocationMatcher.getFilePath(fileTestableInput);

        assertEquals(file.getAbsolutePath(), result);
    }

    @Test
    public void testGetFilePath_fileNotExist_FileTestableInput() {
        File file = new File("test/file");
        FileTestableInput fileTestableInput = new FileTestableInput(file);

        String result = FindingsLocationMatcher.getFilePath(fileTestableInput);

        assertEquals(file.getAbsolutePath(), result);
    }

    @Test
    public void testGetFilePath_fileNotExist_ProjectFileTestableInput() {
        File file = new File("test/file");
        ProjectFileTestableInput projectFileTestableinput = new ProjectFileTestableInput(file,
                "project", "project", "/project", "relative/path");

        String result = FindingsLocationMatcher.getFilePath(projectFileTestableinput);

        assertEquals("project/relative/path", result);
    }

    @Test
    public void testCompute_onlyProjectNameInProjectPath() throws LocationsException {
        PathInput pathInput = new PathInput("/projectName");

        ITestableInput result = findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertEquals("projectName", result.getName());

        // Verify override functions in FileTestableInput
        FileTestableInput fileTestableInput = new FileTestableInput(new File("/projectName"));

        assertEquals(fileTestableInput.toString(), result.toString());
        assertEquals(fileTestableInput.hashCode(), result.hashCode());
        // When compared object is not FileTestableInput type
        assertFalse(result.equals("test"));
        // When compared object is same as result
        assertTrue(result.equals(fileTestableInput));
    }

    @Test
    public void testCompute_oneSegmentInProjectPath() throws LocationsException {
        PathInput pathInput = new PathInput("test/project");

        ITestableInput result = findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertEquals("test", ((ProjectFileTestableInput) result).getProjectName());
        assertEquals("/test", ((ProjectFileTestableInput) result).getProjectPath());
        assertEquals("project", ((ProjectFileTestableInput) result).getProjectRelativePath());

        // Verify override functions in ProjectFileTestableInput
        ProjectFileTestableInput projectFileTestableInput = new ProjectFileTestableInput(new File("test/project"),
                "test", "project", "/project", "project");

        assertEquals(projectFileTestableInput.hashCode(), result.hashCode());
        // When compared object is not ProjectFileTestableInput type
        assertFalse(result.equals("test"));
        // When compared object is ProjectFileTestableInput type
        assertTrue(result.equals(projectFileTestableInput));
        // When compared object is same as result
        assertTrue(result.equals(result));
    }

    @Test
    public void testCompute_moreThanOneSegmentInProjectPath() throws LocationsException {
        PathInput pathInput = new PathInput("test/project/files/file");

        ITestableInput result = findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertEquals("project", ((ProjectFileTestableInput) result).getProjectName());
        assertEquals("test/project", ((ProjectFileTestableInput) result).getProjectPath());
        assertEquals("files/file", ((ProjectFileTestableInput) result).getProjectRelativePath());
    }

    @Test
    public void testCompute_emptyFilePath() throws LocationsException {
        PathInput pathInput = new PathInput("");

        ITestableInput result = findingsLocationMatcher.matchLocation(pathInput, Collections.singletonList(0L), null, null, false);

        assertNull(((PathInput) result).getProjectName());
        assertNull(((PathInput) result).getProjectPath());
    }

    @Test
    public void testCompute_nullOriginalInput() throws LocationsException {
        ITestableInput result = findingsLocationMatcher.matchLocation(null, Collections.singletonList(0L), null, null, false);
        assertNull(result);
    }
}
