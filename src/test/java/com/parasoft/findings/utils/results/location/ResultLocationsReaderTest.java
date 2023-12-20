package com.parasoft.findings.utils.results.location;

import com.parasoft.findings.utils.results.testableinput.DefaultLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.FindingsLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.ITestableInput;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.results.violations.SourceRange;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResultLocationsReaderTest {
    @Test
    public void testConstructor_withNullMatcher() {
        ResultLocationsReader underTest = new ResultLocationsReader(null);

        ITestableInputLocationMatcher matcher = underTest.getLocationMatcher();
        assertNotNull(matcher);
        assertTrue(matcher instanceof DefaultLocationMatcher);
    }

    @Test
    public void testConstructor_withNotNullMatcher() {
        FindingsLocationMatcher mockedMatcher = mock(FindingsLocationMatcher.class);
        ResultLocationsReader underTest = new ResultLocationsReader(mockedMatcher);
        ITestableInputLocationMatcher matcher = underTest.getLocationMatcher();

        assertNotNull(matcher);
        assertEquals(mockedMatcher, matcher);
    }

    @Test
    public void testGetResultLocation() throws Throwable {
        ResultLocationsReader underTest = spy(new ResultLocationsReader(null));

        // Testable input not found
        doReturn(null).when(underTest).getTestableInput("locationRef", false);
        ResultLocation resultLocation1 = underTest.getResultLocation("locationRef", mock(SourceRange.class), false);
        assertNull(resultLocation1);

        // Testable input found
        doReturn(mock(ITestableInput.class)).when(underTest).getTestableInput("locationRef", false);
        ResultLocation resultLocation2 = underTest.getResultLocation("locationRef", mock(SourceRange.class), false);
        assertNotNull(resultLocation2);
    }

    @Test
    public void testGetTestableInput() throws Throwable {
        ITestableInputLocationMatcher mockedMatcher = mock(ITestableInputLocationMatcher.class);
        ResultLocationsReader underTest = spy(new ResultLocationsReader(mockedMatcher));

        // Location not found
        doReturn(null).when(underTest).getStoredLocation("locationRef");
        ITestableInput input1 = underTest.getTestableInput("locationRef", false);
        assertNull(input1);

        // Location found
        Properties properties = new Properties();
        doReturn(properties).when(underTest).getStoredLocation("locationRef");
        ITestableInput expectedInput = mock(ITestableInput.class);
        doReturn(expectedInput).when(mockedMatcher).matchLocation(properties, false);
        ITestableInput input2 = underTest.getTestableInput("locationRef", false);

        verify(mockedMatcher).matchLocation(properties, false);
        assertEquals(expectedInput, input2);
    }
}