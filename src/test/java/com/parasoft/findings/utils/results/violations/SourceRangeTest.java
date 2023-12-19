package com.parasoft.findings.utils.results.violations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SourceRangeTest {
    @Test
    public void testConstructorAndGetters() {
        int startLine = 5;
        int startLineOffset = 10;
        int endLine = 7;
        int endLineOffset = 20;

        SourceRange sourceRange = new SourceRange(
                startLine, startLineOffset, endLine, endLineOffset);

        assertEquals(startLine, sourceRange.getStartLine());
        assertEquals(startLineOffset, sourceRange.getStartLineOffset());
        assertEquals(endLine, sourceRange.getEndLine());
        assertEquals(endLineOffset, sourceRange.getEndLineOffset());
    }

    @Test
    public void testHashCode() {
        SourceRange srcRange1 = new SourceRange(2, 3, 4, 5);
        SourceRange srcRange_sameAsSrcRange1 = new SourceRange(2, 3, 4, 5);

        assertEquals(srcRange1.hashCode(), srcRange_sameAsSrcRange1.hashCode());
    }

    @Test
    public void testEquals() {
        SourceRange srcRange1 = new SourceRange(2, 3, 4, 5);
        SourceRange srcRange_sameAsSrcRange1 = new SourceRange(2, 3, 4, 5);
        SourceRange srcRange_diffStartLineWithSrcRange1 = new SourceRange(1, 3, 4, 5);
        SourceRange srcRange_diffStartLineOffsetWithSrcRange1 = new SourceRange(1, 2, 4, 5);
        SourceRange srcRange_diffEndLineWithSrcRange1 = new SourceRange(1, 3, 3, 5);
        SourceRange srcRange_diffEndLineOffsetWithSrcRange1 = new SourceRange(1, 3, 4, 5);

        assertTrue(srcRange1.equals(srcRange1)); // Same object
        assertTrue(srcRange1.equals(srcRange_sameAsSrcRange1)); // Same properties
        assertFalse(srcRange1.equals("")); // Different Type
        assertFalse(srcRange1.equals(srcRange_diffStartLineWithSrcRange1)); // Different startLine
        assertFalse(srcRange1.equals(srcRange_diffStartLineOffsetWithSrcRange1)); // Different startLineOffset
        assertFalse(srcRange1.equals(srcRange_diffEndLineWithSrcRange1)); // Different endLine
        assertFalse(srcRange1.equals(srcRange_diffEndLineOffsetWithSrcRange1)); // Different endLineOffset
    }

    @Test
    public void testToString() {
        SourceRange sourceRange = new SourceRange(3, 2, 5, 7);

        String expectedString = "[3,2-5,7]";
        assertEquals(expectedString, sourceRange.toString());
    }
}