package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest {
    File file;
    URL url;
    @BeforeEach
    public void setURL() throws MalformedURLException {
        file = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        url = new URL("https://www.google.com");
    }
    @Test
    public void testGetNoExtensionName_file() {
        //Normal
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName(file));
        //File is null
        assertNull(FileUtil.getNoExtensionName((File) null));
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

    @Test
    public void testGetNoExtensionName_by_fileName() {
        //Normal, file with extension
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName("cpptest_pro_report_202001.xml"));
        //Normal, file without extension
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName("cpptest_pro_report_202001"));
        //FileName is null
        assertNull(FileUtil.getNoExtensionName((String) null));
    }

    @Test
    public void testReadFile_list() throws IOException {
        List<String> list = new ArrayList<>();
        FileUtil.readFile(file, list);
        assertEquals(19448, list.size());
        assertEquals("         <Total name=\"Suppressed / Total\" supp=\"0\" total=\"2149\">", list.get(19428));
    }

    @Test
    public void testReadFile_encoding() throws IOException {
        assertTrue(FileUtil.readFile(file, "UTF-8").contains("         <Total name=\"Suppressed / Total\" supp=\"0\" total=\"2149\">"));
    }

    @Test
    public void testReadFile_reader() throws IOException {
        FileInputStream fileStream = new FileInputStream(file);
        InputStreamReader streamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        assertTrue(FileUtil.readFile(reader).contains("         <Total name=\"Suppressed / Total\" supp=\"0\" total=\"2149\">"));
    }
}
