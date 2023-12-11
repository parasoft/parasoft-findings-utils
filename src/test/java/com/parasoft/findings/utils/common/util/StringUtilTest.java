package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {
    @Test
    public void testGetLineSeparator() {
        assertEquals("\r\n", StringUtil.getLineSeparator());
    }

    @Test
    public void testIsAnyEmptyTrimmed() {
        //No given strings is null or trimmed value is zero-length
        assertFalse(StringUtil.isAnyEmptyTrimmed("String", "String"));
        //Given strings has null
        assertTrue(StringUtil.isAnyEmptyTrimmed(null, "String"));
        //Given strings has trimmed value is zero-length
        assertTrue(StringUtil.isAnyEmptyTrimmed("String", "  "));
    }

    @Test
    public void testIsEmpty() {
        //Given string is null
        assertTrue(StringUtil.isEmpty(null));
        //Given string is zero-length
        assertTrue(StringUtil.isEmpty(""));
        //Given string is not null or zero-length
        assertFalse(StringUtil.isEmpty("String"));
    }

    @Test
    public void testIsNonEmpty() {
        //Given strings is not null or zero-length
        assertTrue(StringUtil.isNonEmpty("String"));
        //Given strings is null
        assertFalse(StringUtil.isNonEmpty(null));
        //Given strings is zero-length
        assertFalse(StringUtil.isNonEmpty(""));
    }

    @Test
    public void testIsNonEmptyTrimmed() {
        //Given strings is not-null and trimmed value is not zero-length
        assertTrue(StringUtil.isNonEmptyTrimmed("String "));
        //Given strings is null
        assertFalse(StringUtil.isNonEmptyTrimmed(null));
        //Given strings trimmed value is zero-length
        assertFalse(StringUtil.isNonEmptyTrimmed(" "));
    }

    @Test
    public void testEquals() {
        //Two strings are both null
        assertTrue(StringUtil.equals(null, null));
        //Two strings are equal
        assertTrue(StringUtil.equals("String", "String"));
        //One string is null, the other is not
        assertFalse(StringUtil.equals(null, "String"));
        //One string is null, the other is not
        assertFalse(StringUtil.equals("String", null));
        //Two strings are not equal
        assertFalse(StringUtil.equals("String", "Void"));
    }

    @Test
    public void testGetNonEmpty() {
        //Given string is not null
        assertEquals("String", StringUtil.getNonEmpty("String"));
        //Given string is null
        assertEquals("", StringUtil.getNonEmpty(null));
    }
}
