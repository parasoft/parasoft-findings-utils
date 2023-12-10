package com.parasoft.findings.utils.common.utils;

import com.parasoft.findings.utils.common.util.URLUtil;
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
    public void testToURL() throws MalformedURLException {
        assertEquals(url, URLUtil.toURL("http://www.google.com"));
        assertEquals(file.toURI().toURL(), URLUtil.toURL(file.getAbsolutePath()));
        assertNull(URLUtil.toURL("http://www.google.com&"));
    }

    @Test
    public void testMakeFromPath() {
        assertNull(URLUtil.makeFromPath(null));
    }

    @Test
    public void testGetLocalFile() throws MalformedURLException {
        assertNull(URLUtil.getLocalFile(null));
        assertNull(URLUtil.getLocalFile(url));
        assertEquals(file.getAbsoluteFile(), URLUtil.getLocalFile(file.toURI().toURL()).getAbsoluteFile());
    }

    @Test
    public void testToFile() throws MalformedURLException {
        assertEquals(file.getAbsoluteFile(), URLUtil.toFile(file.toURI().toURL()).getAbsoluteFile());
        URL wrongUrl = new URL("http://www.google.com/|staticanalysis/cpptest_pro_report_202001.xml");
        assertNotNull(URLUtil.toFile(wrongUrl).getAbsoluteFile());
    }

    @Test
    public void testGetPath() throws MalformedURLException {
        try(MockedStatic<URLDecoder> mockedStatic = Mockito.mockStatic(URLDecoder.class)) {
            mockedStatic.when(() -> URLDecoder.decode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException());
            assertEquals(file.toURI().getPath(), URLUtil.getPath(file.toURI().toURL()));
        }
    }
}
