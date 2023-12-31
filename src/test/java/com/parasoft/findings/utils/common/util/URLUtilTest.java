package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    public void testMakeFromPath() throws MalformedURLException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        //When given string is null
        assertNull(URLUtil.makeFromPath(null));
        //When given String is not null
        assertEquals(preparedFile.toURI().toURL(), URLUtil.makeFromPath(preparedFile.getPath()));
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
    // ？
    public void testToFile_incorrectUrl() throws MalformedURLException {
        File testFile = new File("src/test/resources/xml/static  analysis/", "cpptest_pro_report_202001.xml");
        URL urlWithEscapedSpace = testFile.toURI().toURL();
        // urlWithUnescapedSpace can not be converted to URI
        URL urlWithUnescapedSpace = new URL(urlWithEscapedSpace.toString().replace("%20", " "));
        assertEquals(testFile.getAbsoluteFile(), URLUtil.toFile(urlWithUnescapedSpace).getAbsoluteFile());
    }

    @Test
    public void testGetPath_normal() throws MalformedURLException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        assertEquals(preparedFile.toURI().getPath(), URLUtil.getPath(preparedFile.toURI().toURL()));
    }

    @Test
    public void testGetPath_throwExceptionWhenDecode() throws MalformedURLException {
        File preparedFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        try (MockedStatic<URLDecoder> mockedStaticURLDecoder = Mockito.mockStatic(URLDecoder.class);
             MockedStatic<Logger> mockedStaticLogger = Mockito.mockStatic(Logger.class)) {
            mockedStaticURLDecoder.when(() -> URLDecoder.decode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException("Expected test error"));
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStaticLogger.when(Logger::getLogger).thenReturn(logger);

            assertEquals(preparedFile.toURI().getPath(), URLUtil.getPath(preparedFile.toURI().toURL()));
            verify(logger, times(1)).error(eq("An exception is thrown during decode URL. The uncoded path will be returned"), any());
        }
    }
}
