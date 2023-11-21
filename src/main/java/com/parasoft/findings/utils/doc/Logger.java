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

package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.common.logging.FindingsLogger;

/**
 * The logger for this package.
 */
final class Logger {
    /**
     * Provides logger for this package.
     *
     * @return the logger instance
     * @post $result != null
     */
    public static FindingsLogger getLogger() {
        return LOGGER;
    }

    /**
     * Just to prevent instantiation.
     */
    private Logger() {
        super();
    }

    private final static FindingsLogger LOGGER = FindingsLogger.getLogger(Logger.class);

}
