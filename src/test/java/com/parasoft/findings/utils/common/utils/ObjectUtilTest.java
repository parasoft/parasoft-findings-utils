package com.parasoft.findings.utils.common.utils;

import com.parasoft.findings.utils.common.util.ObjectUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectUtilTest {
    @Test
    public void testEquals() {
        assertTrue(ObjectUtil.equals(null, null));
        assertTrue(ObjectUtil.equals("String", "String"));
        assertFalse(ObjectUtil.equals(null, "String"));
        assertFalse(ObjectUtil.equals("String", null));
        assertFalse(ObjectUtil.equals("String", "Void"));
    }

    @Test
    public void testhashCode() {
        Object object = new Object();
        assertEquals(object.hashCode(), ObjectUtil.hashCode(object));
        assertEquals(31, ObjectUtil.hashCode(null));
    }
}
