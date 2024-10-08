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

import org.apache.http.client.HttpResponseException;

/**
 * Signals a non 2xx HTTP response.
 */
public final class ResponseWithContentException // parasoft-suppress OWASP2021.A8.OROM "Using default serialization mechanism."
        extends HttpResponseException {

    private static final long serialVersionUID = -419207194615475081L;

    private final ResponseContent _content;
    private final URI _requestUri;

    /**
     * @param statusCode
     * @param sReason
     * @param content
     * @param requestUri
     * @pre sReason != null
     * @pre content != null
     * @pre requestUri != null
     */
    public ResponseWithContentException(int statusCode, String sReason, ResponseContent content, URI requestUri) {
        super(statusCode, sReason);
        _content = content;
        _requestUri = requestUri;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " on " + _requestUri; //$NON-NLS-1$
    }

    public int getCode() {
        return super.getStatusCode();
    }

    public ResponseContent getResponseContent() {
        return _content;
    }

}