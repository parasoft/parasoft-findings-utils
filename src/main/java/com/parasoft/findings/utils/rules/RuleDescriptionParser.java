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

package com.parasoft.findings.utils.rules;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.parasoft.findings.utils.common.util.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class RuleDescriptionParser
{
    private static final char _separator = '.';

    private final Locale _locale;
    private final List<RuleDescription> _rules = new ArrayList<RuleDescription>();

    private RuleParserUtil.RuleParsingOptions _options = new RuleParserUtil.RuleParsingOptions();

    public RuleDescriptionParser()
    {
        this(Locale.getDefault());

    }

    public RuleDescriptionParser(Locale locale)
    {
        _locale = locale;
    }

    public void parseFile(URL file)
        throws IOException
    {
        Logger.getLogger().debug("parsing file:" + file.toString()); //$NON-NLS-1$
        Properties intlHeaders = getIntlRuleHeaders(file, _locale);
        RuleFileHandler handler = createHandler(intlHeaders);
        RuleParserUtil.saxParse(file, handler, _options);

        handlerDone(handler);
    }

    protected RuleFileHandler createHandler(Properties intlHeaders)
    {
        return new RuleFileHandler(intlHeaders);
    }

    protected void handlerDone(RuleFileHandler handler)
    {
        Collection<RuleDescription> rules = handler.getRules();

        _rules.addAll(rules);
    }

    public List<RuleDescription> getRules()
    {
        return _rules;
    }

    private static Properties getIntlRuleHeaders(URL rulesUrl, Locale locale)
    {
        Properties result = new Properties();
        RuleIntlUtil.initIntl(rulesUrl, result, locale, true);
        return result;
    }

    /**
     * SAX parser handler for rules xml.
     * You must call all reset() before reusing same handler for more than one xml file.
     */
    protected static class RuleFileHandler
        extends DefaultHandler
    {
        private final Properties _intlHeaders;

        private final Map<String, RuleDescription> _rules = new HashMap<String, RuleDescription>();

        private final Stack<CategoryDescription> _categoryStack = new Stack<CategoryDescription>();
        private RuleDescription _currentRule = null;
        private RuleDescriptionBody _currentBody = null;

        private final Stack<RuleDescriptionBody.BodyElement> _bodyElementsStack = new Stack<RuleDescriptionBody.BodyElement>();

        public RuleFileHandler(Properties intlHeaders)
        {
            _intlHeaders = intlHeaders;
        }

        private Collection<RuleDescription> getRules()
        {
            return _rules.values();
        }

        /**
         * @see DefaultHandler#startElement(String, String, String, Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            if (IRuleConstants.RULE_TAG.equals(qName)) {
                _currentRule = handleRule(attributes);
                if (_currentRule != null) {
                    _currentBody = new RuleDescriptionBody();
                }
            } else if (IRuleConstants.CATEGORY_TAG.equals(qName)) {
                handleCategory(attributes);
            } else if (_currentBody != null) {
                RuleDescriptionBody.BodyElement element = new RuleDescriptionBody.BodyElement(qName);
                int count = attributes.getLength();
                for (int i = 0; i < count; i++) {
                    element.addAttribute(attributes.getQName(i), attributes.getValue(i));
                }

                if (!_bodyElementsStack.isEmpty()) {
                    RuleDescriptionBody.BodyElement parent = _bodyElementsStack.peek();
                    parent.addChild(element);
                } else {
                    _currentBody.getElements().add(element);
                }
                _bodyElementsStack.push(element);
            }
        }

        /**
         * @see DefaultHandler#endElement(String, String, String)
         */
        @Override
        public void endElement(String uri, String localName, String qName)
        {
            if (IRuleConstants.RULE_TAG.equals(qName)) {
                if (_currentRule != null) {
                    if (!_currentBody.isEmpty()) {
                        _currentRule.setBody(_currentBody);
                    }
                    RuleIntlUtil.internationalize(_currentRule, _intlHeaders);
                    _currentRule = null;
                    _currentBody = null;
                }
            } else if (IRuleConstants.CATEGORY_TAG.equals(qName)) {
                _categoryStack.pop();
            } else if (_currentBody != null) {
                if (!_bodyElementsStack.isEmpty()) {
                    _bodyElementsStack.pop();
                }
            }
        }

        private void handleCategory(Attributes attributes)
        {
            String sId = attributes.getValue(IRuleConstants.NAME_ATTR);

            CategoryDescription parent = null;
            if (!_categoryStack.isEmpty()) {
                parent = _categoryStack.peek();
            }
            String sCategoryId = getFullId(parent, sId);

            CategoryDescription category = new CategoryDescription(sCategoryId);

            _categoryStack.push(category);
        }

        private RuleDescription handleRule(Attributes attributes)
        {
            CategoryDescription parent = _categoryStack.peek();
            if (parent == null) {
                Logger.getLogger().warn("Missing parent category for rule: " + attributes.getValue(IRuleConstants.ID_ATTR)); //$NON-NLS-1$
                return null;
            }

            RuleDescription rule = new RuleDescription();
            rule.setSeparator(_separator);

            int count = attributes.getLength();
            for (int i = 0; i < count; i++) {
                String sKey = attributes.getQName(i);
                String sValue = attributes.getValue(i);

                if (IRuleConstants.ID_ATTR.equals(sKey)) {
                    String fullId = getFullId(parent, sValue);
                    if (_rules.containsKey(fullId)) {
                        Logger.getLogger().info("Skipping rule, same id already exists: " + fullId); //$NON-NLS-1$
                        return null;
                    }
                    rule.setRuleId(fullId);
                    handleUnknownCategory(rule);

                } else if (IRuleConstants.HEADER_ATTR.equals(sKey)) {
                    rule.setHeader(sValue);

                } else if (IRuleConstants.SEVERITY_ATTR.equals(sKey)) {

                    try {
                        int severity = Integer.parseInt(attributes.getValue(IRuleConstants.SEVERITY_ATTR));
                        rule.setSeverity(severity);
                    } catch (NumberFormatException x) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during parsing severity value don't cause the build to fail."
                        Logger.getLogger().warn(x);
                    }
                } else {
                    rule.addAttribute(sKey, sValue);
                }
            }
            if (rule.getRuleId() == null) {
                Logger.getLogger().warn("Missing rule identifier!");  //$NON-NLS-1$
                return null;
            }
            String sIntlHeader = _intlHeaders.getProperty(rule.getRuleId());
            if (sIntlHeader != null) {
                rule.setHeader(sIntlHeader);
            }

            _rules.put(rule.getRuleId(), rule);
            return rule;
        }

        private String getFullId(CategoryDescription parent, String id)
        {
            if (parent == null) {
                return id;
            }
            return parent.getCategoryId() + _separator + id;
        }
    }

    private static void handleUnknownCategory(RuleDescription rule)
    {
        String ruleId = rule.getRuleId();
        if(StringUtil.isNonEmptyTrimmed(ruleId) && ruleId.startsWith(CategoryDescription.UNKNOWN_CATEGORY + rule.getSeparator())) {
            rule.setRuleId(ruleId.substring(1 + ruleId.indexOf(rule.getSeparator())));
        }
    }
}
