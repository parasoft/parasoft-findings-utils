package com.parasoft.findings.utils.common.nls;

import com.parasoft.findings.utils.common.IStringConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.text.MessageFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NLSTest {

    @BeforeEach
    public void setUp() {
        Locale englishLocale = new Locale("en", "US");
        Locale.setDefault(englishLocale);
        Messages.initMessages(Messages.class);
    }

    @Test
    public void testGetEnglishString() {
        String value = Messages.TEST;
        assertEquals("test", value);
    }

    /**
     * Test {@link NLS#bind(String, Object)}
     */
    @Test
    public void testBind_1() {
        assertEquals("No message available.", NLS.bind(null, "variable_1"));

        assertEquals("'variable_1'", NLS.bind(Messages.TEST_ONE_VARIABLE_WITH_QUOTES, "variable_1"));

        assertEquals("{0}'", NLS.bind(Messages.TEST_ONE_VARIABLE_WITH_QUOTES_INVALID_1, "variable_1"));

        assertThrows(IllegalArgumentException.class, () -> NLS.bind(Messages.TEST_ONE_VARIABLE_WITH_QUOTES_INVALID_2, "variable_1"));

        assertEquals("NLS missing message: MESSAGE_MISSING in: " + MessageResourceBundle.getDefaultBundleName(Messages.class), NLS.bind(Messages.MESSAGE_MISSING, "variable_1"));
    }

    /**
     * Test {@link NLS#bind(String, Object, Object)}
     */
    @Test
    public void testBind_2() {
        assertEquals("No message available.", NLS.bind(null, "variable_1", "variable_2"));

        assertEquals("variable_1 variable_2", NLS.bind(Messages.TEST_TWO_VARIABLE, "variable_1", "variable_2"));

        assertEquals("variable_2 variable_1", NLS.bind(Messages.TEST_TWO_VARIABLE_REVERSE, "variable_1", "variable_2"));
    }

    @Test
    public void testGetFormatted() {
        assertEquals(IStringConstants.EMPTY, NLS.getFormatted(null));

        assertEquals("{0}", NLS.getFormatted(Messages.TEST_ONE_VARIABLE));

        assertEquals(IStringConstants.EMPTY, NLS.getFormatted(Messages.TEST_ONE_VARIABLE, null));

        assertEquals(IStringConstants.EMPTY, NLS.getFormatted(Messages.TEST_ONE_VARIABLE_INVALID, "variable_1"));

        MockedConstruction<MessageFormat> mocked = mockConstruction(MessageFormat.class);
        MessageFormat messageFormat = new MessageFormat(Messages.TEST_ONE_VARIABLE);
        when(messageFormat.format("variable_1")).thenReturn(null);
        assertEquals(IStringConstants.EMPTY, NLS.getFormatted(Messages.TEST_ONE_VARIABLE, "variable_1"));
        mocked.close();

        assertEquals("variable_1", NLS.getFormatted(Messages.TEST_ONE_VARIABLE, "variable_1"));
    }
}