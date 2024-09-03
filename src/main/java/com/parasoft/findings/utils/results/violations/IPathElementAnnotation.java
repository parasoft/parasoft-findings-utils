package com.parasoft.findings.utils.results.violations;
public interface IPathElementAnnotation {
    /**
     * Returns annotation message (human-readable, translated if needed)
     * @return annotation message
     */
    String getMessage();

    /**
     * Returns annotation kind (not necessarily human-readable)
     * @return annotation kind
     */
    String getKind();
}
