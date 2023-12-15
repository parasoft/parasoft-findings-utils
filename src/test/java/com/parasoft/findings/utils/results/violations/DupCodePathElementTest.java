package com.parasoft.findings.utils.results.violations;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class DupCodePathElementTest {

    ResultLocation location = Mockito.mock(ResultLocation.class);
    DupCodePathElement dupCodePathElement = new DupCodePathElement("_description", location);

    @Test
    public void testGetDescription() {
        assertEquals("_description", dupCodePathElement.getDescription());
    }

    @Test
    public void testGetLocation() {
        assertEquals(location, dupCodePathElement.getLocation());
    }

    @Test
    public void testGetChildren() {
        assertEquals(0, dupCodePathElement.getChildren().length);
    }

    @Test
    public void testGetAttribute() {
        dupCodePathElement.addAttribute("sName", "sValue");

        assertEquals("sValue", dupCodePathElement.getAttribute("sName"));
    }

    @Test
    public void testEquals() {
        DupCodePathElement equalObject = new DupCodePathElement("_description", location);
        DupCodePathElement unequalDescription = new DupCodePathElement("aaaaa", location);
        DupCodePathElement unequalLocation = new DupCodePathElement("_description", null);
        //When given the ob
        assertTrue(dupCodePathElement.equals(dupCodePathElement));
        //When given object is not instance of DupCodePathElement
        assertFalse(dupCodePathElement.equals(""));
        //When two "_description" are not equal
        assertFalse(dupCodePathElement.equals(unequalDescription));
        //When two "location" are not equal
        assertFalse(dupCodePathElement.equals(unequalLocation));
        //When two objects are equals
        assertTrue(dupCodePathElement.equals(equalObject));
    }
}
