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

package com.parasoft.findings.utils.doc.remote;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Common wrapper for org.apache.http.client.utils.URIBuilder
 */
public final class URIBuilder {
    private final org.apache.http.client.utils.URIBuilder _builder;

    public URIBuilder(URI uri) {
        _builder = new org.apache.http.client.utils.URIBuilder(uri);
    }

    public URIBuilder addParameter(String param, String value) {
        _builder.addParameter(param, value);
        return this;
    }

    public URI build()
            throws URISyntaxException {
        return _builder.build();
    }

    public URIBuilder setPath(String sPath) {
        _builder.setPath(sPath);
        return this;
    }
}