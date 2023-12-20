package com.parasoft.findings.utils.results.violations;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class DupCodePathElementTest {

    ResultLocation mockedResultLocation = Mockito.mock(ResultLocation.class);
    DupCodePathElement underTest = new DupCodePathElement("[Line 73] Duplicated function in file 'DeadLock.cpp'", mockedResultLocation);

    @Test
    public void testGetDescription() {
        assertEquals("[Line 73] Duplicated function in file 'DeadLock.cpp'", underTest.getDescription());
    }

    @Test
    public void testGetLocation() {
        assertEquals(mockedResultLocation, underTest.getLocation());
    }

    @Test
    public void testGetChildren() {
        assertEquals(0, underTest.getChildren().length);
    }

    @Test
    public void testGetAttribute() {
        underTest.addAttribute("locHash", "972801700");
        assertEquals("972801700", underTest.getAttribute("locHash"));
    }

    @Test
    public void testEquals() {
        DupCodePathElement equalObject = new DupCodePathElement("[Line 73] Duplicated function in file 'DeadLock.cpp'", mockedResultLocation);
        DupCodePathElement unequalDescription = new DupCodePathElement("[Line 13] Duplicated code in file 'NullPointer.cpp'", mockedResultLocation);
        DupCodePathElement unequalLocation = new DupCodePathElement("[Line 73] Duplicated function in file 'DeadLock.cpp'", null);
        //When given the same object
        assertTrue(underTest.equals(underTest));
        //When given object is not instance of DupCodePathElement
        assertFalse(underTest.equals(""));
        //When two "_description" are not equal
        assertFalse(underTest.equals(unequalDescription));
        //When two "location" are not equal
        assertFalse(underTest.equals(unequalLocation));
        //When two objects are equals
        assertTrue(underTest.equals(equalObject));
    }

    @Test
    public void testHashCode() {
        assertEquals(underTest.getLocation().hashCode() * 31 + underTest.getDescription().hashCode(), underTest.hashCode());
    }
}
