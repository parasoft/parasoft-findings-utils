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

package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.IStringConstants;

import java.util.Locale;

public final class LocaleUtil {

    /** default locale to use */
    private static Locale _locale = null;

    /**
     * Constructor.
     */
    private LocaleUtil() {}

    /**
     * Returns the default locale to use. This setting can be overridden by using the system
     * properties <code>JTEST_LOCALE_LANGUAGE</code> and <code>JTEST_LOCALE_COUNTRY</code>.
     *
     * @return the default locale to use
     */
    public static synchronized Locale getLocale()
    {
        if (_locale == null) {
            final String sLanguage = System.getProperty("JTEST_LOCALE_LANGUAGE"); //$NON-NLS-1$
            if (sLanguage != null) {
                String sCountry = System.getProperty("JTEST_LOCALE_COUNTRY"); //$NON-NLS-1$
                if (sCountry == null) {
                    sCountry = IStringConstants.EMPTY;
                }
                _locale = new Locale(sLanguage, sCountry);
            } else {
                _locale = Locale.getDefault();
            }
        }
        return _locale;
    }
}
