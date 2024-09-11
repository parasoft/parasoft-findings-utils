package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FileTestableInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FlowAnalysisPathElementTest {

    private FlowAnalysisPathElement underTest;
    private ResultLocation locationForTest;
    private FlowAnalysisPathElement.TypeImpl typeImplForTest;
    IFlowAnalysisPathElement mockedFlowAnalysisPathElement = Mockito.mock(IFlowAnalysisPathElement.class);
    IFlowAnalysisPathElement[] iFlowAnalysisPathElementsForTest = {mockedFlowAnalysisPathElement};
    List<PathElementAnnotation> pathElementAnnotationsForTest = new ArrayList<>();

    @BeforeEach
    public void prepareTestVariables() {
        locationForTest = new ResultLocation(new FileTestableInput(new File("testFile")), new SourceRange(1,2,3,0));
        typeImplForTest = new FlowAnalysisPathElement.TypeImpl("typeForTest");
        pathElementAnnotationsForTest.add(new PathElementAnnotation("Loop condition evaluation: !exitGame (assuming true)", "condEval"));

        underTest = new FlowAnalysisPathElement("descriptionForTest", locationForTest,
                new HashMap<>(), iFlowAnalysisPathElementsForTest, typeImplForTest, "const char *", "assertion", pathElementAnnotationsForTest);
    }

    @Test
    public void testGetDescription() {
        assertEquals("descriptionForTest", underTest.getDescription());
    }

    @Test
    public void testGetLocation() {
        assertEquals(locationForTest, underTest.getLocation());
    }

    @Test
    public void testAddAttribute() {
        underTest.addAttribute("attributeForTest", "test");
        assertEquals("test", underTest.getAttribute("attributeForTest"));
    }

    @Test
    public void testGetChildren() {
        assertEquals(iFlowAnalysisPathElementsForTest, underTest.getChildren());
    }

    @Test
    public void testGetAnnotations() {
        assertEquals(pathElementAnnotationsForTest, underTest.getAnnotations());
    }

    @Test
    public void testGetThrownTypes() {
        assertEquals("const char *", underTest.getThrownTypes());
    }

    @Test
    public void testGetThrowingMethod() {
        assertEquals("assertion", underTest.getThrowingMethod());
    }

    @Test
    public void testGetType() {
        assertEquals(typeImplForTest, underTest.getType());
    }

    @Test
    public void testHashCode() {
        assertEquals(-731395433, underTest.hashCode());
    }

    @Test
    public void testEquals_withSameObject() {
        assertTrue(underTest.equals(underTest));
    }

    @Test
    public void testEquals_withFlowAnalysisPathElementObject() {
        FlowAnalysisPathElement newFlowAnalysisPathElement = new FlowAnalysisPathElement("descriptionForTest", locationForTest,
                new HashMap<>(), iFlowAnalysisPathElementsForTest, typeImplForTest, "const char *", "assertion", pathElementAnnotationsForTest);
        assertTrue(underTest.equals(newFlowAnalysisPathElement));
    }

    @Test
    public void testEquals_withOtherObject() {
        assertFalse(underTest.equals("testEqualsFunction"));
    }

    @Test
    public void testTypeImplHashCode() {
        assertEquals(48958561, underTest.getType().hashCode());
    }

    @Test
    public void testTypeImplEquals_withOtherObject() {
        assertFalse(underTest.getType().equals("testEqualsFunction"));
    }

    @Test
    public void testTypeImplEquals_withTypeObject() {
        FlowAnalysisPathElement.TypeImpl differentType = new FlowAnalysisPathElement.TypeImpl("differentType");

        assertTrue(underTest.getType().equals(typeImplForTest));
        assertFalse(underTest.getType().equals(differentType));
    }
}