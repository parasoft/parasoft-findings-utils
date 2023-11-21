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

/**
 * Init and keep one instance of result manager.
 */
public final class UResults {
    private static ResultsInitManager _INIT_MANAGER = null;

    /**
     * Prevent to make instance.
     */
    private UResults() {
        super();
    }

    /**
     * Gets results core.
     *
     * @return the results core or <code>null</code> if not initialized
     */
    public static DefaultResultsCore getResultsCore() {
        ResultsInitManager initManager = getInitManager();
        if (initManager != null) {
            return initManager.getResultsCore();
        }
        return null;
    }

    /**
     * Gets result factories manager.
     *
     * @return the instance of result factories manager
     * @post $result != null
     */
    public static ResultFactoriesManager getResultFactoriesManager() {
        DefaultResultsCore core = getResultsCore();
        if (core != null) {
            return core.getFactoriesManager();
        }
        return null;
    }

    private static synchronized ResultsInitManager getInitManager() {
        if (_INIT_MANAGER == null) {
            _INIT_MANAGER = new ResultsInitManager();
        }
        return _INIT_MANAGER;
    }

    private static class ResultsInitManager {
        private DefaultResultsCore _resultsCore = null;

        public DefaultResultsCore getResultsCore() {
            initialize();
            return _resultsCore;
        }

        private synchronized void initialize() {
            if (_resultsCore == null) {
                _resultsCore = new DefaultResultsCore();
            }
        }
    }

    private static class DefaultResultsCore {

        private ResultFactoriesManager _factoriesManager = null;

        public ResultFactoriesManager getFactoriesManager() {
            return getFactoriesManagerImpl();
        }

        private synchronized ResultFactoriesManager getFactoriesManagerImpl() {
            if (_factoriesManager == null) {
                _factoriesManager = new ResultFactoriesManager();
            }
            return _factoriesManager;
        }
    }
}
