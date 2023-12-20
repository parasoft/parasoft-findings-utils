package com.parasoft.findings.utils.results.violations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathElementAnnotationTest {

    PathElementAnnotation underTest = new PathElementAnnotation("Point where value is declared", "point");

    @Test
    public void testGetMessage() {
        assertEquals("Point where value is declared", underTest.getMessage());
    }

    @Test
    public void testGetKind() {
        assertEquals("point", underTest.getKind());
    }
}
