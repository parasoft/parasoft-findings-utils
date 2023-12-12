package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}
