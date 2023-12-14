package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class IntegerUtilTest {

    @Test
    public void testParseInt_normal() {
        int result = IntegerUtil.parseInt("1", 2);
        assertEquals(1, result);
    }

    @Test
    public void testParseInt_useDefaultValue() {
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);
            int result = IntegerUtil.parseInt("test", 2);
            verify(logger, times(1)).info("Could not parse int value from: test");
            assertEquals(2, result);
        }
    }

    @Test
    public void testCompare_smaller() {
        // When first number is smaller than the second number
        int result = IntegerUtil.compare(1, 2);
        assertEquals(-1, result);
    }

    @Test
    public void testCompare_equal() {
        // When first number is equal to the second number
        int result = IntegerUtil.compare(1, 1);
        assertEquals(0, result);
    }

    @Test
    public void testCompare_greater() {
        // When first number is larger than the second number
        int result = IntegerUtil.compare(2, 1);
        assertEquals(1, result);
    }
}
