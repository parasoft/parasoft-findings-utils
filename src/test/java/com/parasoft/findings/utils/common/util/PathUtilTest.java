package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathUtilTest {

    String testPath = "D:\\Software\\Java";

    @Test
    public void testSplitPath_UnixOrWin() {
        String[] splitPath = PathUtil.splitPath(testPath);
        assertEquals(3, splitPath.length);
    }

    @Test
    public void testSplitPath_normalSeparators() {
        String[] splitPath = PathUtil.splitPath(testPath, ":");
        assertEquals(2, splitPath.length);
    }

    @Test
    public void testSplitPath_nullSeparators() {
        try {
            PathUtil.splitPath(testPath, null);
        } catch (IllegalArgumentException ie) {
            assertEquals("Missing separator.", ie.getMessage());
        }
    }

    @Test
    public void testSplitPath_nullPath() {
        // If path is null
        String[] splitPath = PathUtil.splitPath(null, ":");
        assertNull(splitPath);
    }

    @Test
    public void testNormalizePath_normal() {
        String path = PathUtil.normalizePath(testPath);
        assertEquals("D:/Software/Java", path);
    }

    @Test
    public void testNormalizePath_nullPath() {
        String path = PathUtil.normalizePath(null);
        assertNull(path);
    }
}
