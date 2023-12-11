package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectUtilTest {
    @Test
    public void testEquals() {
        //Two Objects are both null
        assertTrue(ObjectUtil.equals(null, null));
        //Two Objects are equal
        assertTrue(ObjectUtil.equals("String", "String"));
        //One object is null, the other is not
        assertFalse(ObjectUtil.equals(null, "String"));
        //One object is null, the other is not
        assertFalse(ObjectUtil.equals("String", null));
        //Two Objects are not equal
        assertFalse(ObjectUtil.equals("String", "Void"));
    }

    @Test
    public void testhashCode() {
        Object object = new Object();
        //Object are not null
        assertEquals(object.hashCode(), ObjectUtil.hashCode(object));
        //Object are null
        assertEquals(31, ObjectUtil.hashCode(null));
    }
}
