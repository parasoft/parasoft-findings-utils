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

import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.logging.Logger;


public class LoggingHandler {
    private final Logger _logger;

    /**
     * Constructor.
     *
     * @param logger the logger
     */
    public LoggingHandler(Logger logger) {
        _logger = logger;
    }

    public void log(Level level, Object object, Throwable throwable) {
        final Throwable t = new Throwable();
        StackTraceElement[] stackTrace = t.getStackTrace();
        final StackTraceElement methodCaller = getMethodCaller(stackTrace);
        String sMessage = MessageFormat.format(
                " at line {0} : {1}", methodCaller.getLineNumber(), object); //$NON-NLS-1$
        _logger.logp(convertLevel(level, java.util.logging.Level.INFO), methodCaller.getClassName(), methodCaller.getMethodName(), sMessage, throwable);
    }

    public void log(Level level, Supplier<Object> objectSupplier, Throwable throwable) {
        final Throwable t = new Throwable();
        StackTraceElement[] stackTrace = t.getStackTrace();
        final StackTraceElement methodCaller = getMethodCaller(stackTrace);
        _logger.logp(convertLevel(level, java.util.logging.Level.INFO), methodCaller.getClassName(), methodCaller.getMethodName(), throwable, () -> {
            return MessageFormat.format(" at line {0} : {1}", methodCaller.getLineNumber(), objectSupplier.get()); //$NON-NLS-1$
        });
    }

    private StackTraceElement getMethodCaller(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!stackTraceElement.getClassName().equals(
                    FindingsLogger.class.getName())
                    && !stackTraceElement.getClassName().equals(getClass().getName())) {
                return stackTraceElement;
            }
        }
        return stackTrace[stackTrace.length - 1];
    }

    private static java.util.logging.Level convertLevel(Level level, java.util.logging.Level defaultLevel) {
        java.util.logging.Level result = defaultLevel;
        String sLevelName = level.getName();
        if (Level.OFF_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.OFF;
        } else if (Level.TRACE_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.INFO;
        } else if (Level.TIME_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.INFO;
        } else if (Level.DEBUG_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.FINE;
        } else if (Level.INFO_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.INFO;
        } else if (Level.WARN_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.WARNING;
        } else if (Level.ERROR_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.SEVERE;
        } else if (Level.FATAL_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.SEVERE;
        } else if (Level.ALL_LEVEL_LABEL.equals(sLevelName)) {
            result = java.util.logging.Level.ALL;
        }
        return result;
    }

}
