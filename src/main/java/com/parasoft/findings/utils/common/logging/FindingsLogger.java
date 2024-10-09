/*
 * Copyright 2023 Parasoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parasoft.findings.utils.common.logging;

import java.io.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * The main logger, which redirect logging messages to connected logging system.
 */
public final class FindingsLogger {
    /**
     * The logger handler factory.
     */
    private static ILoggerHandlerFactory FACTORY = null;

    /**
     * The default wrapper class name.
     */
    private static final String DEFAULT_WRAPPER_CLASS_NAME = FindingsLogger.class.getName();

    /**
     * The logger handler
     */
    private ILoggerHandler _handler = null;

    /**
     * The wrapper class name.
     */
    private String _sWrapperClassName = DEFAULT_WRAPPER_CLASS_NAME;

    private static final String DEFAULT_LOGGER_NAME = "Findings logger default name";  //$NON-NLS-1$

    /**
     * Constructor.
     *
     * @param handler the handler to use.
     */
    private FindingsLogger(ILoggerHandler handler) {
        _handler = handler;
    }

    /**
     * Create, register and return new logger.
     *
     * @param sName    the logger name.
     * @param bWrapper flag if <code>true</code> logger can work in wrapped mode.
     * @return the findings logger instance.
     * @pre (sName ! = null) && (sName.trim().length() > 0)
     * @post $result != null
     */
    public static FindingsLogger getLogger(String sName, boolean bWrapper) {
        if (!sName.startsWith(COM_PARASOFT_PREFIX)) {
            sName = COM_PARASOFT_PREFIX + sName;
        }
        ILoggerHandler handler = null;
        if (FACTORY != null) {
            try {
                handler = FACTORY.getHandler(sName);
            } catch (Throwable thr) { // parasoft-suppress SECURITY.UEHL.LGE "Reviewed" // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to prevent exceptions from bubbling up and causing the program to terminate." // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during handler obtaining don't cause the process to fail."
                // error in factory - cannot obtain handler
            }
        }
        if (handler == null) {
            handler = new DefaultLoggingHandler(Logger.getLogger(sName));
        }

        FindingsLogger parasoftLogger = new FindingsLogger(handler);

        if (bWrapper) {
            parasoftLogger.setWrapperClassName(sName);
        }
        return parasoftLogger;
    }

    /**
     * Create, register and return new logger.
     *
     * @param theClass the class where the logger will be use.
     * @return the findings logger instance.
     * @pre theClass != null
     **/
    public static FindingsLogger getLogger(Class theClass) {
        return getLogger(theClass.getName(), false);
    }

    /**
     * Return new unnamed (with default name) logger.
     *
     * @return the findings logger instance.
     **/
    public static FindingsLogger getLogger() {
        if (FACTORY == null) {
            return getLogger(DEFAULT_LOGGER_NAME, false);
        }
        String sName = FACTORY.getDefaultName();
        return getLogger(sName, false);
    }

    /**
     * Set external wrapper class name, it is used to correctly fix class log info.
     *
     * @param sName the wrapper class name.
     * @pre (sName ! = null) && (sName.trim().length() > 0)
     **/
    public void setWrapperClassName(String sName) {
        _sWrapperClassName = sName;
    }

    /**
     * Log the message object with the <code>DEBUG</code> level.
     * If the specified object is instance of Throwable it's stack trace will be logged
     * with the same level.
     *
     * @param object the object to log.
     * @pre object != null
     **/
    public void debug(Object object) {
        if (object instanceof Throwable) {
            log(() -> createMessage((Throwable) object), Level.DEBUG, ((Throwable) object));
        } else {
            log(object, Level.DEBUG, null);
        }
    }

    /**
     * Log the message object with the <code>INFO</code> level.
     * If the specified object is instance of Throwable it's stack trace will be logged
     * with the same level.
     *
     * @param object the object to log.
     * @pre object != null
     **/
    public void info(Object object) {
        if (object instanceof Throwable) {
            log(() -> createMessage((Throwable) object), Level.INFO, ((Throwable) object));
        } else {
            log(object, Level.INFO, null);
        }
    }

    /**
     * Log the message object with the <code>INFO</code> level.
     * <code>throwable</code> and place where this log is created.
     *
     * @param object the object to log.
     * @param throwable the exception to log, stack trace will be printed.
     * @pre object != null
     **/
    public void info(Object object, Throwable throwable) {
        this.log(object, Level.INFO, throwable);
    }

    /**
     * Log the message object with the <code>WARN</code> level.
     * If the specified object is instance of Throwable it's stack trace will be logged
     * with the same level.
     *
     * @param object the object to log.
     * @pre object != null
     **/
    public void warn(Object object) {
        if (object instanceof Throwable) {
            log(() -> createMessage((Throwable) object), Level.WARN, ((Throwable) object));
        } else {
            log(object, Level.WARN, null);
        }
    }

    /**
     * Log the message object with the <code>WARN</code> level and print call stack trace.
     *
     * @param object the object to log.
     * @pre object != null
     **/
    public void warnTrace(Object object) {
        if (object instanceof Throwable) {
            log(() -> createMessage((Throwable) object), Level.WARN, ((Throwable) object));
        } else {
            Throwable throwable = new Throwable();
            log(() -> createStackTrace(object, throwable), Level.WARN, null);
        }
    }

    /**
     * Log the message object with the <code>WARN</code> level
     * including the stack trace of the <code>throwable</code>.
     *
     * @param object    the object to log.
     * @param throwable the exception to log, stack trace will be printed.
     * @pre object != null
     **/
    public void warn(Object object, Throwable throwable) {
        log(object, Level.WARN, throwable);
    }

    /**
     * Log the message object with the <code>ERROR</code> level.
     * If the specified object is instance of Throwable it's stack trace will be logged
     * with the same level.
     *
     * @param object the object to log.
     **/
    public void error(Object object) {
        if (object instanceof Throwable) {
            log(() -> createMessage((Throwable) object), Level.ERROR, ((Throwable) object));
        } else {
            log(object, Level.ERROR, null);
        }
    }

    /**
     * Log the message object with the <code>ERROR</code> level and print call stack trace.
     *
     * @param object the object to log.
     * @pre object != null
     **/
    public void errorTrace(Object object) {
        if (object instanceof Throwable) {
            log(() -> createMessage((Throwable) object), Level.ERROR, ((Throwable) object));
        } else {
            Throwable throwable = new Throwable();
            log(() -> createStackTrace(object, throwable), Level.ERROR, null);
        }
    }

    /**
     * Log the message object with the <code>ERROR</code> level
     * including the stack trace of the <code>throwable</code>.
     *
     * @param object    the object to log.
     * @param throwable the exception to log, stack trace will be printed.
     * @pre object != null
     **/
    public void error(Object object, Throwable throwable) {
        log(object, Level.ERROR, throwable);
    }

    /**
     * Sets current logger handlers factory. If argument factory is
     * <code>null</code> then sets LoggingHandlerFactory as current.
     *
     * @param factory the handlers factory to set
     */
    public static void setCurrentFactory(ILoggerHandlerFactory factory) {
        if (factory != null) {
            FACTORY = factory;
            FACTORY.switchLoggingOn();
        } else {
            FACTORY = new DefaultLoggingHandlerFactory();
        }
    }

    /**
     * Log the message object with the <code>level</code> level including the stack trace of the
     * <code>throwable</code> and place where this log is created.
     * <p>
     * NOTE: Use this method only in special situations such as moving java.util.logging logs to findings logger.
     *
     * @param sWrapperClassName - place from this log is created example ex. org.apache.jasper.JspC with
     *                          this parameter can be specify a different location than was given in the making
     *                          logger.
     * @param object            the object to log.
     * @param level             the logging level.
     * @param throwable         the exception to log, stack trace will be printed.
     * @pre _handler != null
     * @pre object != null
     * @pre level != null
     */
    public void log(String sWrapperClassName, Object object, Level level, Throwable throwable) {
        tryLog(() -> _handler.log(sWrapperClassName, level, object, throwable));
    }

    /**
     * Similar as @see {@link FindingsLogger#log(String, Object, Level, Throwable)} but use Supplier to obtain object to log.
     *
     * @param sWrapperClassName
     * @param objectSupplier    the supplier used to obtain object to log.
     * @param level             the logging level.
     * @param throwable         the exception to log, stack trace will be printed.
     * @pre _handler != null
     * @pre object != null
     * @pre level != null
     */
    public void log(String sWrapperClassName, Supplier<Object> objectSupplier, Level level, Throwable throwable) {
        if (objectSupplier == null) {
            tryLog(() -> _handler.log(sWrapperClassName, level, (Object) objectSupplier, throwable));
        }
        tryLog(() -> _handler.log(sWrapperClassName, level, objectSupplier, throwable));
    }

    private void tryLog(Runnable logMethod) {
        try {
            logMethod.run();
        } catch (Throwable thr) { // parasoft-suppress SECURITY.UEHL.LGE "Reviewed" // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to prevent exceptions from bubbling up and causing the program to terminate." // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during logging don't cause the process to fail."
            error(thr);
            // error in handler - logging failed
        }
    }

    /**
     * Log the message object with the <code>level</code> level
     * including the stack trace of the <code>throwable</code>.
     * <p>
     * NOTE: Use this method only in special situations such as moving java.util.logging logs to
     * findings logger.
     *
     * @param object    the object to log.
     * @param level     the logging level.
     * @param throwable the exception to log, stack trace will be printed.
     * @pre _handler != null
     * @pre object != null
     * @pre level != null
     */
    public void log(Object object, Level level, Throwable throwable) {
        log(_sWrapperClassName, object, level, throwable);
    }

    /**
     * Log the message object with the <code>level</code> level
     * including the stack trace of the <code>throwable</code> and message obtained from supplier.
     * <p>
     * NOTE: Use this method only in special situations such as moving java.util.logging logs to
     * findings logger.
     *
     * @param msgSupplier A function, which when called, produces the desired log message.
     * @param level       the logging level.
     * @param throwable   the exception to log, stack trace will be printed.
     * @pre _handler != null
     * @pre object != null
     * @pre level != null
     */
    public void log(Supplier<Object> msgSupplier, Level level, Throwable throwable) {
        log(_sWrapperClassName, msgSupplier, level, throwable);
    }

    /**
     * Creates message for given <code>Throwable</code>.
     * If {@link Throwable#getMessage()} returns <code>null</code> standard message is returned.
     *
     * @param throwable should not be <code>null</code>.
     * @return not <code>null</code> message describing <code>throwable</code>
     * @pre throwable != null
     * @post $result != null
     **/
    private static String createMessage(Throwable throwable) {
        String sMessage = throwable.getMessage();
        if (sMessage == null) {
            sMessage = STANDARD_THROWABLE_MESSAGE;
        }
        return sMessage;
    }

    /**
     * Create stack trace, base on current call, reduce line info with Logger
     * and Logger Wrapper classes calls.
     *
     * @param object the base object for message.
     * @return message with stack trace info
     * @post $result != null
     */
    private String createStackTrace(Object object, Throwable throwable) {
        StringBuffer sbResult = new StringBuffer();

        if (object != null) {
            sbResult.append(object);
            sbResult.append(LINE_SEPARATOR);
        }

        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer, true)); // parasoft-suppress MISC.ACPST "Required to prepare logging info."

        StringReader reader = null;
        LineNumberReader lineReader = null;
        String sWrapperClassWithDotPostfix = _sWrapperClassName + DOT_POSTFIX;

        try {
            reader = new StringReader(writer.toString());
            lineReader = new LineNumberReader(reader);

            String sLine = null;
            boolean bFound = false;
            do {
                try {
                    sLine = lineReader.readLine();
                } catch (IOException ioe) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during stack trace creating don't cause the process to fail."
                    error("Cannot create stack trace.", ioe); //$NON-NLS-1$
                    break;
                }
                if (sLine == null) {
                    break;
                }
                if (sLine.indexOf(sWrapperClassWithDotPostfix) > 0) {
                    bFound = true;
                    continue;
                }
                if (bFound) {
                    sbResult.append(sLine);
                    sbResult.append(LINE_SEPARATOR);
                }
            } while (true);
        } finally {
            if (lineReader != null) {
                try {
                    lineReader.close();
                } catch (IOException ioe) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during stack trace creating don't cause the process to fail."
                    error("Cannot create stack trace.", ioe); //$NON-NLS-1$
                }
                lineReader = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }
        return sbResult.toString();
    }

    /**
     * Dot postfix.
     */
    private static final String DOT_POSTFIX = ".";  //$NON-NLS-1$

    /**
     * Standard logging prefix for our code.
     */
    private final static String COM_PARASOFT_PREFIX = "com.parasoft.findings."; //$NON-NLS-1$

    /**
     * Default line separator
     */
    public final static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

    /**
     * Message to be used when {@link Throwable#getMessage()} returns <tt>null</tt>.
     */
    private final static String STANDARD_THROWABLE_MESSAGE = "Throwable caught"; //$NON-NLS-1$
}