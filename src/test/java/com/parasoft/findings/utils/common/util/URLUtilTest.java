package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.BeforeEach;
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
    URL url;
    File file;
    @BeforeEach
    public void setURL() throws MalformedURLException {
        url = new URL("http://www.google.com");
        file = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
    }

    @Test
    public void testToURL_normal() throws MalformedURLException {
        //When give url
        assertEquals(url, URLUtil.toURL("http://www.google.com"));
        //When give file path
        assertEquals(file.toURI().toURL(), URLUtil.toURL(file.getAbsolutePath()));
    }

    @Test
    public void testToURL_String_could_not_be_parsed() throws MalformedURLException {
        //When given String could not be parsed as a URI
        assertNull(URLUtil.toURL("http://www.google.com&"));
    }

    @Test
    public void testMakeFromPath() {
        //When given String is null
        assertNull(URLUtil.makeFromPath(null));
        //When given String is not null
        assertEquals("src\\test\\resources\\xml\\staticanalysis\\cpptest_pro_report_202001.xml", file.getPath());
    }

    @Test
    public void testGetLocalFile() throws MalformedURLException {
        //When given url is null
        assertNull(URLUtil.getLocalFile(null));
        //When given url is not file
        assertNull(URLUtil.getLocalFile(url));
        //When given url is file
        assertEquals(file.getAbsoluteFile(), URLUtil.getLocalFile(file.toURI().toURL()).getAbsoluteFile());
    }

    @Test
    public void testToFile_normal() throws MalformedURLException {
       assertEquals(file.getAbsoluteFile(), URLUtil.toFile(file.toURI().toURL()).getAbsoluteFile());
    }

    @Test
    public void testToFile_when_given_URI_has_blank() throws MalformedURLException {
        URL wrongUrl = new URL("file://E:\\parasoft-findings-utils\\src\\test\\resources\\xml\\static analysis\\cpptest_pro_report_202001.xml");
        File testFile = new File("src/test/resources/xml/static analysis/", "cpptest_pro_report_202001.xml");
        assertEquals(testFile.getAbsoluteFile(), URLUtil.toFile(wrongUrl).getAbsoluteFile());
    }

    @Test
    public void testGetPath_normal() throws MalformedURLException {
        assertEquals(file.toURI().getPath(), URLUtil.getPath(file.toURI().toURL()));
    }

    @Test
    public void testGetPath_when_decode_throw_exception() throws MalformedURLException {
        try(MockedStatic<URLDecoder> mockedStatic = Mockito.mockStatic(URLDecoder.class)) {
            mockedStatic.when(() -> URLDecoder.decode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException("Expected test error"));
            assertEquals(file.toURI().getPath(), URLUtil.getPath(file.toURI().toURL()));
        }
    }
}
