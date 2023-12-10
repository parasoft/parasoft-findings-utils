package com.parasoft.findings.utils.common.utils;

import com.parasoft.findings.utils.common.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName(file));
        assertNull(FileUtil.getNoExtensionName((File) null));
    }

    @Test
    public void testListFilesByName() {
        File dir = new File("src/test/resources/xml/staticanalysis/");
        File wrongDir = new File("src/test/resources/xml/");
        assertEquals(0 ,FileUtil.listFilesByName(wrongDir, "cpptest_pro_report_202001").length);
        assertEquals(1 ,FileUtil.listFilesByName(dir, "cpptest_pro_report_202001").length);
    }

    @Test
    public void testGetNoExtensionName_fileName() {
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName("cpptest_pro_report_202001.xml"));
        assertEquals("cpptest_pro_report_202001", FileUtil.getNoExtensionName("cpptest_pro_report_202001"));
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
}
