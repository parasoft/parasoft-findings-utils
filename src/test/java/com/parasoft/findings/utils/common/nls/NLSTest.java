package com.parasoft.findings.utils.common.nls;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class NLSTest {

    @Test
    public void testGetEnglishString() {
        Locale englishLocale = new Locale("en", "US");
        Locale.setDefault(englishLocale);
        Messages.initMessages(Messages.class);

        String value = Messages.TEST;
        assertEquals("test", value);
    }
}