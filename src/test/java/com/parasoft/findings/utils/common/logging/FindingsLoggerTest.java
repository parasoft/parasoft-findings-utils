package com.parasoft.findings.utils.common.logging;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class FindingsLoggerTest {
    private static final Logger logger = Logger.getLogger(FindingsLoggerTest.class.getName());

    @BeforeEach
    public void setUp() throws Throwable {
        Map<String, Object> fieldsMap = new HashMap<>();
        // Static fields which need to be reset, in case of dirty data is introduced by unit tests
        fieldsMap.put("FACTORY", null);

        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            resetStaticField(entry.getKey(), entry.getValue());
        }
    }

    public void resetStaticField(String filedName, Object newValue) {
        try {
            Field field = FindingsLogger.class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(null, newValue);
        } catch (Throwable e) {
            logger.log(java.util.logging.Level.WARNING, "Reset static variable:" + filedName + " with value " + newValue + " failed.");
            e.printStackTrace();
        }
    }

    public Object getPrivateVariableValue(String filedName, Object object) {
        try {
            Field field = FindingsLogger.class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Throwable e) {
            logger.log(java.util.logging.Level.WARNING, "Get private variable:" + filedName + " value failed.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Test {@link FindingsLogger#getLogger(String, boolean)}
     */
    @Test
    public void testGetLogger_1() {
        DefaultLoggingHandlerFactory factory = mock(DefaultLoggingHandlerFactory.class);
        FindingsLogger.setCurrentFactory(factory);
        try (MockedConstruction<FindingsLogger> mockedClass = mockConstruction(FindingsLogger.class)){
            FindingsLogger.getLogger("className1", true);
            FindingsLogger.getLogger("com.parasoft.findings.className2", true);

            verify(factory).getHandler("com.parasoft.findings.className1");

            final FindingsLogger logger1 = mockedClass.constructed().get(0);
            assertNotNull(logger1);
            verify(logger1).setWrapperClassName("com.parasoft.findings.className1");

            final FindingsLogger logger2 = mockedClass.constructed().get(1);
            assertNotNull(logger2);
            verify(logger2).setWrapperClassName("com.parasoft.findings.className2");
        }
    }

    /**
     * Test {@link FindingsLogger#getLogger(Class)}
     */
    @Test
    public void testGetLogger_2() {
        try (MockedStatic<FindingsLogger> mockedStatic = mockStatic(FindingsLogger.class)) {
            mockedStatic.when(() -> FindingsLogger.getLogger(FindingsLoggerTest.class)).thenCallRealMethod();

            final FindingsLogger logger = FindingsLogger.getLogger(FindingsLoggerTest.class);

            mockedStatic.verify(() -> FindingsLogger.getLogger(eq(FindingsLoggerTest.class.getName()), eq(false)));
        }
    }

    /**
     * Test {@link FindingsLogger#getLogger()}
     */
    @Test
    public void testGetLogger_3_FACTORY_notExist() {
        try (MockedStatic<FindingsLogger> mockedStatic = mockStatic(FindingsLogger.class)) {
            mockedStatic.when(FindingsLogger::getLogger).thenCallRealMethod();

            FindingsLogger.getLogger();

            mockedStatic.verify(() -> FindingsLogger.getLogger(eq("Findings logger default name"), eq(false)));
        }
    }

    /**
     * Test {@link FindingsLogger#getLogger()}
     */
    @Test
    public void testGetLogger_3_FACTORY_exist() {
        DefaultLoggingHandlerFactory factory = mock(DefaultLoggingHandlerFactory.class);
        doReturn("testDefaultLoggerName").when(factory).getDefaultName();
        FindingsLogger.setCurrentFactory(factory);
        try (MockedStatic<FindingsLogger> mockedStatic = mockStatic(FindingsLogger.class)) {
            mockedStatic.when(FindingsLogger::getLogger).thenCallRealMethod();

            FindingsLogger.getLogger();

            mockedStatic.verify(() -> FindingsLogger.getLogger(eq("testDefaultLoggerName"), eq(false)));
        }
    }

    @Test
    public void testSetWrapperClassName() {
        FindingsLogger logger = FindingsLogger.getLogger();
        logger.setWrapperClassName("testWrapperClassName");

        String sWrapperClassName = (String) getPrivateVariableValue("_sWrapperClassName", logger);

        assertEquals("testWrapperClassName", sWrapperClassName);
    }

    /**
     * Test {@link FindingsLogger#debug(Object)}
     */
    @Test
    public void testDebug() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Throwable throwable = new Throwable();
        Object nonThrowableObject = "nonThrowableObject";

        logger.debug(throwable);
        verify(logger).log(any(Supplier.class), eq(Level.DEBUG), eq(throwable));

        logger.debug(nonThrowableObject);
        verify(logger).log(eq("nonThrowableObject"), eq(Level.DEBUG), eq(null));
    }

    /**
     * Test {@link FindingsLogger#info(Object)}
     */
    @Test
    public void testInfo_1() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Throwable throwable = new Throwable();
        Object nonThrowableObject = "nonThrowableObject";

        logger.info(throwable);
        verify(logger).log(any(Supplier.class), eq(Level.INFO), eq(throwable));

        logger.info(nonThrowableObject);
        verify(logger).log(eq("nonThrowableObject"), eq(Level.INFO), eq(null));
    }

    /**
     * Test {@link FindingsLogger#info(Object, Throwable)}
     */
    @Test
    public void testInfo_2() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Object stringObject = "nonThrowableObject";
        Throwable throwable = new Throwable();

        logger.info(stringObject, throwable);

        verify(logger).log(eq(stringObject), eq(Level.INFO), eq(throwable));
    }

    /**
     * Test {@link FindingsLogger#warn(Object)}
     */
    @Test
    public void testWarn_1() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Throwable throwable = new Throwable();
        Object nonThrowableObject = "nonThrowableObject";

        logger.warn(throwable);
        verify(logger).log(any(Supplier.class), eq(Level.WARN), eq(throwable));

        logger.warn(nonThrowableObject);
        verify(logger).log(eq("nonThrowableObject"), eq(Level.WARN), eq(null));
    }

    /**
     * Test {@link FindingsLogger#warnTrace(Object)}
     */
    @Test
    public void testWarnTrace() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Throwable throwable = new Throwable();
        Object nonThrowableObject = new Object();

        logger.warnTrace(throwable);
        verify(logger).log(any(Supplier.class), eq(Level.WARN), eq(throwable));

        logger.warnTrace(nonThrowableObject);
        verify(logger).log(any(Supplier.class), eq(Level.WARN), eq(null));
    }

    /**
     * Test {@link FindingsLogger#warn(Object, Throwable)}
     */
    @Test
    public void testWarn_2() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Object stringObject = "nonThrowableObject";
        Throwable throwable = new Throwable();

        logger.warn(stringObject, throwable);

        verify(logger).log(eq(stringObject), eq(Level.WARN), eq(throwable));
    }

    /**
     * Test {@link FindingsLogger#error(Object)}
     */
    @Test
    public void testError_1() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Throwable throwable = new Throwable();
        Object nonThrowableObject = "nonThrowableObject";

        logger.error(throwable);
        verify(logger).log(any(Supplier.class), eq(Level.ERROR), eq(throwable));

        logger.error(nonThrowableObject);
        verify(logger).log(eq(nonThrowableObject), eq(Level.ERROR), eq(null));
    }

    /**
     * Test {@link FindingsLogger#error(Object, Throwable)}
     */
    @Test
    public void testError_2() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Object object = new Object();
        Throwable throwable = new Throwable();

        logger.error(object, throwable);

        verify(logger).log(eq(object), eq(Level.ERROR), eq(throwable));
    }

    /**
     * Test {@link FindingsLogger#errorTrace(Object)}
     */
    @Test
    public void testErrorTrace() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any());
        Throwable throwable = new Throwable();
        Object nonThrowableObject = new Object();

        logger.errorTrace(throwable);
        verify(logger).log(any(Supplier.class), eq(Level.ERROR), eq(throwable));

        logger.errorTrace(nonThrowableObject);
        verify(logger).log(any(Supplier.class), eq(Level.ERROR), eq(null));
    }

    @Test
    public void testSetCurrentFactory_normal() {
        DefaultLoggingHandlerFactory factory = mock(DefaultLoggingHandlerFactory.class);
        FindingsLogger.setCurrentFactory(factory);

        verify(factory).switchLoggingOn();
    }

    @Test
    public void testSetCurrentFactory_withNullFactory() {
        FindingsLogger.setCurrentFactory(null);
        Object factory = getPrivateVariableValue("FACTORY", null);

        assertNotNull(factory);
        assertEquals(DefaultLoggingHandlerFactory.class, factory.getClass());
    }

    /**
     * Test {@link FindingsLogger#log(String, Object, Level, Throwable)}
     */
    @Test
    public void testLog_1() {
        DefaultLoggingHandlerFactory handlerFactory = mock(DefaultLoggingHandlerFactory.class);
        DefaultLoggingHandler handler = mock(DefaultLoggingHandler.class);
        doReturn(handler).when(handlerFactory).getHandler(any());
        FindingsLogger.setCurrentFactory(handlerFactory);

        FindingsLogger logger = spy(FindingsLogger.getLogger("logger", false));
        Object object = new Object();
        Throwable throwable = new Throwable();

        logger.log("testWrapper", object, Level.INFO, throwable);

        verify(handler).log(eq("testWrapper"), eq(Level.INFO), eq(object), eq(throwable));
    }

    /**
     * Test {@link FindingsLogger#log(String, Supplier, Level, Throwable)}
     */
    @Test
    public void testLog_2() {
        DefaultLoggingHandlerFactory handlerFactory = mock(DefaultLoggingHandlerFactory.class);
        DefaultLoggingHandler handler = mock(DefaultLoggingHandler.class);
        doReturn(handler).when(handlerFactory).getHandler(any());
        FindingsLogger.setCurrentFactory(handlerFactory);

        FindingsLogger logger = spy(FindingsLogger.getLogger("logger", false));
        Supplier<Object> objectSupplier = Object::new;
        Throwable throwable = new Throwable();

        logger.log("testWrapper", objectSupplier, Level.INFO, throwable);
        verify(handler).log(eq("testWrapper"), eq(Level.INFO), eq(objectSupplier), eq(throwable));

        logger.log("testWrapper", null, Level.INFO, throwable);
        verify(handler).log(eq("testWrapper"), eq(Level.INFO), eq(null), eq(throwable));
    }

    /**
     * Test {@link FindingsLogger#log(Object, Level, Throwable)}
     */
    @Test
    public void testLog_3() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any(), any());
        Object object = new Object();
        Throwable throwable = new Throwable();

        logger.log(object, Level.INFO, throwable);

        verify(logger).log(eq("com.parasoft.findings.utils.common.logging.FindingsLogger"), eq(object), eq(Level.INFO), eq(throwable));
    }

    /**
     * Test {@link FindingsLogger#log(Supplier, Level, Throwable)}
     */
    @Test
    public void testLog_4() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        doNothing().when(logger).log(any(), any(), any(), any());
        Supplier<Object> msgSupplier = Object::new;
        Throwable throwable = new Throwable();

        logger.log(msgSupplier, Level.INFO, throwable);

        verify(logger).log(eq("com.parasoft.findings.utils.common.logging.FindingsLogger"), eq(msgSupplier), eq(Level.INFO), eq(throwable));
    }

    @Test
    public void testCreateMessage_withErrorMessage() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        Throwable throwable = new Throwable("ErrorMessage");
        ArgumentCaptor<Supplier> messageSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        logger.error(throwable);

        verify(logger).log(messageSupplierCaptor.capture(), eq(Level.ERROR), eq(throwable));
        String message = (String) messageSupplierCaptor.getValue().get(); // call createMessage(Throwable) when call get()
        assertEquals("ErrorMessage", message);
    }

    @Test
    public void testCreateMessage_withEmptyMessage() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        Throwable throwable = new Throwable((String) null);
        ArgumentCaptor<Supplier> messageSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        logger.error(throwable);

        verify(logger).log(messageSupplierCaptor.capture(), eq(Level.ERROR), eq(throwable));
        String message = (String) messageSupplierCaptor.getValue().get(); // call createMessage(Throwable) when call get()
        assertEquals("Throwable caught", message);
    }

    @Test
    public void testCreateStackTrace_withNullObject() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        ArgumentCaptor<Supplier> messageSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        logger.errorTrace(null);

        verify(logger).log(messageSupplierCaptor.capture(), eq(Level.ERROR), eq(null));
        String trace = (String) messageSupplierCaptor.getValue().get(); // call createStackTrace(Object, Throwable) when call get()
        assertNotNull(trace);
        assertTrue(trace.contains("com.parasoft.findings.utils.common.logging.FindingsLoggerTest.testCreateStackTrace_withNullObject(FindingsLoggerTest.java:398)"));
    }

    @Test
    public void testCreateStackTrace_withObject() {
        FindingsLogger logger = spy(FindingsLogger.getLogger());
        ArgumentCaptor<Supplier> messageSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Object stringObject = "Error string message";
        logger.errorTrace(stringObject);

        verify(logger).log(messageSupplierCaptor.capture(), eq(Level.ERROR), eq(null));
        String trace = (String) messageSupplierCaptor.getValue().get(); // call createStackTrace(Object, Throwable) when call get()
        assertNotNull(trace);
        assertTrue(trace.contains((String)stringObject));
        assertTrue(trace.contains("com.parasoft.findings.utils.common.logging.FindingsLoggerTest.testCreateStackTrace_withObject(FindingsLoggerTest.java:411)"));
    }
}