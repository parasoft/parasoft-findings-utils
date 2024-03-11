package com.parasoft.findings.utils.results.location;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import com.parasoft.findings.utils.results.testableinput.LocationUtil;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LocationsReaderTest {
    String uri = "file://machine-name/C:/Workspace/jenkins_original_code_workspace/workspace/cicd.findings.cpptest.std.Timer.remote_docs/stdinout.c";
    String localName = "";

    @Test
    public void testStartElement_LocationsTag() {
        try {
            LocationsReader underTest = new LocationsReader();
            // Will do nothing
            underTest.startElement(uri , localName, "Locations", mock(Attributes.class));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testStartElement_unsupportedTag() {
        try {
            LocationsReader underTest = new LocationsReader();
            underTest.startElement(uri, localName, "Illegal_Tag", mock(Attributes.class));
        } catch (Exception e) {
            assertTrue(e instanceof SAXException);
            assertEquals("Tag with illegal name spotted:Illegal_Tag", e.getMessage());
        }
    }

    @Test
    public void testStartElement_locRefAttrNotExist() {
        LocationsReader underTest = new LocationsReader();
        try {
            AttributesImpl attributes = new AttributesImpl();

            underTest.startElement(uri, localName, "Loc", attributes);
        } catch (Exception e) {
            assertTrue(e instanceof SAXException);
            assertEquals("Location ref attribute not found.", e.getMessage());
        }
    }

    @Test
    public void testStartElement_readRepRefNotFound() {
        try (MockedStatic<LocationUtil> mockedStatic = mockStatic(LocationUtil.class)) {
            LocationsReader underTest = new LocationsReader();
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "", IXmlTagsAndAttributes.LOC_REF_ATTR, "CDATA", "locationRef");

            underTest.startElement(uri, localName, "Loc", attributes);

            mockedStatic.verify(() -> LocationUtil.readStoredLocation(attributes));
            assertNull(underTest.getStoredLocation("locationRef"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testStartElement_repRefNotFound() {
        // Prepare repositoryMapping
        Map<String, String> repositoriesMapping = new HashMap<>();
        repositoriesMapping.put("repoRef2", "repoAddress2");
        repositoriesMapping.put("repoRef3", "repoAddress3");
        // Prepare mock
        FindingsLogger mockedFindingsLogger = mock(FindingsLogger.class);
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", IXmlTagsAndAttributes.LOC_REF_ATTR, "CDATA", "locationRef");
        attributes.addAttribute("", "", IXmlTagsAndAttributes.REP_REF_ATTR, "CDATA", "repoRef1");
        try ( MockedStatic<FindingsLogger> mockedStaticFindingsLogger = mockStatic(FindingsLogger.class) ) {
            // mock logger
            mockedStaticFindingsLogger.when(() -> FindingsLogger.getLogger(Logger.class)).thenReturn(mockedFindingsLogger);

            LocationsReader underTest = new LocationsReader(repositoriesMapping);
            underTest.startElement(uri, localName, "Loc", attributes);
        } catch (Exception e) {
            assertTrue(e instanceof SAXException);
            assertEquals("Repository ref not matched: repoRef1", e.getMessage());
            verify(mockedFindingsLogger).error("Ref matching failed for repository mappings: ");
            verify(mockedFindingsLogger).error("    repoRef2 -> repoAddress2");
            verify(mockedFindingsLogger).error("    repoRef3 -> repoAddress3");
        }
    }

    @Test
    public void testStartElement_repRefFound() throws Throwable {
        // Prepare repositoryMapping
        Map<String, String> repositoriesMapping = new HashMap<>();
        repositoriesMapping.put("repoRef1", "repoAddress");
        // Prepare mock
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", IXmlTagsAndAttributes.LOC_REF_ATTR, "CDATA", "locationRef");
        attributes.addAttribute("", "", IXmlTagsAndAttributes.REP_REF_ATTR, "CDATA", "repoRef1");

        LocationsReader underTest = new LocationsReader(repositoriesMapping);
        underTest.startElement(uri, localName, "Loc", attributes);
        Properties storedLocation = underTest.getStoredLocation("locationRef");

        assertNotNull(storedLocation);
        assertEquals("repoAddress", storedLocation.getProperty("repRef"));
    }

    @Test
    public void testEndElement_LocationsTag() {
        try {
            LocationsReader underTest = new LocationsReader();

            // Should do nothing
            underTest.endElement(uri, localName, "Locations");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEndElement_LocTag() {
        try {
            LocationsReader underTest = new LocationsReader();

            // Should do nothing
            underTest.endElement(uri, localName, "Loc");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testEndElement_unsupportedTag() {
        try {
            LocationsReader underTest = new LocationsReader();

            underTest.endElement(uri, localName, "ILLEGAL_TAG");
        } catch (Exception e) {
            assertTrue(e instanceof SAXException);
            assertEquals("Tag with illegal name spotted:ILLEGAL_TAG", e.getMessage());
        }
    }
}