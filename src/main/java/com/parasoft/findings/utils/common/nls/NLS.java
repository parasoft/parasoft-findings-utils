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

package com.parasoft.findings.utils.common.nls;

import java.text.MessageFormat;

import com.parasoft.findings.utils.common.IStringConstants;

/**
 * Common superclass for all message bundle classes. Provides convenience methods for manipulating messages.
 */
public abstract class NLS {
    /**
     * Creates a new NLS instance.
     */
    protected NLS() {
        super();
    }

    /**
     * Initialize the given class with the values from the message bundle
     * placed in <B>res</B> sub package inside default messages.properties file.
     *
     * @param clazz the class where the constants will exist.
     * @pre clazz != null
     */
    public static void initMessages(Class<? extends NLS> clazz) {
        String resourcePath = MessageResourceBundle.getDefaultBundleName(clazz);
        MessageResourceBundle.load(resourcePath, clazz);
    }

    /**
     * Returns formatted message given a format message identifier.
     * This method uses the given format pattern to formats a message
     * using an message formatter passing
     * specified array of format arguments to the formatter.
     *
     * @param sPattern the format pattern
     * @param aArgs    the array of format arguments
     * @return the formatted message
     * @pre aArgs != null
     **/
    public final static String getFormatted(String sPattern, Object... aArgs) {
        if (sPattern == null) {
            Logger.getLogger().warn("Format pattern is null."); //$NON-NLS-1$
            return IStringConstants.EMPTY;
        }

        if (aArgs == null) {
            Logger.getLogger().warn("Formatting arguments are null."); //$NON-NLS-1$
            return IStringConstants.EMPTY;
        }

        String sResult = null;
        try {
            MessageFormat formatter = new MessageFormat(sPattern);
            sResult = formatter.format(aArgs);
        } catch (IllegalArgumentException iae) {
            Logger.getLogger().warn(iae);
            return IStringConstants.EMPTY;
        }

        if (sResult == null) { // parasoft-suppress BD.PB.CC "Bogus"
            Logger.getLogger().warn("Formatting result is null."); //$NON-NLS-1$
            return IStringConstants.EMPTY;
        }
        return sResult;
    }
}