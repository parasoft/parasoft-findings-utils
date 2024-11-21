package com.parasoft.findings.utils.common.variables;

import java.util.*;

import com.parasoft.findings.utils.common.IStringConstants;

/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/**
 * Engine that handles variable substitutions.
 */
public class StringSubstitutionEngine {
    /**
     * Resulting string
     */
    private StringBuffer _sbResultBuffer = null;

    /**
     * Whether substitutions were performed
     */
    private boolean _bSubstitutions = false;

    /**
     * Stack of variables to resolve
     */
    private LinkedList<VariableReference> _variablesStack = null;

    /**
     * provider of variables
     */
    private IVariablesProvider _variableProvider = null;

    /**
     * Constructor
     *
     * @param variableProvider provider of variables
     * @post _variableProvider != null
     */
    public StringSubstitutionEngine(IVariablesProvider variableProvider) {
        _variableProvider = variableProvider;

    }

    /**
     * Performs recursive string substitution and returns the resulting string.
     *
     * @param sExpression               expression to resolve
     * @param bReportUndefinedVariables whether to report undefined variables as an error
     * @param bResolveVariables         should the variables be resolved
     * @return the resulting string with all variables recursively substituted
     * @throws IllegalArgumentException when conflicting sets appear
     */
    public String performStringSubstitution(String sExpression,
                                            boolean bReportUndefinedVariables, boolean bResolveVariables) {
        if (sExpression == null) {
            return null;
        }
        substitute(sExpression, bReportUndefinedVariables, bResolveVariables);
        List<Set<String>> resolvedVariableSets = new ArrayList<Set<String>>();
        while (_bSubstitutions) {
            Set<String> resolved = substitute(_sbResultBuffer.toString(), bReportUndefinedVariables, true);

            for (int i = resolvedVariableSets.size() - 1; i >= 0; i--) {
                Set<String> prevSet = resolvedVariableSets.get(i);

                if (prevSet.equals(resolved)) {
                    raiseConflictError(i, resolvedVariableSets);
                }
            }

            resolvedVariableSets.add(resolved);
        }
        return _sbResultBuffer.toString();

    }

    /**
     * Error handler for performStringSubstitution routine
     *
     * @param firstResolved        - first index where sets are resolved
     * @param resolvedVariableSets - list of sets
     * @throws IllegalArgumentException always
     */
    private static void raiseConflictError(int firstResolved, List<Set<String>> resolvedVariableSets) {
        Set<String> conflictingSet = new HashSet<String>();

        int resolvedVariableSetsSize = resolvedVariableSets.size();
        while (firstResolved < resolvedVariableSetsSize) {
            conflictingSet.addAll(resolvedVariableSets.get(firstResolved));
            firstResolved++;
        }

        StringBuffer sbProblemVariableList = new StringBuffer();
        for (String string : conflictingSet) {
            sbProblemVariableList.append(string);
            sbProblemVariableList.append(", "); //$NON-NLS-1$
        }
        sbProblemVariableList.setLength(sbProblemVariableList.length() - 2); //truncate the last ", "
        throw new IllegalArgumentException();
    }

    /**
     * Makes a substitution pass of the given expression returns a Set of the variables that
     * were resolved in this pass
     *
     * @param sExpression               source expression
     * @param bReportUndefinedVariables whether to report undefined variables as an error
     * @param bResolveVariables         whether to resolve the value of any variables
     * @return a Set of the variables that were resolved in this pass
     */
    private Set<String> substitute(final String sExpression,
                                   boolean bReportUndefinedVariables, boolean bResolveVariables) {
        final int exprLength = sExpression.length();
        _sbResultBuffer = new StringBuffer(exprLength);
        _variablesStack = new LinkedList<VariableReference>();
        _bSubstitutions = false;

        Set<String> resolvedVariables = new HashSet<String>();

        int pos = 0;
        int state = SCAN_FOR_START;
        while (pos < exprLength) {
            switch (state) {
                case SCAN_FOR_START:
                    int start = sExpression.indexOf(VARIABLE_START, pos);
                    if (start >= 0) {
                        int length = start - pos;
                        // copy non-variable text to the result
                        if (length > 0) {
                            _sbResultBuffer.append(sExpression, pos, start);
                        }
                        pos = start + 2;
                        state = SCAN_FOR_END;

                        _variablesStack.addLast(new VariableReference());
                    } else {
                        // done - no more variables
                        _sbResultBuffer.append(sExpression.substring(pos));
                        pos = exprLength;
                    }
                    break;
                case SCAN_FOR_END:
                    // be careful of nested variables
                    start = sExpression.indexOf(VARIABLE_START, pos);
                    int end = sExpression.indexOf(VARIABLE_END, pos);
                    if (end < 0) {
                        // variables are not completed
                        VariableReference tos = _variablesStack.getLast();
                        tos.append(sExpression.substring(pos));
                        pos = exprLength;
                    } else {
                        if ((start >= 0) && (start < end)) {
                            // start of a nested variable
                            int length = start - pos;
                            if (length > 0) {
                                VariableReference tos = _variablesStack.getLast();
                                tos.append(sExpression.substring(pos, start));
                            }
                            pos = start + 2;
                            _variablesStack.addLast(new VariableReference());
                        } else {
                            // end of variable reference
                            VariableReference tos = _variablesStack.removeLast();
                            String sSubstring = sExpression.substring(pos, end);
                            tos.append(sSubstring);
                            resolvedVariables.add(sSubstring);

                            pos = end + 1;
                            String sValue = resolve(tos, bReportUndefinedVariables, bResolveVariables);
                            if (_variablesStack.isEmpty()) {
                                // append to result
                                _sbResultBuffer.append(sValue);
                                state = SCAN_FOR_START;
                            } else {
                                // append to previous variable
                                tos = _variablesStack.getLast();
                                tos.append(sValue);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        // process incomplete variable references
        while (!_variablesStack.isEmpty()) {
            VariableReference tos = _variablesStack.removeLast();
            if (_variablesStack.isEmpty()) {
                _sbResultBuffer.append(VARIABLE_START);
                _sbResultBuffer.append(tos.getText());
            } else {
                VariableReference var = _variablesStack.getLast();
                var.append(VARIABLE_START);
                var.append(tos.getText());
            }
        }
        return resolvedVariables;

    }

    /**
     * Resolve and return the value of the given variable reference, possibly <code>null</code>.
     *
     * @param var                       VariableReference
     * @param bReportUndefinedVariables whether to report undefined variables as an error
     * @param bResolveVariables         whether to resolve the variables value or just to validate that this variable is valid
     * @return variable value, possibly <code>null</code>
     */
    protected String resolve(VariableReference var,
                             boolean bReportUndefinedVariables, boolean bResolveVariables) {
        String sText = var.getText();
        int pos = sText.indexOf(VARIABLE_ARG);
        String sName = null;
        String sArg = null;
        if (pos > 0) {
            sName = sText.substring(0, pos);
            pos++;
            int textLength = sText.length();
            if (pos < textLength) {
                sArg = sText.substring(pos);
            } else if (pos == textLength) {
                sArg = IStringConstants.EMPTY;//empty argument value
            }
        } else {
            sName = sText;
        }
        StaticVariable valueVariable = null;
        StaticVariable variable = _variableProvider.getVariable(sName);
        if ((sArg == null) && (variable instanceof StaticVariable)) {
            //don't resolve statics if there is an argument, PR 100191
            valueVariable = variable;
        }
        if (valueVariable == null) {
            // no variables with the given name
            if (bReportUndefinedVariables) {
                throw new IllegalArgumentException();
            }
            // leave as is
            return getOriginalVarText(var);
        }

        if (sArg == null) {
            if (bResolveVariables) {
                _bSubstitutions = true;
                return valueVariable.getValue();
            }
            //leave as is
            return getOriginalVarText(var);
        }
        // error - an argument specified for a value variable
        throw new IllegalArgumentException();

    }


    /**
     * Returns original variable text
     *
     * @param var VariableReference
     * @return original variable text
     */
    private static String getOriginalVarText(VariableReference var) {
        StringBuffer res = new StringBuffer(var.getText());
        res.insert(0, VARIABLE_START);
        res.append(VARIABLE_END);
        return res.toString();

    }

    /**
     * Reference to a variable
     */
    public static class VariableReference {
        // the text inside the variable reference
        private final StringBuffer _sbText;

        public VariableReference() {
            _sbText = new StringBuffer();

        } // VariableReference()

        public void append(String sText) {
            _sbText.append(sText);

        } // append(String sText)

        public String getText() {
            return _sbText.toString();

        } // String getText()

    }

    // delimiters
    private static final String VARIABLE_START = "${"; //$NON-NLS-1$
    private static final char VARIABLE_END = '}';
    private static final char VARIABLE_ARG = ':';

    // parsing states
    private static final int SCAN_FOR_START = 0;
    private static final int SCAN_FOR_END = 1;

}
