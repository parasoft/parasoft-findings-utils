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

/**
 * Interface for result storages. Implementations are responsible
 * for dumping/retrieving results to/from xml format.
 */
public interface IResultXmlStorage {

    /**
     * Returns an instance of IResultSAXReader that can read results written by this storage
     * class.
     *
     * @param xmlVersion xml version
     * @return an instance of IResultSAXReader that can read results written by this storage
     * class.
     */
    IResultSAXReader getReader(int xmlVersion);

    /**
     * Returns the identifier of result handled by this storage.
     *
     * @return the identifier of result handled by this storage
     * @post $result != null
     */
    String getResultId();

    /**
     * Gets version number of this storage
     *
     * @return the version number
     * @post $result > 0
     */
    int getVersion();

    /**
     * Checks if this storage is compatible with given older version of storage.
     *
     * @param version the number of older version to check
     * @return <tt>true</tt> if storages compatible, <tt>false</tt> otherwise
     * @pre version > 0
     * @pre version < getVersion()
     */
    boolean isCompatible(int version);

}