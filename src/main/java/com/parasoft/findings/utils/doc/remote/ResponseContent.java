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

import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


//Inspired by org.apache.http.client.fluent.Content
public class ResponseContent {

    public static final ResponseContent NO_CONTENT = new ResponseContent(new byte[]{}, ContentType.DEFAULT_BINARY);

    private final byte[] _abRaw;
    private final ContentType _type;

    ResponseContent(final byte[] raw, final ContentType type) {
        _abRaw = raw;
        _type = type;
    }

    public byte[] asBytes() {
        if (_abRaw != null) {
            return _abRaw.clone();
        }
        return new byte[0];
    }

    public String asString() {
        Charset charset = _type.getCharset();
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        byte[] ab = asBytes();
        try {
            return new String(ab, charset.name());
        } catch (UnsupportedEncodingException ex) {
            // ignore exception
            return new String(ab);
        }
    }

    @Override
    public String toString() {
        return asString();
    }

}