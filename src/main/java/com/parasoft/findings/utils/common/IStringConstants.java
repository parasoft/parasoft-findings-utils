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

import com.parasoft.findings.utils.common.util.StringUtil;

/**
 * Generic string constants.
 */
public interface IStringConstants {
    /**
     * The String ": "
     */
    String COLON_SP = ": "; //$NON-NLS-1$

    /**
     * The comma (,) character.
     */
    String CHAR_COMMA = ","; //$NON-NLS-1$

    /**
     * The dot or period (.) character.
     */
    String CHAR_DOT = "."; //$NON-NLS-1$

    /**
     * The line feed (\n) character.
     */
    String CHAR_LINEFEED = "\n"; //$NON-NLS-1$ // parasoft-suppress PORT.LNSP "constant value"

    /**
     * The empty string.
     */
    String EMPTY = ""; //$NON-NLS-1$

    /**
     * Line separator system property.
     */
    String LINE_SEPARATOR = StringUtil.getLineSeparator();

    /** "false" */
    String FALSE = "false"; //$NON-NLS-1$

    /**
     * The String ".html"
     */
    String HTML_EXT = ".html";  //$NON-NLS-1$

    /**
     * The string "UTF-8".
     */
    String UTF_8 = "UTF-8"; //$NON-NLS-1$
}