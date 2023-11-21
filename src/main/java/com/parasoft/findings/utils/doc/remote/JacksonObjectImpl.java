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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonObjectImpl {
    private ObjectNode _jacksonImpl = null;

    public JacksonObjectImpl(String source) throws JSONException {
        try {
            _jacksonImpl = (ObjectNode) JSON.getObjectMapper().readTree(source);
        } catch (JsonProcessingException | ClassCastException e) {
            throw new JSONException(e);
        }
    }

    public String getString(String key) throws JSONException {
        JsonNode jackson = getJackson(key);
        if (jackson.isTextual()) {
            return jackson.textValue();
        }
        throw new JSONException("Not a string-like value: " + jackson); //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return _jacksonImpl.toString();
    }

    private JsonNode getJackson(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key."); //$NON-NLS-1$
        } else {
            JsonNode value = _jacksonImpl.get(key);
            if (value == null) {
                throw new JSONException("No value for property " + key); //$NON-NLS-1$
            } else {
                return value;
            }
        }
    }

    private static class JSON {
        private static final ObjectMapper _objectMapper = new ObjectMapper();

        static ObjectMapper getObjectMapper() {
            return _objectMapper;
        }

        static {
            //allow single quotes
            _objectMapper.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature());
            //allow jakson field names without quotes
            //_objectMapper.enable(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature());
            _objectMapper.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature());
            //allow comments
            _objectMapper.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature());
            //allow non-numeric numbers
            _objectMapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
            //allow missing values
            _objectMapper.enable(JsonReadFeature.ALLOW_MISSING_VALUES.mappedFeature());
            //do not convert empty element in end of array into object
            _objectMapper.enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature());
        }
    }

}
