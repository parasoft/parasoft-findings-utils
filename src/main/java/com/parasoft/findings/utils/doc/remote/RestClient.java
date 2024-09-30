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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

/**
 * Class communicates with a target DTP REST service.
 */
public class RestClient {
    /**
     * The root URI used to construct target endpoints.
     * In most cases you should construct them with use of
     * {@link #getEndpointURI(String...)} and {@link #getEndpointBuilder(String...)}.
     */
    protected final URI _apiURI;

    private final HttpsExecutor.ProxyExecutor _executor;

    private static final int DEFAULT_CONNECT_TIMEOUT = 30; // 30 seconds

    private static final int DEFAULT_READ_TIMEOUT = 60; // 60 seconds

    private static final int DEFAULT_READ_LONG_TIMEOUT = 8 * 60; // 8 minutes

    private static final int _socketTimeoutMillis = DEFAULT_READ_TIMEOUT * 1000;

    private static final int _socketLongTimeoutMillis = DEFAULT_READ_LONG_TIMEOUT * 1000;

    private static final int _connectionTimeoutMillis = DEFAULT_CONNECT_TIMEOUT * 1000;

    /**
     * Separator used to construct rest requests
     */
    public static final char SEPARATOR_CHAR = '/';

    private static final String EMPTY_PATH = ""; //$NON-NLS-1$

    public RestClient(URI endpointURI) {
        _apiURI = endpointURI;
        _executor = createExecutor();
    }

    protected HttpsExecutor.ProxyExecutor createExecutor() {
        return HttpsExecutor.httpsTrustAllExecutor();
    }

    protected String getString(URI endpointUri)
            throws IOException {
        return getString(endpointUri, TimeoutCategory.NORMAL);
    }

    protected String getString(URI endpointUri, TimeoutCategory timeoutCategory)
            throws IOException {
        Request request = createGetRequest(endpointUri, timeoutCategory);
        ResponseContent content;
        try {
            content = executeRequest(request, endpointUri);
        } catch (final TimeoutException e) {
            throw new IOException() {

                @Override
                public Throwable getCause() {
                    return e;
                }
            };
        }
        return content.asString();
    }

    private ResponseContent executeRequest(Request request, URI uri)
            throws IOException, TimeoutException {
        Logger.getLogger().debug("Executing request:  URI: " + uri + ", RestClient: " + this);  //$NON-NLS-1$//$NON-NLS-2$
        ResponseContentHandlerImpl responseContentHandler = new ResponseContentHandlerImpl(uri);
        return executeRequest(request, responseContentHandler);
    }

    private ResponseContent executeRequest(Request request, ResponseContentHandlerImpl responseContentHandler)
            throws IOException {
        return _executor.execute(request, responseContentHandler);
    }

    protected URI getEndpointURI(URI baseURI, String... asSubPath) {
        return resolveMethodPath(baseURI, asSubPath);
    }

    protected URI getEndpointURI(String... asSubPath) {
        return getEndpointURI(_apiURI, asSubPath);
    }

    protected URIBuilder getEndpointBuilder(String... asSubPath) {
        return new URIBuilder(getEndpointURI(asSubPath));
    }

    private Request createGetRequest(URI uri, TimeoutCategory timeoutCategory) {
        Request request = Request.Get(uri);
        switch (timeoutCategory) {
            case LONG:
                request.socketTimeout(_socketLongTimeoutMillis);
                break;

            case NORMAL:
            default:
                request.socketTimeout(_socketTimeoutMillis);
                break;
        }
        request.connectTimeout(_connectionTimeoutMillis);

        return request;
    }

    private static URI resolveMethodPath(URI baseURI, String... asSubPath) {
        int elementsCount = asSubPath.length;
        if (elementsCount <= 0) {
            return baseURI;
        }

        // we've got a sub method here
        // so if the base URI does not end with '/' we need to properly put separators in right places
        String sBasePath = baseURI.getPath();
        StringBuilder sbPath = new StringBuilder(normalizePath(sBasePath, true, false));
        for (int i = 0; i < elementsCount; i++) {
            String sSegment = asSubPath[i];
            if (sSegment != null) {
                sbPath.append(normalizePath(sSegment, true, false));
            }
        }
        String sPath = sbPath.toString();

        try {

            //the path needs to be properly encoded, builder will do that
            URIBuilder builder = new URIBuilder(baseURI);
            builder.setPath(sPath);
            return builder.build();

        } catch (URISyntaxException e) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during encode path don't cause the build to fail."
            Logger.getLogger().error("Cannot encode path " + sPath + " using non encoded version.", e); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return baseURI.resolve(sPath);
    }

    private static String normalizePath(String sPath, boolean bSeparatorBefore, boolean bSeparatorAfter) {
        if ((sPath == null) || (sPath.trim().length() <= 0)) {
            return EMPTY_PATH;
        }

        if (bSeparatorBefore) {
            if (sPath.charAt(0) != SEPARATOR_CHAR) {
                sPath = SEPARATOR_CHAR + sPath;
            }
        } else {
            if (sPath.charAt(0) == SEPARATOR_CHAR) {
                sPath = sPath.substring(1);
            }
        }
        if (bSeparatorAfter) {
            if (sPath.charAt(sPath.length() - 1) != SEPARATOR_CHAR) {
                sPath = sPath + SEPARATOR_CHAR;
            }
        } else {
            if (sPath.charAt(sPath.length() - 1) == SEPARATOR_CHAR) {
                sPath = sPath.substring(0, sPath.length() - 1);
            }
        }
        return sPath;
    }

    public enum TimeoutCategory {
        /**
         * Normal timeouts operation.
         */
        NORMAL,

        /**
         * Long timeouts operation. Expected to be server side time consuming.
         */
        LONG
    }

    private static class ResponseContentHandlerImpl
            implements ResponseHandler<ResponseContent> {

        private final URI _requestUri;

        ResponseContentHandlerImpl(URI requestUri) {
            _requestUri = requestUri;
        }

        public ResponseContent handleResponse(HttpResponse response)
                throws IOException {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            ResponseContent content = entity != null ? createContent(entity) : ResponseContent.NO_CONTENT;
            int statusCode = statusLine.getStatusCode();
            if (statusCode >= 300) {
                Logger.getLogger().debug("Received response with status code: " + //$NON-NLS-1$
                        statusLine.getStatusCode() + ", reason: " + statusLine.getReasonPhrase()); //$NON-NLS-1$
                Logger.getLogger().debug("uri:" + _requestUri); //$NON-NLS-1$
                Logger.getLogger().debug("content:" + content); //$NON-NLS-1$
                throw new ResponseWithContentException(statusCode, statusLine.getReasonPhrase(), content, _requestUri);
            }

            return content;
        }

        protected ResponseContent createContent(HttpEntity entity)
                throws IOException {
            return new ResponseContent(EntityUtils.toByteArray(entity), ContentType.getOrDefault(entity));
        }
    }
}