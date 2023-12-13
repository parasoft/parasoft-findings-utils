package com.parasoft.findings.utils.common.logging;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultLoggingHandlerTest {

    @Test
    public void testGetName_nameExist() {
        DefaultLoggingHandler handler = new DefaultLoggingHandler(Logger.getLogger("testName"));

        assertEquals("testName", handler.getName());
    }

    @Test
    public void testGetName_nameNotExist() {
        Logger mockedLogger = mock(Logger.class);
        doReturn(null).when(mockedLogger).getName();
        DefaultLoggingHandler handler = new DefaultLoggingHandler(mockedLogger);

        assertEquals("Parasoft Findings Logging Handler", handler.getName());
    }

    /**
     * Test {@link DefaultLoggingHandler#log(String, Level, Object, Throwable)}
     */
    @Test
    public void testLog_1() {
        Logger mockedLogger = mock(Logger.class);
        DefaultLoggingHandler handler = new DefaultLoggingHandler(mockedLogger);
        Object object = "Object";
        Throwable throwable = new Throwable();

        handler.log("testSWrapperClassName", Level.INFO, object, throwable);

        verify(mockedLogger).logp(java.util.logging.Level.INFO, this.getClass().getName(), "testLog_1", " at line 40 : Object", throwable);
    }

    /**
     * Test {@link DefaultLoggingHandler#log(String, Level, Supplier, Throwable)}
     */
    @Test
    public void testLog_2() {
        Logger mockedLogger = mock(Logger.class);
        DefaultLoggingHandler handler = new DefaultLoggingHandler(mockedLogger);
        Supplier<Object> objectSupplier = () -> "Object";
        Throwable throwable = new Throwable();

        handler.log("testSWrapperClassName", Level.INFO, objectSupplier, throwable);

        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(mockedLogger).logp(eq(java.util.logging.Level.INFO), eq(this.getClass().getName()), eq("testLog_2"), eq(throwable), supplierCaptor.capture());
        assertEquals(" at line 55 : Object", supplierCaptor.getValue().get());
    }

    @Test
    public void testConvertLevel() {
        Logger mockedLogger = mock(Logger.class);
        DefaultLoggingHandler handler = new DefaultLoggingHandler(mockedLogger);
        Object object = new Object();
        Throwable throwable = new Throwable();
        String sWrapperClassName = "testSWrapperClassName";

        // OFF
        handler.log(sWrapperClassName, Level.OFF, object, throwable);
        verify(mockedLogger).logp(eq(java.util.logging.Level.OFF), anyString(), anyString(), anyString(), eq(throwable));

        // DEBUG
        handler.log(sWrapperClassName, Level.DEBUG, object, throwable);
        verify(mockedLogger).logp(eq(java.util.logging.Level.FINE), anyString(), anyString(), anyString(), eq(throwable));

        // WARN
        handler.log(sWrapperClassName, Level.WARN, object, throwable);
        verify(mockedLogger).logp(eq(java.util.logging.Level.WARNING), anyString(), anyString(), anyString(), eq(throwable));

        // ALL
        handler.log(sWrapperClassName, Level.ALL, object, throwable);
        verify(mockedLogger).logp(eq(java.util.logging.Level.ALL), anyString(), anyString(), anyString(), eq(throwable));

        // ERROR
        handler.log(sWrapperClassName, Level.ERROR, object, throwable);
        verify(mockedLogger, times(1)).logp(eq(java.util.logging.Level.SEVERE), anyString(), anyString(), anyString(), eq(throwable));
        // FATAL
        handler.log(sWrapperClassName, Level.FATAL, object, throwable);
        verify(mockedLogger, times(2)).logp(eq(java.util.logging.Level.SEVERE), anyString(), anyString(), anyString(), eq(throwable));

        // INFO
        handler.log(sWrapperClassName, Level.INFO, object, throwable);
        verify(mockedLogger, times(1)).logp(eq(java.util.logging.Level.INFO), anyString(), anyString(), anyString(), eq(throwable));
        // TRACE
        handler.log(sWrapperClassName, Level.TRACE, object, throwable);
        verify(mockedLogger, times(2)).logp(eq(java.util.logging.Level.INFO), anyString(), anyString(), anyString(), eq(throwable));
        // TIME
        handler.log(sWrapperClassName, Level.TIME, object, throwable);
        verify(mockedLogger, times(3)).logp(eq(java.util.logging.Level.INFO), anyString(), anyString(), anyString(), eq(throwable));
    }
}