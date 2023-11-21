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

package com.parasoft.findings.utils.common;

/**
 * Base class for all checked Findings exceptions.
 */
public class FindingsException
        extends Exception {
    private static final long serialVersionUID = -1;

    /**
     * Constructs a new instance of <tt>FindingsException</tt>.
     *
     * @param sMessage the message of exception
     */
    public FindingsException(String sMessage) {
        this(sMessage, null);
    }

    /**
     * Constructs a new instance of <tt>FindingsException</tt>.
     *
     * @param cause the cause of exception
     * @pre cause != null
     */
    public FindingsException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /**
     * Constructs a new instance of <tt>FindingsException</tt>.
     *
     * @param sMessage the message of exception
     * @param cause    the cause of exception
     */
    public FindingsException(String sMessage, Throwable cause) {
        super(sMessage, cause);
    }
}