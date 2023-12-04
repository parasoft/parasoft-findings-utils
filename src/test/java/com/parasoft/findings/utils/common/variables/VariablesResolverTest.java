package com.parasoft.findings.utils.common.variables;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VariablesResolverTest {
    VariablesResolver _variablesResolver = new VariablesResolver(new IVariablesProvider() {
        private final Map<String, String> _variables = new HashMap<String, String>();
        {
            _variables.put("FAKE_C_DRIVER", "C:");
            _variables.put("FAKE_JAVA_INSTALLATION", "Program Files/Java");
            _variables.put("FAKE_JAVA_BIN", "jdk1.8.0_301/bin");
        }
        @Override
        public StaticVariable getVariable(String sName) {
            String sValue = _variables.get(sName);
            return sValue == null ? null : new StaticVariable(sName, sValue);
        }
    });

    @Test
    public void testPerformSubstitution() {
        String value = _variablesResolver.performSubstitution("${FAKE_C_DRIVER}/${FAKE_JAVA_INSTALLATION}/${FAKE_JAVA_BIN}");
        assertEquals("C:/Program Files/Java/jdk1.8.0_301/bin", value);
    }

    @Test
    public void testPerformSubstitution_noVariable() {
        String value = _variablesResolver.performSubstitution("${VARIABLE_NOT_EXISTING}");
        assertEquals("${VARIABLE_NOT_EXISTING}", value);
    }
}