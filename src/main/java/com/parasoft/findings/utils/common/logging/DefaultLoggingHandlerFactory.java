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

import java.util.logging.Logger;

public class DefaultLoggingHandlerFactory
        implements ILoggerHandlerFactory {
    private static final String DEFAULT_NAME = "com.parasoft.findings"; //$NON-NLS-1$

    public ILoggerHandler getHandler(String sName) {
        return new DefaultLoggingHandler(Logger.getLogger(sName));
    }

    public ILoggerHandler getHandler() {
        return getHandler(DEFAULT_NAME);
    }

    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    public boolean isInitialized() {
        return true;
    }

    public void switchLoggingOff() { }

    public void switchLoggingOn() { }
}
