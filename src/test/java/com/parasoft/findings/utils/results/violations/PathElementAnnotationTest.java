package com.parasoft.findings.utils.results.violations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathElementAnnotationTest {

    PathElementAnnotation pathElementAnnotation = new PathElementAnnotation("sMessage", "sKind");

    @Test
    public void testGetMessage() {
        assertEquals("sMessage", pathElementAnnotation.getMessage());
    }

    @Test
    public void testGetKind() {
        assertEquals("sKind", pathElementAnnotation.getKind());
    }
}
