package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathUtilTest {

    String testedPath = "D:/Software\\Java";

    /**
     * Test {@link PathUtil#splitPath(String)}
     */
    @Test
    public void testSplitPath_1() {
        String[] splitPath = PathUtil.splitPath(testedPath);
        assertEquals(3, splitPath.length);
    }

    /**
     * Test {@link PathUtil#splitPath(String, String)}
     */
    @Test
    public void testSplitPath_2() {
        String[] splitPath = PathUtil.splitPath(testedPath, ":");
        assertEquals(2, splitPath.length);
    }

    /**
     * Test {@link PathUtil#splitPath(String, String)}
     */
    @Test
    public void testSplitPath_nullSeparators() {
        try {
            PathUtil.splitPath(testedPath, null);
        } catch (IllegalArgumentException ie) {
            assertEquals("Missing separator.", ie.getMessage());
        }
    }

    /**
     * Test {@link PathUtil#splitPath(String, String)}
     */
    @Test
    public void testSplitPath_emptySeparators() {
        try {
            PathUtil.splitPath(testedPath, "");
        } catch (IllegalArgumentException ie) {
            assertEquals("Missing separator.", ie.getMessage());
        }
    }

    /**
     * Test {@link PathUtil#splitPath(String, String)}
     */
    @Test
    public void testSplitPath_nullPath() {
        // If path is null
        String[] splitPath = PathUtil.splitPath(null, ":");
        assertNull(splitPath);
    }

    @Test
    public void testNormalizePath_normal() {
        String path = PathUtil.normalizePath(testedPath + "//bin");
        assertEquals("D:/Software/Java/bin", path);
    }

    @Test
    public void testNormalizePath_nullPath() {
        String path = PathUtil.normalizePath(null);
        assertNull(path);
    }
}
