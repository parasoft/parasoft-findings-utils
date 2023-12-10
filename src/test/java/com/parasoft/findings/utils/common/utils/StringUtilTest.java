package com.parasoft.findings.utils.common.utils;

import com.parasoft.findings.utils.common.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {
    @Test
    public void testGetLineSeparator() {
        assertEquals("\r\n", StringUtil.getLineSeparator());
    }

    @Test
    public void testIsAnyEmptyTrimmed() {
        assertFalse(StringUtil.isAnyEmptyTrimmed("String", "String"));
        assertTrue(StringUtil.isAnyEmptyTrimmed(null, "String"));
        assertTrue(StringUtil.isAnyEmptyTrimmed("String", "  "));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("String"));
    }

    @Test
    public void testIsNonEmpty() {
        assertTrue(StringUtil.isNonEmpty("String"));
        assertFalse(StringUtil.isNonEmpty(null));
        assertFalse(StringUtil.isNonEmpty(""));
    }

    @Test
    public void testIsNonEmptyTrimmed() {
        assertTrue(StringUtil.isNonEmptyTrimmed("String "));
        assertFalse(StringUtil.isNonEmptyTrimmed(null));
        assertFalse(StringUtil.isNonEmptyTrimmed(" "));
    }

    @Test
    public void testEquals() {
        assertTrue(StringUtil.equals(null, null));
        assertTrue(StringUtil.equals("String", "String"));
        assertFalse(StringUtil.equals(null, "String"));
        assertFalse(StringUtil.equals("String", null));
        assertFalse(StringUtil.equals("String", "Void"));
    }

    @Test
    public void testGetNonEmpty() {
        assertEquals("String", StringUtil.getNonEmpty("String"));
        assertEquals("", StringUtil.getNonEmpty(null));
    }
}
