package com.parasoft.findings.utils.results.location;

import com.parasoft.findings.utils.results.testableinput.ITestableInput;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.PathInput;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LegacyResultLocationsReaderTest {
    @Test
    public void testStartElement_LocationsTag() {
        try {
            LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(null);
            // Will do nothing
            underTest.startElement("", "", "Locations", mock(Attributes.class));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testStartElement_LocTag() {
        try {
            AttributesImpl attributes = new AttributesImpl();
            String locValue = "/path/to/source/file";
            attributes.addAttribute("", "", IXmlTagsAndAttributes.LOC_ATTR, "CDATA", locValue);
            LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(null);

            underTest.startElement("", "", IXmlTagsAndAttributes.LOCATION_TAG, attributes);
            Properties storedLocation = underTest.getStoredLocation(locValue);
            assertNotNull(storedLocation);
            assertEquals(1, storedLocation.size());
            assertEquals(locValue, storedLocation.getProperty(IXmlTagsAndAttributes.LOC_ATTR));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testStartElement_unsupportedTag() {
        try {
            LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(null);

            underTest.startElement("", "", "ILLEGAL_TAG", mock(Attributes.class));
        } catch (Exception e) {
            assertTrue(e instanceof SAXException);
            assertEquals("Tag with illegal name spotted:ILLEGAL_TAG", e.getMessage());
        }
    }

    @Test
    public void testEndElement_LocationsTag() {
        try {
            LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(null);

            // Should do nothing
            underTest.endElement("", "", "Locations");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEndElement_LocTag() {
        try {
            LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(null);

            // Should do nothing
            underTest.endElement("", "", "Loc");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEndElement_unsupportedTag() {
        try {
            LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(null);

            underTest.endElement("", "", "ILLEGAL_TAG");
        } catch (Exception e) {
            assertTrue(e instanceof SAXException);
            assertEquals("Tag with illegal name spotted:ILLEGAL_TAG", e.getMessage());
        }
    }

    @Test
    public void testGetResultLocation_noInputFound() throws Throwable {
        LegacyResultLocationsReader underTest = spy(new LegacyResultLocationsReader(null));
        doReturn(null).when(underTest).getTestableInput(anyString(), any(Boolean.class));

        ResultLocation resultLocation = underTest.getResultLocation("loc", mock(SourceRange.class), true);

        assertNull(resultLocation);
    }

    @Test
    public void testGetResultLocation_inputFound() throws Throwable {
        LegacyResultLocationsReader underTest = spy(new LegacyResultLocationsReader(null));
        ITestableInput mockedInput = mock(ITestableInput.class);
        doReturn(mockedInput).when(underTest).getTestableInput(anyString(), any(Boolean.class));
        SourceRange mockedSourceRange = mock(SourceRange.class);

        ResultLocation resultLocation = underTest.getResultLocation("loc", mockedSourceRange, true);

        assertNotNull(resultLocation);
        assertEquals(mockedSourceRange, resultLocation.getSourceRange());
        assertEquals(mockedInput, resultLocation.getTestableInput());
    }

    @Test
    public void testGetTestableInput_notFoundStoredLocation() throws Throwable {
        LegacyResultLocationsReader underTest = spy(new LegacyResultLocationsReader(null));
        doReturn(null).when(underTest).getStoredLocation(anyString());

        PathInput input = (PathInput) underTest.getTestableInput("loc", true);

        assertEquals("loc", input.getPath());
    }

    @Test
    public void testGetTestableInput_foundStoredLocation() throws Throwable {
        ITestableInputLocationMatcher mockedMatcher = mock(ITestableInputLocationMatcher.class);
        LegacyResultLocationsReader underTest = spy(new LegacyResultLocationsReader(mockedMatcher));
        Properties properties = new Properties();
        doReturn(properties).when(underTest).getStoredLocation(any());

        underTest.getTestableInput("loc", true);

        verify(mockedMatcher).matchLocation(properties, true);
    }

    @Test
    public void testGetLocationMatcher() {
        ITestableInputLocationMatcher mockedMatcher = mock(ITestableInputLocationMatcher.class);
        LegacyResultLocationsReader underTest = new LegacyResultLocationsReader(mockedMatcher);

        assertEquals(mockedMatcher, underTest.getLocationMatcher());
    }
}