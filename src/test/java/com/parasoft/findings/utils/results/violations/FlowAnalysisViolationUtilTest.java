package com.parasoft.findings.utils.results.violations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlowAnalysisViolationUtilTest {
    private final FlowAnalysisViolation mockedFlowAnalysisViolation = mock(FlowAnalysisViolation.class);
    private final IFlowAnalysisPathElement mockedFlowAnalysisPathElement = mock(IFlowAnalysisPathElement.class);
    private final IFlowAnalysisPathElement[] flowAnalysisPathElements = {mockedFlowAnalysisPathElement};
    private final IFlowAnalysisPathElement mockedFlowAnalysisPathChildElement = mock(IFlowAnalysisPathElement.class);
    private final IFlowAnalysisPathElement[] flowAnalysisPathChildElements = {mockedFlowAnalysisPathChildElement};

    private final String testGetCauseMessage = "Test getCause message";
    private final String testGetPointMessage = "Test getPoint message";
    private List<PathElementAnnotation> pathElementAnnotations;

    @BeforeEach
    public void setUp() {
        pathElementAnnotations = new ArrayList<>();
        pathElementAnnotations.add(new PathElementAnnotation(testGetCauseMessage, "cause"));
        pathElementAnnotations.add(new PathElementAnnotation(testGetPointMessage, "point"));
        when(mockedFlowAnalysisViolation.getPathElements()).thenReturn(flowAnalysisPathElements);
    }

    @Test
    public void testGetCauseMessage_hasCauseMessage() {
        when(mockedFlowAnalysisPathElement.getAnnotations()).thenReturn(pathElementAnnotations);
        String result = FlowAnalysisViolationUtil.getCauseMessage(mockedFlowAnalysisViolation);
        assertEquals(testGetCauseMessage, result);
    }

    @Test
    public void testGetCauseMessage_hasCauseMessageInChildElements() {
        when(mockedFlowAnalysisPathElement.getAnnotations()).thenReturn(new ArrayList<>());
        when(mockedFlowAnalysisPathElement.getChildren()).thenReturn(flowAnalysisPathChildElements);
        when(mockedFlowAnalysisPathChildElement.getAnnotations()).thenReturn(pathElementAnnotations);

        String result = FlowAnalysisViolationUtil.getCauseMessage(mockedFlowAnalysisViolation);
        assertEquals(testGetCauseMessage, result);
    }

    @Test
    public void testGetCauseMessage_noMessages() {
        when(mockedFlowAnalysisPathElement.getAnnotations()).thenReturn(new ArrayList<>());
        String result = FlowAnalysisViolationUtil.getCauseMessage(mockedFlowAnalysisViolation);
        assertNull(result);
    }

    @Test
    public void testGetPointMessage_hasPointMessage() {
        when(mockedFlowAnalysisPathElement.getAnnotations()).thenReturn(pathElementAnnotations);
        String result = FlowAnalysisViolationUtil.getPointMessage(mockedFlowAnalysisViolation);
        assertEquals(testGetPointMessage, result);
    }

    @Test
    public void testGetPointMessage_hasPointMessageInChildElements() {
        when(mockedFlowAnalysisPathElement.getAnnotations()).thenReturn(new ArrayList<>());
        when(mockedFlowAnalysisPathElement.getChildren()).thenReturn(flowAnalysisPathChildElements);
        when(mockedFlowAnalysisPathChildElement.getAnnotations()).thenReturn(pathElementAnnotations);

        String result = FlowAnalysisViolationUtil.getPointMessage(mockedFlowAnalysisViolation);
        assertEquals(testGetPointMessage, result);
    }

    @Test
    public void testGetPointMessage_noMessages() {
        when(mockedFlowAnalysisPathElement.getAnnotations()).thenReturn(new ArrayList<>());
        String result = FlowAnalysisViolationUtil.getPointMessage(mockedFlowAnalysisViolation);
        assertNull(result);
    }
}