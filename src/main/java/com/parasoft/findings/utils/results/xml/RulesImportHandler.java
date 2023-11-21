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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.parasoft.findings.utils.common.util.IntegerUtil;

public class RulesImportHandler {
    private final Map<String, RuleAttributes> _rulesMap = new HashMap<String, RuleAttributes>();

    private final Map<String, String> _categoriesMap = new HashMap<String, String>();

    public RuleAttributes getRuleAttributes(String ruleId) {
        return _rulesMap.get(ruleId);
    }

    public String getCategoryDescription(String categoryId) {
        return _categoriesMap.get(categoryId);
    }

    public void addRule(String ruleId, RuleAttributes ruleAttributes) {
        _rulesMap.put(ruleId, ruleAttributes);
    }

    public void addCategory(String categoryId, String categoryDesc) {
        _categoriesMap.put(categoryId, categoryDesc);
    }

    public ContentHandler getRulesSAXReader() {
        return new RulesReader();
    }

    private final class RulesReader
            extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes)
                throws SAXException {
            if (IXmlTagsAndAttributes.RULES_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.RULES_LIST_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.RULE_TAG.equals(qName)) {
                String category = attributes.getValue(IXmlTagsAndAttributes.RULE_CAT_ID_ATTR);
                String description = attributes.getValue(IXmlTagsAndAttributes.RULE_DESC_ATTR);
                String ruleId = attributes.getValue(IXmlTagsAndAttributes.RULE_ID_ATTR);

                if ((category == null) || (description == null) || (ruleId == null)) {
                    throw new SAXException(IResultSAXReader.ATTRIBUTE_MISSING);
                }

                RuleAttributes ruleAttributes = new RuleAttributes(category, description, "line");
                addRule(ruleId, ruleAttributes);
            } else if (IXmlTagsAndAttributes.CATEGORIES_LIST_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.RULES_CATEGORY_TAG.equals(qName)) {
                String categoryId = attributes.getValue(IXmlTagsAndAttributes.CATEGORY_ID_ATTR);
                String categoryDesc = attributes.getValue(IXmlTagsAndAttributes.CATEGORY_DESC_ATTR);

                if ((categoryId == null) || (categoryDesc == null)) {
                    throw new SAXException(IResultSAXReader.ATTRIBUTE_MISSING);
                }
                addCategory(categoryId, categoryDesc);
            } else if (IXmlTagsAndAttributes.SEVERITY_LIST_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.SEVERITY_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.DISTR_STATS_TAG.equals(qName)) {
                // nothing to do
            } else {
                throw new SAXException(IResultSAXReader.ILLEGAL_TAG_MESSAGE);
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (IXmlTagsAndAttributes.RULES_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.RULES_LIST_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.RULE_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.CATEGORIES_LIST_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.RULES_CATEGORY_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.SEVERITY_LIST_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.SEVERITY_TAG.equals(qName)) {
                // nothing to do
            } else if (IXmlTagsAndAttributes.DISTR_STATS_TAG.equals(qName)) {
                // nothing to do
            } else {
                throw new SAXException(IResultSAXReader.ILLEGAL_TAG_MESSAGE);
            }
        }
    }
}
