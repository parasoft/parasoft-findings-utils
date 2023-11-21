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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Heavily inspired by code in org.apache.http.client.fluent.Executor.
 * Difference is that we need to inject an X509TrustManager.
 * Source base: com.parasoft.sdm.api.bts.impl.jira5.util.http.HttpsExecutor
 **/
public final class HttpsExecutor {
    private HttpsExecutor() {
        // Just to prevent do instances
    }

    private static final HttpClient CLIENT;

    static {
        X509TrustManager trustAllManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        HttpClientBuilder builder = HttpClientBuilder.create();

        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory()); //$NON-NLS-1$

        SSLConnectionSocketFactory ssl = null;
        try {
            SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            sslContext.init(null, new TrustManager[]{trustAllManager}, null);

            ssl = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            builder.setSSLSocketFactory(ssl);
            registryBuilder.register("https", ssl); //$NON-NLS-1$

        } catch (SecurityException ignore) {
            // ignore exception
        } catch (KeyManagementException ignore) {
            // ignore exception
        } catch (NoSuchAlgorithmException ignore) {
            // ignore exception
        }

        Registry<ConnectionSocketFactory> registry = registryBuilder.build();

        PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(registry);

        builder.setConnectionManager(ccm);
        builder.setMaxConnPerRoute(100);
        builder.setMaxConnTotal(200);
        builder.disableCookieManagement(); // DTP require clearing cookie header to be independent for each API calls - XT-37107

        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.BASIC, new BasicSchemeFactory(StandardCharsets.UTF_8))
                .register(AuthSchemes.DIGEST, new DigestSchemeFactory(StandardCharsets.UTF_8))
                .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
                .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
                .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
                .build();
        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);

        CLIENT = builder.build();
    }

    public static ProxyExecutor httpsTrustAllExecutor() {
        HttpClient client = new CloseableAndConfigurableClientImpl(CLIENT);
        return new ProxyExecutor(Executor.newInstance(client));
    }

    /**
     * Wrapper for executor that may redirect all request via proxy
     */
    public static class ProxyExecutor {
        private final Executor _executor;

        ProxyExecutor(Executor executor) {
            _executor = executor;
        }

        public ResponseContent execute(Request request, ResponseHandler<ResponseContent> handler)
                throws IOException {
            return _executor.execute(request).handleResponse(handler);
        }
    }

    public static class CloseableAndConfigurableClientImpl implements HttpClient, Closeable, Configurable {
        final HttpClient _client;

        public CloseableAndConfigurableClientImpl(HttpClient client) {
            _client = client;
        }

        @Override
        public HttpParams getParams() {
            return _client.getParams();
        }

        @Override
        public ClientConnectionManager getConnectionManager() {
            return _client.getConnectionManager();
        }

        @Override
        public HttpResponse execute(HttpUriRequest request)
                throws IOException {
            return _client.execute(request);
        }

        @Override
        public HttpResponse execute(HttpUriRequest request, HttpContext context)
                throws IOException {
            return _client.execute(request, context);
        }

        @Override
        public HttpResponse execute(HttpHost target, HttpRequest request)
                throws IOException {
            return _client.execute(target, request);
        }

        @Override
        public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
                throws IOException {
            return _client.execute(target, request, context);
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
                throws IOException {
            return _client.execute(request, responseHandler);
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
                throws IOException {
            return _client.execute(request, responseHandler, context);
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
                throws IOException {
            return _client.execute(target, request, responseHandler);
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
                throws IOException {
            return _client.execute(target, request, responseHandler, context);
        }

        @Override
        public void close()
                throws IOException {
            ((Closeable) _client).close();
        }

        /**
         * Returns actual request configuration.
         */
        @Override
        public RequestConfig getConfig() {
            return ((Configurable) _client).getConfig();
        }
    }
}
