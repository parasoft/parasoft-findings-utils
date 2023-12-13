package com.parasoft.findings.utils.common.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultLoggingHandlerFactoryTest {
    @Test
    public void testGetHandler_withHandlerName() {
        DefaultLoggingHandlerFactory factory = new DefaultLoggingHandlerFactory();
        ILoggerHandler handler = factory.getHandler("testName");

        assertEquals("testName", handler.getName());
    }

    @Test
    public void testGetHandler_withoutHandlerName() {
        DefaultLoggingHandlerFactory factory = new DefaultLoggingHandlerFactory();
        ILoggerHandler handler = factory.getHandler();

        assertEquals("com.parasoft.findings", handler.getName());
    }

    @Test
    public void testGetDefaultName() {
        DefaultLoggingHandlerFactory factory = new DefaultLoggingHandlerFactory();
        String defaultName = factory.getDefaultName();

        assertEquals("com.parasoft.findings", defaultName);
    }

    @Test
    public void testIsInitialized() {
        DefaultLoggingHandlerFactory factory = new DefaultLoggingHandlerFactory();
        boolean initialized = factory.isInitialized();

        assertTrue(initialized);
    }
}