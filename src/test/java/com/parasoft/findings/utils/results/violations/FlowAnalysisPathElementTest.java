package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FileTestableInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class FlowAnalysisPathElementTest {

    private FlowAnalysisPathElement flowAnalysisPathElement;
    private ResultLocation locationForTest;
    private FlowAnalysisPathElement.TypeImpl typeImplForTest;

    @BeforeEach
    public void prepareTestVariables() {
        locationForTest = new ResultLocation(new FileTestableInput(new File("testFile")), new SourceRange(1,2,3,0));
        typeImplForTest = new FlowAnalysisPathElement.TypeImpl("typeForTest");

        flowAnalysisPathElement = new FlowAnalysisPathElement("descriptionForTest", locationForTest,
                new HashMap<>(), null, typeImplForTest, null, null, null, null);
    }

    @Test
    public void testGetDescription() {
        assertEquals("descriptionForTest", flowAnalysisPathElement.getDescription());
    }

    @Test
    public void testGetLocation() {
        assertEquals(locationForTest, flowAnalysisPathElement.getLocation());
    }

    @Test
    public void testAddAttribute() {
        flowAnalysisPathElement.addAttribute("attributeForTest", "test");
        assertEquals("test", flowAnalysisPathElement.getAttribute("attributeForTest"));
    }

    @Test
    public void testGetChildren() {
        assertEquals(null, flowAnalysisPathElement.getChildren());
    }

    @Test
    public void testGetProperties() {
        assertEquals(new Properties(), flowAnalysisPathElement.getProperties());
    }

    @Test
    public void testGetAnnotations() {
        assertEquals(new ArrayList<>(), flowAnalysisPathElement.getAnnotations());
    }

    @Test
    public void testGetThrownTypes() {
        assertEquals(null, flowAnalysisPathElement.getThrownTypes());
    }

    @Test
    public void testGetThrowingMethod() {
        assertEquals(null, flowAnalysisPathElement.getThrowingMethod());
    }

    @Test
    public void testGetType() {
        assertEquals(typeImplForTest, flowAnalysisPathElement.getType());
    }

    @Test
    public void testGetHashCode() {
        assertEquals(-731395433, flowAnalysisPathElement.hashCode());
    }

    @Test
    public void testEquals_withSameObject() {
        assertTrue(flowAnalysisPathElement.equals(flowAnalysisPathElement));
    }

    @Test
    public void testEquals_withFlowAnalysisPathElementObject() {
        FlowAnalysisPathElement newFlowAnalysisPathElement = new FlowAnalysisPathElement("descriptionForTest", locationForTest,
                new HashMap<>(), null, typeImplForTest, null, null, null, null);
        assertTrue(flowAnalysisPathElement.equals(newFlowAnalysisPathElement));
    }

    @Test
    public void testEquals_withOtherObject() {
        assertFalse(flowAnalysisPathElement.equals("testEqualsFunction"));
    }

    @Test
    public void testTypeImplGetHashCode() {
        assertEquals(48958561, flowAnalysisPathElement.getType().hashCode());
    }

    @Test
    public void testTypeImplEquals_withOtherObject() {
        assertFalse(flowAnalysisPathElement.getType().equals("testEqualsFunction"));
    }
}
