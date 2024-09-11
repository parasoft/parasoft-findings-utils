package com.parasoft.findings.utils.results.violations;

import java.util.List;

public class FlowAnalysisViolationUtil {
    public static String getCauseMessage(IFlowAnalysisViolation flowAnalysisViolation)
    {
        String sCauseMsg = null;
        IFlowAnalysisPathElement[] iFlowAnalysisPathElements = flowAnalysisViolation.getPathElements();
        if (iFlowAnalysisPathElements != null) {
            for (IFlowAnalysisPathElement element : iFlowAnalysisPathElements) {
                sCauseMsg = getMessage(element, "cause"); // $NON-NLS-1$
                if (sCauseMsg != null) {
                    break;
                }
            }
        }
        return sCauseMsg;
    }

    public static String getPointMessage(IFlowAnalysisViolation flowAnalysisViolation)
    {
        String sPointMsg = null;
        IFlowAnalysisPathElement[] iFlowAnalysisPathElements = flowAnalysisViolation.getPathElements();
        if (iFlowAnalysisPathElements != null) {
            for (IFlowAnalysisPathElement element : iFlowAnalysisPathElements) {
                sPointMsg = getMessage(element, "point"); // $NON-NLS-1$
                if (sPointMsg != null) {
                    break;
                }
            }
        }
        return sPointMsg;
    }

    private static String getMessage(IFlowAnalysisPathElement element, String kind)
    {
        List<PathElementAnnotation> annotations = element.getAnnotations();
        if (annotations != null) {
            for (PathElementAnnotation annotation : annotations) {
                if (kind.equals(annotation.getKind())) {
                    return annotation.getMessage();
                }
            }
        }
        IFlowAnalysisPathElement[] children = element.getChildren();
        if (children != null) {
            for (IFlowAnalysisPathElement child : children) {
                String msg = getMessage(child, kind);
                if (msg != null) {
                    return msg;
                }
            }
        }
        return null;
    }
}