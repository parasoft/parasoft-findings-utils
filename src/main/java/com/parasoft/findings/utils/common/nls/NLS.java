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

    private static final String NO_MESSAGE_AVAILABLE = "No message available.";  //$NON-NLS-1$
    private static final String MISSING_ARGUMENT = "<missing argument>";  //$NON-NLS-1$
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
     * @param sMessage
     * @param binding
     *
     * @return String
     */
    public static String bind(String sMessage, Object binding)
    {
        if (sMessage == null) {
            return NO_MESSAGE_AVAILABLE;
        }
        StringBuilder sbResult = internalBind(sMessage, new Object[] { binding } );
        return sbResult.toString();
    }

    /**
     * @param sMessage
     * @param binding1
     * @param binding2
     *
     * @return String
     */
    public static String bind(String sMessage, Object binding1, Object binding2)
    {
        if (sMessage == null) {
            return NO_MESSAGE_AVAILABLE;
        }
        StringBuilder sbResult = internalBind(sMessage, new Object[] { binding1, binding2 } );
        return sbResult.toString();
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
        } catch (IllegalArgumentException iae) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during getting formatted value don't cause the build to fail."
            Logger.getLogger().warn(iae);
            return IStringConstants.EMPTY;
        }

        if (sResult == null) { // parasoft-suppress BD.PB.CC "Bogus"
            Logger.getLogger().warn("Formatting result is null."); //$NON-NLS-1$
            return IStringConstants.EMPTY;
        }
        return sResult;
    }

    /**
     * Perform the string substitution on the given message.
     *
     * @param sMessage
     * @param aArgs
     *
     * @return StringBuilder
     */
    private static StringBuilder internalBind(String sMessage, Object[] aArgs)
    {
        int argsLength = aArgs.length;
        int mesLength = sMessage.length();
        int bufLenght = mesLength + (argsLength * 5);

        StringBuilder sbResult = new StringBuilder(bufLenght);
        for (int idxAt = 0; idxAt < mesLength; idxAt++) {
            char chr = sMessage.charAt(idxAt);
            switch (chr) {
                case '{' :
                    int idx = sMessage.indexOf('}', idxAt);
                    if (idx == -1) {
                        sbResult.append(chr); break;
                    }
                    idxAt++;
                    if (idxAt >= mesLength) {
                        sbResult.append(chr); break;
                    }
                    try {
                        int num = Integer.parseInt(sMessage.substring(idxAt, idx));
                        if ((num >= argsLength) || (num < 0)) {
                            sbResult.append(MISSING_ARGUMENT);
                        } else {
                            sbResult.append(aArgs[num]);
                        }
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException(nfe);
                    }
                    idxAt = idx; break;
                case '\'' :
                    int nextIdx = idxAt + 1;
                    if (nextIdx >= mesLength) {
                        sbResult.append(chr); break;
                    }
                    char next = sMessage.charAt(nextIdx);
                    if (next == '\'') {
                        idxAt++;
                        sbResult.append(chr); break;
                    }
                    idx = sMessage.indexOf('\'', nextIdx);
                    if (idx == -1) {
                        sbResult.append(chr); break;
                    }
                    sbResult.append(sMessage.substring(nextIdx, idx));
                    idxAt = idx; break;
                default :
                    sbResult.append(chr); break;
            }
        }
        return sbResult;
    }
}