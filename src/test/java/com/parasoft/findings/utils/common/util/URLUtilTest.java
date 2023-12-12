package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class URLUtilTest {

    @Test
    public void testToURL_normal() throws MalformedURLException {
        URL preparedUrl = new URL("http://www.google.com");
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        //When give url
        assertEquals(preparedUrl, URLUtil.toURL("http://www.google.com"));
        //When give file path
        assertEquals(preparedFile.toURI().toURL(), URLUtil.toURL(preparedFile.getAbsolutePath()));
    }

    @Test
    public void testToURL_whenStringCouldNotBeParsed() {
        //When given string could not be parsed as a URI
        assertNull(URLUtil.toURL("http://www.google.com&"));
    }

    @Test
    public void testMakeFromPath() {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        //When given string is null
        assertNull(URLUtil.makeFromPath(null));
        //When given String is not null
        assertEquals("src\\test\\resources\\xml\\staticanalysis\\cpptest_pro_report_202001.xml", preparedFile.getPath());
    }

    @Test
    public void testGetLocalFile() throws MalformedURLException {
        URL preparedUrl = new URL("http://www.google.com");
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        //When given url is null
        assertNull(URLUtil.getLocalFile(null));
        //When given url is not file
        assertNull(URLUtil.getLocalFile(preparedUrl));
        //When given url is file
        assertEquals(preparedFile.getAbsoluteFile(), URLUtil.getLocalFile(preparedFile.toURI().toURL()).getAbsoluteFile());
    }

    @Test
    public void testToFile_normal() throws MalformedURLException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        assertEquals(preparedFile.getAbsoluteFile(), URLUtil.toFile(preparedFile.toURI().toURL()).getAbsoluteFile());
    }

    @Test
    public void testToFile_whenGivenURIHasBlank() throws MalformedURLException {
        URL wrongUrl = new URL("file://E:\\parasoft-findings-utils\\src\\test\\resources\\xml\\static analysis\\cpptest_pro_report_202001.xml");
        File testFile = new File("src/test/resources/xml/static analysis/", "cpptest_pro_report_202001.xml");
        assertEquals(testFile.getAbsoluteFile(), URLUtil.toFile(wrongUrl).getAbsoluteFile());
    }

    @Test
    public void testGetPath_normal() throws MalformedURLException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        assertEquals(preparedFile.toURI().getPath(), URLUtil.getPath(preparedFile.toURI().toURL()));
    }

    @Test
    public void testGetPath_whenDecodeThrowException() throws MalformedURLException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        try(MockedStatic<URLDecoder> mockedStatic = Mockito.mockStatic(URLDecoder.class)) {
            mockedStatic.when(() -> URLDecoder.decode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException("Expected test error"));
            assertEquals(preparedFile.toURI().getPath(), URLUtil.getPath(preparedFile.toURI().toURL()));
        }
    }
}
