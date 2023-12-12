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

/**
 * The base interface for logging handlers factory
 */
public interface ILoggerHandlerFactory {
    /**
     * Return the handler (new, if not exist) for the specified name.
     * @param sName the handler name.
     * @return the handler.
     * @pre sName != null
     * @post $result != null
     */
    ILoggerHandler getHandler(String sName);

    /**
     * Return the handler (new, if not exist) for the dafault name.
     * @return the handler.
     * @post $result != null
     */
    ILoggerHandler getHandler();

    /**
     * Returns the default name for the handler
     * @return default handler name
     * @post $result != null
     */
    String getDefaultName();

    /**
     * Returns factory initialization status. If was not correctly initialized
     * returns <value>false</value>.
     * @return initialization status (<value>true</value> - properly initialized,
     *         <value>false</value> - otherwise)
     */
    boolean isInitialized();

    /**
     * Switch logging off for this factory's engine.
     * After calling this method, all handlers, provided (existing or new)
     * by this factory will not log anything.
     */
    void switchLoggingOff();

    /**
     * Switch logging on for this factory's engine.
     * After calling this method, all handlers, provided (existing or new)
     * by this factory will start logging.
     * This method IS NOT required to start logging for this factory engine.
     * You should call this method only if you have manually turned off logging
     * by invoking switchLoggingOff() - in other cases it is senseless.
     */
    void switchLoggingOn();

}