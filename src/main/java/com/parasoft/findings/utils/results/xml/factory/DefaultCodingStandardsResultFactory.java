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

package com.parasoft.findings.utils.results.xml.factory;

import com.parasoft.findings.utils.results.xml.IResultXmlStorage;
import com.parasoft.findings.utils.results.xml.IViolationXmlStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Default concrete result factory for coding standard violations.
 */
public class DefaultCodingStandardsResultFactory {
    private List<IResultXmlStorage> _storagesList = null;
    private boolean _bInitalized = false;

    /**
     * Constructor.
     */
    public DefaultCodingStandardsResultFactory() {
        super();
        _storagesList = new ArrayList<IResultXmlStorage>();
    }

    public final IResultXmlStorage[] getResultStorages() {
        initialize();
        return _storagesList.toArray(new IResultXmlStorage[_storagesList.size()]);
    }

    public final IViolationXmlStorage[] getViolationStorages() {
        List<IViolationXmlStorage> violationsStorages = new ArrayList<IViolationXmlStorage>();
        IResultXmlStorage[] aStorages = getResultStorages();
        for (int i = 0; i < aStorages.length; i++) {
            if (!(aStorages[i] instanceof IViolationXmlStorage)) {
                Logger.getLogger().warn("Expected IViolationXmlStorage"); //$NON-NLS-1$
                continue;
            }
            violationsStorages.add((IViolationXmlStorage) aStorages[i]);
        }
        return violationsStorages.toArray(new IViolationXmlStorage[violationsStorages.size()]);
    }

    private synchronized void initialize() {
        if (_bInitalized) {
            return;
        }
        _bInitalized = true;
        initializeStorages();
    }

    /**
     * Initializes storages.
     */
    private void initializeStorages() {
        _storagesList.add(new DefaultCodingStandardsViolationStorage());
        _storagesList.add(new FlowAnalysisResultStorage());
        _storagesList.add(new DupcodeViolationStorage());
        _storagesList.add(new MetricsViolationStorage());
    }

}