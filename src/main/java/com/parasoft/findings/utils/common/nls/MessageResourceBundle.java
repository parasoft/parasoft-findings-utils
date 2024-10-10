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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Class responsible for loading message values from a property file and
 * assigning them directly to the fields of a messages class.
 */
public final class MessageResourceBundle {
    /**
     * Just to prevent doing instances.
     **/
    private MessageResourceBundle() {
    }

    /**
     * Provides bundle name to use with load methods supported by this class.
     * By default it follows a pattern like: package.where.clazz.resides.res.messages
     *
     * @param clazz
     * @return String
     */
    public static String getDefaultBundleName(Class<?> clazz) {
        return getResourceBase(clazz) + IntlUtil.PROP_SUB_DIR + IntlUtil.DEFAULT_RESOURCE_NAME;
    }

    /**
     * Load the given resource bundle using the specified class loader.
     *
     * @param sBundleName
     * @param clazz
     */
    public static void load(String sBundleName, Class<?> clazz) {
        String[] suffixes = getSupportedSuffixes();
        IntlResourceProvider loader = IntlResourceProvider.forClasspathResources(suffixes, clazz);
        load(sBundleName, loader, clazz);
    }

    /**
     * Load the given resource bundle using the specified loader and its preferred resource locations.
     *
     * @param sBundleName
     * @param loader
     * @param clazz
     */
    private static void load(String sBundleName, IntlResourceProvider loader, Class<?> clazz) {
        Field[] aFields = clazz.getDeclaredFields();
        boolean bIsAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;

        // build a map of field names to Field objects
        int length = aFields.length;
        Map<String, Field> fields = new HashMap<String, Field>(length * 2);
        for (int i = 0; i < length; i++) {
            fields.put(aFields[i].getName(), aFields[i]);
        }
        MessagesProperties properties = new MessagesProperties(fields, sBundleName, bIsAccessible);
        loadImpl(sBundleName, loader, properties);
        findMissing(sBundleName, fields, aFields, bIsAccessible);
    }

    /**
     * @return String[]
     */
    private static String[] getSupportedSuffixes() {
        if (_nlSuffixes == null) {
            // Limit resources to supported locals only to improve vstudio loading performance
            _nlSuffixes = IntlUtil.getIntlSuffixes(Locale.getDefault(), IntlUtil.SUPPORTED_LOCALES, IntlUtil.DEFAULT_EXTENSION, true);
        }
        return _nlSuffixes;
    }

    private static void loadImpl(String sBundleName, IntlResourceProvider loader, MessageBundleProperties properties) {
        InputStream[] resources = loader.getResources(sBundleName);
        for (InputStream input : resources) {
            try {
                properties.load(input);
            } catch (IOException ioe) {
                Logger.getLogger().error("Error loading " + input, ioe); //$NON-NLS-1$
            } finally {
                IntlUtil.close(input);
            }
        }
    }

    /**
     * Extracts resource base, a leading part of fully qualified resource file name from a Class
     *
     * @param clazz the instance of class
     * @return String
     * @pre clazz != null
     */
    private static String getResourceBase(Class<?> clazz) {
        String sName = clazz.getName();
        int idx = sName.lastIndexOf('.');
        if (idx > 0) {
            return sName.substring(0, idx + 1);
        }
        Logger.getLogger().warn("Cannot extract base for class: " + clazz.getName()); //$NON-NLS-1$
        return ""; //$NON-NLS-1$
    }

    private static final int MOD_EXPECT = Modifier.PUBLIC | Modifier.STATIC;
    private static final int MOD_MASK = MOD_EXPECT | Modifier.FINAL;

    private static final Object MES_ASSIGN = new Object();

    private static String[] _nlSuffixes;

    private static void findMissing(String sBundleName, Map<String, Field> fieldMap, Field[] aFields, boolean bIsAccessible) {
        for (Field field : aFields) {
            if ((field.getModifiers() & MOD_MASK) != MOD_EXPECT) {
                continue;
            }
            if (fieldMap.get(field.getName()) == MES_ASSIGN) {
                continue;
            }
            try {
                String value = "NLS missing message: " + field.getName() + " in: " + sBundleName; //$NON-NLS-1$ //$NON-NLS-2$
                Logger.getLogger().warn(value);
                if (!bIsAccessible) {
                    makeAccessible(field);
                }
                field.set(null, value);
            } catch (Exception exc) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to ensure exceptions during missing finding don't cause the process to fail."
                Logger.getLogger().error("Error in setting message value for: " + field.getName(), exc); //$NON-NLS-1$
            }
        }
    }

    /**
     * Change the accessibility of the specified field so we can set its value
     * to be the appropriate message string.
     *
     * @param field
     */
    private static void makeAccessible(final Field field) {
        if (System.getSecurityManager() == null) {
            field.setAccessible(true);
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                field.setAccessible(true);
                return null;
            }
        });
    }

    /**
     * Class which sub-classes java.util.Properties and uses the #put method to
     * set field values rather than storing the values in the table.
     */
    private static class MessagesProperties // parasoft-suppress OWASP2021.A8.OROM "Using default serialization mechanism."
            extends MessageBundleProperties {
        private static final long serialVersionUID = 1L;

        private final String _sBundleName;

        private final Map _fieldMap;
        private final boolean _bIsAccessible;

        /**
         * @param fieldMap
         * @param sBundleName
         * @param bIsAccessible
         */
        public MessagesProperties(Map fieldMap, String sBundleName, boolean bIsAccessible) {
            super();
            _fieldMap = fieldMap;
            _sBundleName = sBundleName;
            _bIsAccessible = bIsAccessible;
        }

        /**
         * //javadoc-ref//@see MessageBundleProperties#put(java.lang.Object, java.lang.Object)
         */
        @Override
        @SuppressWarnings("unchecked")
        public synchronized Object put(Object key, Object value) {
            Object object = _fieldMap.put(key, MES_ASSIGN);
            if (object == MES_ASSIGN) {
                return null;
            }
            if (object == null) {
                Logger.getLogger().warn("NLS unused message: " + key + " in: " + _sBundleName); //$NON-NLS-1$ //$NON-NLS-2$
                return null;
            }
            final Field field = (Field) object;
            if ((field.getModifiers() & MOD_MASK) != MOD_EXPECT) {
                return null;
            }
            try {
                if (!_bIsAccessible) {
                    makeAccessible(field);
                }
                field.set(null, value);
            } catch (Exception exc) { // parasoft-suppress OWASP2021.A5.NCE "This is intentionally designed to ensure exceptions during message value setting don't cause the process to fail."
                Logger.getLogger().error("Error in setting message value.", exc); //$NON-NLS-1$
            }
            return null;
        }
    }

    /**
     * Properties suitable for loading resources in MessageResourceBundle.
     * They redefine {@link #put(Object, Object)} to never allow resetting of already existing values:
     * the first available resource is the most specific and should be used.
     * Class can be overridden to customise behaviour.
     */
    public static class MessageBundleProperties
            extends Properties {
        private static final long serialVersionUID = -7333013719731974205L;

        public MessageBundleProperties() {
            super();
        }

        @Override
        public synchronized Object put(Object key, Object value) {
            if (!containsKey(key)) {
                super.put(key, value);
            }
            return null;
        }
    }
}