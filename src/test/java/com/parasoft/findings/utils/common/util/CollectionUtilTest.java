package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionUtilTest {

    List<String> emptyList, testList;

    @BeforeEach
    public void initArrayLists() {
        emptyList = new ArrayList<>();
        testList = new ArrayList<>();
        testList.add("test");
    }

    @Test
    public void testIsNonEmpty_normal() {
        boolean result = CollectionUtil.isNonEmpty(testList);
        assertTrue(result);
    }

    @Test
    public void testIsNonEmpty_emptyList() {
        boolean result = CollectionUtil.isNonEmpty(emptyList);
        assertFalse(result);
    }

    @Test
    public void testHasAtLeastNElements_normal() {
        boolean result = CollectionUtil.hasAtLeastNElements(testList, 1);
        assertTrue(result);
    }

    @Test
    public void testHasAtLeastNElements_null() {
        boolean result = CollectionUtil.hasAtLeastNElements(null, 1);
        assertFalse(result);
    }

    @Test
    public void testHasAtLeastNElements_emptyList() {
        boolean result = CollectionUtil.hasAtLeastNElements(emptyList, 1);
        assertFalse(result);
    }
}
