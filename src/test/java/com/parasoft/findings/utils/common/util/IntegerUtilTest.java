package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegerUtilTest {

    @Test
    public void testParseInt_normal() {
        int result = IntegerUtil.parseInt("1", 2);
        assertEquals(1, result);
    }

    @Test
    public void testParseInt_useDefaultValue() {
        int result = IntegerUtil.parseInt("test", 2);
        assertEquals(2, result);
    }

    @Test
    public void testCompare_LessResult() {
        // When first number is smaller than the second number
        int result = IntegerUtil.compare(1, 2);
        assertEquals(-1, result);
    }

    @Test
    public void testCompare_equalResult() {
        // When first number is equal to the second number
        int result = IntegerUtil.compare(1, 1);
        assertEquals(0, result);
    }

    @Test
    public void testCompare_largerResult() {
        // When first number is larger than the second number
        int result = IntegerUtil.compare(2, 1);
        assertEquals(1, result);
    }
}
