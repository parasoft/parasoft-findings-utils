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

import java.io.Serializable;

/**
 * This class introduces a new Findings levels like TIME, TRACE etc...
 */
public final class Level // parasoft-suppress OWASP2021.A8.OROM "We are using the default serialization mechanism when reading or writing objects that implement the Serializable interface and do no further customization."
        implements Serializable {
    /**
     * Automatically generated variable: serialVersionUID
     */
    private static final long serialVersionUID = 3782514602936463022L;

    /**
     * The level value.
     */
    private int _level = -1;

    /**
     * The level name
     */
    private String _sLevelName = null;

    /**
     * Return the level value.
     */
    public int getLevel() {
        return this._level;
    }

    /**
     * Return the level name.
     *
     * @return The level name
     * @pre $none
     * @post $result != null
     * @post $result.trim().length() > 0
     */
    public String getName() {
        return _sLevelName;
    }

    /**
     * Constructor for Findings level.
     *
     * @param level      the level value.
     * @param sLevelName the level name.
     * @pre (sLevelName ! = null) && (sLevelName.trim().length() > 0)
     */
    private Level(int level, String sLevelName) {
        _level = level;
        _sLevelName = sLevelName;
    }


    /**
     * OFF level label
     */
    public final static String OFF_LEVEL_LABEL = "OFF";  //$NON-NLS-1$
    /**
     * TRACE level label
     */
    public final static String TRACE_LEVEL_LABEL = "TRACE";  //$NON-NLS-1$
    /**
     * TIME level label
     */
    public final static String TIME_LEVEL_LABEL = "TIME";  //$NON-NLS-1$
    /**
     * DEBUG level label
     */
    public final static String DEBUG_LEVEL_LABEL = "DEBUG";  //$NON-NLS-1$
    /**
     * INFO level label
     */
    public final static String INFO_LEVEL_LABEL = "INFO";  //$NON-NLS-1$
    /**
     * WARN level label
     */
    public final static String WARN_LEVEL_LABEL = "WARN";  //$NON-NLS-1$
    /**
     * ERROR level label
     */
    public final static String ERROR_LEVEL_LABEL = "ERROR";  //$NON-NLS-1$
    /**
     * FATAL level label
     */
    public final static String FATAL_LEVEL_LABEL = "FATAL";  //$NON-NLS-1$
    /**
     * ALL level label
     */
    public final static String ALL_LEVEL_LABEL = "ALL";  //$NON-NLS-1$

    /**
     * OFF level value
     */
    public final static int OFF_INT = 1;
    /**
     * TRACE level value
     */
    public final static int TRACE_INT = OFF_INT << 1;
    /**
     * TIME level value
     */
    public final static int TIME_INT = TRACE_INT << 1;
    /**
     * DEBUG level value
     */
    public final static int DEBUG_INT = TIME_INT << 1;
    /**
     * INFO level value
     */
    public final static int INFO_INT = DEBUG_INT << 1;
    /**
     * WARN level value
     */
    public final static int WARN_INT = INFO_INT << 1;
    /**
     * ERROR level value
     */
    public final static int ERROR_INT = WARN_INT << 1;
    /**
     * FATAL level value
     */
    public final static int FATAL_INT = ERROR_INT << 1;
    /**
     * ALL level value
     */
    public final static int ALL_INT = FATAL_INT << 1;

    /**
     * Predefined OFF level.
     */
    public final static Level OFF = new Level(OFF_INT, OFF_LEVEL_LABEL);
    /**
     * Predefined TRACE level.
     */
    public static final Level TRACE = new Level(TRACE_INT, TRACE_LEVEL_LABEL);
    /**
     * Predefined TIME level.
     */
    public final static Level TIME = new Level(TIME_INT, TIME_LEVEL_LABEL);
    /**
     * Predefined DEBUG level.
     */
    public final static Level DEBUG = new Level(DEBUG_INT, DEBUG_LEVEL_LABEL);
    /**
     * Predefined INFO level.
     */
    public final static Level INFO = new Level(INFO_INT, INFO_LEVEL_LABEL);
    /**
     * Predefined WARN level.
     */
    public final static Level WARN = new Level(WARN_INT, WARN_LEVEL_LABEL);
    /**
     * Predefined ERROR level.
     */
    public final static Level ERROR = new Level(ERROR_INT, ERROR_LEVEL_LABEL);
    /**
     * Predefined FATAL level.
     */
    public final static Level FATAL = new Level(FATAL_INT, FATAL_LEVEL_LABEL);
    /**
     * Predefined ALL level.
     */
    public final static Level ALL = new Level(ALL_INT, ALL_LEVEL_LABEL);

}