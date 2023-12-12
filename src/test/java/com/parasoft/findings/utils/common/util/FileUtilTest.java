package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileUtilTest {

    /**
     * Test {@link FileUtil#getNoExtensionName(File)}
     */
    @Test
    public void testGetNoExtensionName_1() {
        //Normal
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName(preparedFile));
        //File is null
        assertNull(FileUtil.getNoExtensionName((File) null));
    }

    /**
     * Test {@link FileUtil#getNoExtensionName(String)}
     */
    @Test
    public void testGetNoExtensionName_2() {
        //Normal, file with extension
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName("cpptest_pro_report_202001.xml"));
        //Normal, file without extension
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName("cpptest_pro_report_202001"));
        //FileName is null
        assertNull(FileUtil.getNoExtensionName((String) null));
    }

    @Test
    public void testListFilesByName() {
        File dir = new File("src/test/resources/xml/staticanalysis/");
        File wrongDir = new File("src/test/resources/xml/");
        //Normal
        assertEquals(1 ,FileUtil.listFilesByName(dir, "cpptest_pro_report_202001").length);
        //Can not find the file
        assertEquals(0 ,FileUtil.listFilesByName(wrongDir, "cpptest_pro_report_202001").length);
    }

    /**
     * Test {@link FileUtil#readFile(File, List)}
     */
    @Test
    public void testReadFile_1() throws IOException {
        List<String> lines = new ArrayList<>();
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        FileUtil.readFile(preparedFile, lines);

        assertEquals(19448, lines.size());
        assertTrue(lines.get(19428).contains("<Total name=\"Suppressed / Total\" supp=\"0\" total=\"2149\">"));
    }

    /**
     * Test {@link FileUtil#readFile(File, String)}
     */
    @Test
    public void testReadFile_2() throws IOException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        assertTrue(FileUtil.readFile(preparedFile, "UTF-8").contains("<Total name=\"Suppressed / Total\" supp=\"0\" total=\"2149\">"));
    }

    /**
     * Test {@link FileUtil#readFile(BufferedReader)}
     */
    @Test
    public void testReadFile_3() throws IOException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        FileInputStream fileStream = new FileInputStream(preparedFile);
        InputStreamReader streamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        assertTrue(FileUtil.readFile(reader).contains("<Total name=\"Suppressed / Total\" supp=\"0\" total=\"2149\">"));
    }

    @Test
    public void testRecursiveCopy() throws IOException {
        File sourceDir = new File("src/test/resources/xml/");
        File destinationDir = new File("src/test/resources/delete/");
        File childDir = new File("src/test/resources/xml/staticanalysis/");
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)){
            //Normal
            assertFalse(destinationDir.exists());
            FileUtil.recursiveCopy(sourceDir, destinationDir);
            assertTrue(destinationDir.exists());
            //When copy directory onto itself
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);
            FileUtil.recursiveCopy(sourceDir, sourceDir);
            verify(logger, times(1)).warn("Trying to copy directory onto itself!");
        } finally {
            if (destinationDir.exists()) {
                FileUtil.recursiveDelete(destinationDir);
            }
        }

        //When destinationDir a child of sourceDir
        try {
            FileUtil.recursiveCopy(sourceDir, childDir);
        } catch (IOException e) {
            assertEquals("Cannot copy from " + sourceDir.getAbsolutePath() + " to: " + childDir.getAbsolutePath(), e.getMessage());
        }
    }

    @Test
    public void testOverlaps() {
        //When path1's length is grater than path2's length
        assertFalse(FileUtil.overlaps("src/test/resources/xml/staticanalysis", "src/test/resources/xml"));
        //When path2 is a child of path1
        assertTrue(FileUtil.overlaps("src/test/resources/xml", "src/test/resources/xml/staticanalysis"));
        //When path2 is not child of path1
        assertFalse(FileUtil.overlaps("src1/test/resources/xml", "src2/test/resources/xml/staticanalysis"));
    }

    @Test
    public void testCopyFile() throws IOException {
        File sourceFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        File destinationFile = new File("src/test/resources/xml/jtest_report_202202.xml");
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)){
            //When copy file onto itself
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);
            FileUtil.copyFile(sourceFile, sourceFile);
            verify(logger, times(1)).warn("Trying to copy file onto itself!");
            //Normal
            assertNotEquals(sourceFile.length(), destinationFile.length());
            FileUtil.copyFile(sourceFile, destinationFile);
            assertEquals(sourceFile.length(), destinationFile.length());
        } finally {
            if (destinationFile.exists()) {
                assertTrue(destinationFile.delete());
            }
        }
    }

    @Test
    public void testRecursiveDelete() throws IOException {
        File sourceFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        File destinationFile1 = new File("src/test/resources/xml/destinationDir/", "destinationFile1.xml");
        File destinationFile2 = new File("src/test/resources/xml/destinationDir/", "destinationFile2.xml");
        File destinationDir = new File("src/test/resources/xml/destinationDir");

        assertTrue(destinationDir.mkdirs());
        FileUtil.copyFile(sourceFile, destinationFile1);
        FileUtil.copyFile(sourceFile, destinationFile2);

        //When delete a file
        assertTrue(FileUtil.recursiveDelete(destinationFile1));
        //When the file does not exist
        assertTrue(FileUtil.recursiveDelete(destinationFile1));
        //When delete a dir
        assertTrue(FileUtil.recursiveDelete(destinationDir));
    }
}
