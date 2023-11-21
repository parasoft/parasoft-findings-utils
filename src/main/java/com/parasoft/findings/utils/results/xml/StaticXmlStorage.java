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

package com.parasoft.findings.utils.results.xml;

import java.util.*;


/**
 * Default implementation of coding standards xml storage.
 */
public class StaticXmlStorage {
    private IViolationXmlStorage[] _aViolationsStorages = null;

    /**
     * Constructor.
     *
     * @param aStorages the array of storage for violations
     * @pre aStorages != null
     */
    public StaticXmlStorage(IViolationXmlStorage[] aStorages) {
        _aViolationsStorages = aStorages;
    }

    public IViolationsSAXReader getViolationsReader(ResultVersionsManager versionsManager, FileImportPreferences importPreferences) {
        List<IViolationXmlStorage> storagesList = new ArrayList<IViolationXmlStorage>(); // parasoft-suppress CDD.DUPC "acceptable"
        List<String> tagsList = new ArrayList<String>();
        collectCompatibleStorages(versionsManager, tagsList, storagesList);

        if (storagesList.size() <= 0) {
            return null;
        }
        IViolationXmlStorage[] aStorages = new IViolationXmlStorage[storagesList.size()];
        aStorages = storagesList.toArray(aStorages);
        String[] aViolationTags = new String[tagsList.size()];
        aViolationTags = tagsList.toArray(aViolationTags);
        int[] aVersions = new int[aStorages.length];
        int[] aLegacyVersions = new int[aStorages.length];
        for (int i = 0; i < aVersions.length; i++) {
            aVersions[i] = versionsManager.getVersion(aStorages[i]);
            aLegacyVersions[i] = versionsManager.getLegacyVersion(aStorages[i]);
        }
        return new RuleViolationsReader(aStorages, aVersions, aLegacyVersions, aViolationTags, importPreferences);
    }

    private void collectCompatibleStorages(ResultVersionsManager versionsManager,
                                           List<String> tagsList, List<IViolationXmlStorage> storagesList) {
        for (IViolationXmlStorage _aViolationsStorage : _aViolationsStorages) {
            if (versionsManager.isCompatible(_aViolationsStorage)) {
                storagesList.add(_aViolationsStorage);
                int version = versionsManager.getVersion(_aViolationsStorage);
                tagsList.add(_aViolationsStorage.getTagName(version));
            } else {
                Logger.getLogger().warn("Illegal version of storage for result ID: " + _aViolationsStorage.getResultId()); //$NON-NLS-1$
            }
        }
    }

}
