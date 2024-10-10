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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Routines having to do with files internationalization.
 */
public final class IntlUtil {

    private IntlUtil() {
    }

    public static String[] buildVariants(String sResourceRoot, String[] nlSuffixes) {
        return buildVariants(sResourceRoot, nlSuffixes, null);
    }

    public static String[] buildVariants(String sResourceRoot, String[] nlSuffixes, String extension) {
        sResourceRoot = sResourceRoot.replace('.', '/');
        String[] variants = new String[nlSuffixes.length];
        for (int i = 0; i < variants.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(sResourceRoot).append(nlSuffixes[i]);
            if (extension != null) {
                sb.append(extension);
            }
            variants[i] = sb.toString();
        }
        return variants;
    }

    /**
     * Build an array of internationalized files suffixes to search.
     * The returned array contains the suffixes in order from most specific to most generic.
     * So, in the FR_fr locale, it will return { _fr_FR.<ext>, _fr.<ext>, .<ext> }
     *
     * @param locale locale to use
     * @param sExtension file extension
     * @param bUseDefault when true, default suffix (without internationalization) will be added
     * @return array containing all file suffixes to use from most specific to most general
     *
     * @pre locale != null
     * @pre sExtension != null
     * @post $result != null
     */
    public static String[] getIntlSuffixes(Locale locale, String sExtension, boolean bUseDefault)
    {
        return getIntlSuffixes(locale, null, sExtension, bUseDefault);
    }

    /**
     * Build an array of internationalized files suffixes to search.
     * The returned array contains the suffixes in order from most specific to most generic.
     * So, in the FR_fr locale, it will return { _fr_FR.<ext>, _fr.<ext>, .<ext> }
     *
     * @param locale             locale to use
     * @param asSupportedLocales
     * @param sExtension         file extension
     * @param bUseDefault        when true, default suffix (without internationalization) will be added
     * @return array containing all file suffixes to use from most specific to most general
     * @pre locale != null
     * @pre sExtension != null
     * @post $result != null
     */
    public static String[] getIntlSuffixes(Locale locale, String[] asSupportedLocales, String sExtension, boolean bUseDefault) {
        String sLocale = locale.toString();
        List<String> result = new ArrayList<>(5);

        // add possible variants that extend specific locale
        String sVariant = AUTOENABLED_VARIANTS.get(sLocale);
        if (sVariant != null) {
            addUnique(UNDERSCORE + sVariant + sExtension, result);
        }

        // add hierarchy of locale codes
        while (true) {
            if (includeLocale(asSupportedLocales, sLocale)) {
                addUnique(UNDERSCORE + sLocale + sExtension, result);
            }
            int lastSeparator = sLocale.lastIndexOf(UNDERSCORE);
            if (lastSeparator == -1) {
                break;
            }
            sLocale = sLocale.substring(0, lastSeparator);
        }

        // add the empty suffix last (most general)
        if (bUseDefault) {
            addUnique(sExtension, result);
        }
        return result.toArray(new String[result.size()]);
    }

    private static boolean includeLocale(String[] asSupportedLocales, String sLocale) {
        if (asSupportedLocales == null) {
            return true;
        }

        for (String candidate : asSupportedLocales) {
            if (sLocale.equals(candidate)) {
                return true;
            }
        }

        return false;
    }

    private static void addUnique(String string, List<String> result) {
        if (!result.contains(string)) {
            result.add(string);
        }
    }

    static void close(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException e) {
            Logger.getLogger().error(e);
        }
    }

    /**
     * Variants that we try to provide automatically if specific locale is enabled.
     * If variant is valid and its resources are available (which is optional and depends on particular distro),
     * it will automatically be used.
     */
    private static final Map<String, String> AUTOENABLED_VARIANTS = new HashMap<String, String>() {
        {
            put(ZH_CN_LOCALE, ZH_CN_BEIRU_LOCALE);
        }
    };

    /**
     * Package directories' subdirectory where property files (<tt>.properties</tt>) are kept
     */
    static final String PROP_SUB_DIR = "res."; //$NON-NLS-1$
    /**
     * Default resource name.
     */
    static final String DEFAULT_RESOURCE_NAME = "messages"; //$NON-NLS-1$
    /**
     * Default extension of resource files
     */
    static final String DEFAULT_EXTENSION = ".properties"; //$NON-NLS-1$

    private static final String UNDERSCORE = "_"; //$NON-NLS-1$

    private static final String JA_LOCALE = "ja"; //$NON-NLS-1$
    private static final String ZH_CN_LOCALE = "zh_CN"; //$NON-NLS-1$
    private static final String ZH_CN_BEIRU_LOCALE = "zh_CN_BEIRU"; //$NON-NLS-1$

    public static final String[] SUPPORTED_LOCALES = {JA_LOCALE, ZH_CN_LOCALE, ZH_CN_BEIRU_LOCALE};

}