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

import java.util.function.Supplier;

/**
 * The base interface for logging handlers.
 */
public interface ILoggerHandler {
    /**
     * Return the logger name.
     * @return the logger name
     *
     * @post $result != null
     */
    String getName();

    /**
     * Log the message object with the specified <code>level</code>
     * including the stack trace of the <code>throwable</code>.
     * @param level the level
     * @param object the object to log.
     * @param throwable  the exception to log, stack trace will be printed.
     *
     * @pre level != null
     * @pre object != null
     */
    void log(String var1, Level level, Object object, Throwable throwable);

    /**
     * Log the message object with the specified <code>level</code>
     * including the stack trace of the <code>throwable</code>.
     *
     * Log message is only constructed if the specified level is active.
     *
     * @param level the level
     * @param objectSupplier function, which when called, produces the desired object to log
     * @param throwable  the exception to log, stack trace will be printed.
     *
     * @pre level != null
     * @pre objectSupplier != null
     */
    void log(String var1, Level level, Supplier<Object> objectSupplier, Throwable throwable);

}