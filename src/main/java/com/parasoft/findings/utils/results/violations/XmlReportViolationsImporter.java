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

package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.testableinput.FindingsLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.xml.XmlReportReader;
import com.parasoft.findings.utils.results.xml.FileImportPreferences;

import java.io.File;
import java.net.URL;
import java.util.Properties;

public final class XmlReportViolationsImporter {
    private final Properties _properties;

    /**
     * Just to prevent doing instances.
     **/
    public XmlReportViolationsImporter(Properties properties) {
        _properties = properties;
    }

    /**
     * Imports results from given xml file.
     *
     * @param file source xml file
     * @return import result or null if import cannot be performed.
     */
    public XmlReportViolations performImport(File file) {
        if (!file.exists()) {
            Logger.getLogger().warn("Report file is not existing: " + file.getAbsolutePath()); //$NON-NLS-1$
            return null;
        }
        logProperties();

        FileImportPreferences prefs = new FileImportPreferences(file);
        ITestableInputLocationMatcher matcher = new FindingsLocationMatcher();
        return importViolations(prefs, matcher);
    }

    private XmlReportViolations importViolations(FileImportPreferences preferences, ITestableInputLocationMatcher locationMatcher) {
        return importData(preferences, locationMatcher);
    }

    private XmlReportViolations importData(FileImportPreferences preferences,
                                           ITestableInputLocationMatcher locationFinder) {
        URL reportURL = preferences.getReportURL();
        if (reportURL == null) {
            Logger.getLogger().warn("No report url in preferences."); //$NON-NLS-1$
            return null;
        }
        XmlReportReader reader = new XmlReportReader(preferences, locationFinder);
        return new XmlReportViolations(reportURL, reader);
    }

    private void logProperties() {
        Properties result = new Properties();
        for (Object sKey : _properties.keySet()) {
            String sKeyString = String.valueOf(sKey);
            boolean canPrintValue = !isSensitiveSetting(sKeyString);
            String sValue = _properties.getProperty(sKeyString); // parasoft-suppress BD.EXCEPT.NP "False positive, java.lang.String.valueOf(java.lang.Object) method returns "null" string for null object."
            String sPrintedValue = canPrintValue ? sValue : anonymize(sValue);
            result.setProperty(sKeyString, sPrintedValue);
        }
        Logger.getLogger().info("Properties used in importResults " + result); //$NON-NLS-1$
    }

    private static String anonymize(String src) {
        if (src == null) {
            return ">>not set<<";
        }
        if (src.isEmpty()) {
            return ">>empty<<";
        }
        return ">>hidden<<";
    }

    private boolean isSensitiveSetting(String key) {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return (key != null) &&
                (key.endsWith(".password") || key.endsWith(".secret") || key.endsWith(".proxyPassword"));
    }
}
