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
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import com.parasoft.findings.utils.common.nls.IntlUtil;
import com.parasoft.findings.utils.common.util.IOUtils;

/**
 * Rule extensions of common internationalization system.
 */
public class RuleIntlUtil
{
    /**
     * Dumps the contents of the "corresponding" internationalized strings file
     * to the given properties object,
     * @param structureUrl
     * @param props
     * @param locale {@link Locale} to be used
     * @param bUseDefault If <code>true</code> - default properties file will be used to get
     *  internationalized strings in case if file for given locale not found.
     *
     * @pre locale != null
     * @pre props != null
     */
    static void initIntl(final URL structureUrl, Properties props, Locale locale, boolean bUseDefault)
    {
        if (structureUrl == null) {
            return;
        }

        initIntl(new IIntlBundle() {
            public String getBundlePath()
            {
                String path = structureUrl.getPath();
                return path.substring(0, path.length() - 4);
            }

            public InputStream getIntlFileStream(String sIntlFilename)
            {
                try {
                    return new URL(structureUrl.getProtocol(), structureUrl.getHost(),
                        structureUrl.getPort(), sIntlFilename).openStream();
                } catch (IOException e) {
                    Logger.getLogger().error("Failed to init internationalized strings file process.", e);
                    return null;
                }
            }

        }, props, locale, bUseDefault);
    }

    /**
     * Dumps the contents of the "corresponding" internationalized strings file
     * to the given properties object,
     * @param bundle
     * @param props
     * @param locale {@link Locale} to be used
     * @param bUseDefault If <code>true</code> - default properties file will be used to get
     *  internationalized strings in case if file for given locale not found.
     *
     * @pre bundle != null
     * @pre props != null
     * @pre locale != null
     */
    private static void initIntl(IIntlBundle bundle, Properties props, Locale locale, boolean bUseDefault)
    {
        String[] asSuffixes = IntlUtil.getIntlSuffixes(locale, DEFAULT_RULES_INTL_EXTENSION, bUseDefault);
        for (String asSuffixe : asSuffixes) {
            String sIntlFilename = bundle.getBundlePath() + asSuffixe;
            if (initIntl(bundle, props, sIntlFilename)) {
                return;
            }
        }
    }

    private static boolean initIntl(IIntlBundle bundle, Properties props, String sIntlFilename)
    {
        InputStream is = bundle.getIntlFileStream(sIntlFilename);
        if (is == null) {
            return false;
        }
        try {
            props.load(is);
            return true;
        } catch (IOException x) {
            Logger.getLogger().error("Failed to init internationalized strings file. False will be returned.", x);
            return false;
        } finally {
            IOUtils.close(is);
        }
    }

    /**
     * Represents intl files bundle.
     */
    public interface IIntlBundle
    {
        /**
         * @return Path to bundle.
         *
         * For instance, for intl file "/some/path/messages_en.properties"
         * bundle path will be "/some/path/messages".
         *
         * @post $result != null
         */
        String getBundlePath();

        /**
         * @param sIntlFilename Full name of intl file
         * @return {@link InputStream} for intl file with given name
         *  or null if file doesn't exists.
         * @pre sIntlFilename != null
         */
        InputStream getIntlFileStream(String sIntlFilename);
    }

    public static void internationalize(RuleDescription rule, Properties intlProps)
    {
        IntlPropertiesApplier applier = new IntlPropertiesApplier(intlProps);
        applier.apply(rule);
    }

    public static abstract class IntlPropertiesProcessor
    {
        protected final Properties _props;

        protected String _sRuleId = null;

        protected String _sRadioGroupId = null;

        protected String _sComplexParamTableId = null;

        protected String _sChildTableId = null;

        protected String _sRowId = null;

        private IntlPropertiesProcessor(Properties props)
        {
            _props = props;
        }

        public void process(RuleDescription ruleDescription)
        {
            _sRuleId = ruleDescription.getRuleId();

            RuleDescriptionBody body = ruleDescription.getBody();
            if (body != null) {
                for (RuleDescriptionBody.BodyElement element : body.getElements()) {
                    processElement(element);
                }
            }
            _sRuleId = null;
        }

        private void processElement(RuleDescriptionBody.BodyElement element)
        {
            startElement(element);

            for (RuleDescriptionBody.BodyElement child : element.getChildren()) {
                processElement(child);
            }

            endElement(element);
        }

        private void startElement(RuleDescriptionBody.BodyElement element)
        {
            if (IRuleConstants.MSG_TAG.equals(element.getName())) {
                String sIndex = element.getAttributes().get(IRuleConstants.INDEX_ATTR);
                processProperty(element, IRuleConstants.VALUE_ATTR, "{0}.msg.{1}", _sRuleId, sIndex); //$NON-NLS-1$

            } else if (IRuleConstants.PARAM_TAG.equals(element.getName()) ||
                IRuleConstants.GROUP_TAG.equals(element.getName()) ||
                IRuleConstants.RADIO_GROUP_TAG.equals(element.getName()) ||
                IRuleConstants.COMPLEX_PARAM_TABLE_TAG.equals(element.getName()) ||
                IRuleConstants.ROW_TAG.equals(element.getName()))
            {
                String sId = element.getAttributes().get(IRuleConstants.ID_ATTR);

                processProperty(element, IRuleConstants.LABEL_ATTR, "{0}-label", sId); //$NON-NLS-1$
                processProperty(element, IRuleConstants.DESCRIPTION_ATTR, "{0}-description", sId); //$NON-NLS-1$

                if (IRuleConstants.RADIO_GROUP_TAG.equals(element.getName())) {
                    _sRadioGroupId = sId;
                } else if (IRuleConstants.COMPLEX_PARAM_TABLE_TAG.equals(element.getName())) {
                    _sComplexParamTableId = sId;
                } else if (IRuleConstants.ROW_TAG.equals(element.getName())) {
                    _sRowId = sId;
                }

                if (IRuleConstants.PARAM_TAG.equals(element.getName())) {
                    processParam(element);
                }

            } else if (IRuleConstants.RADIO_ITEM_TAG.equals(element.getName())) {
                String sValue = element.getAttributes().get(IRuleConstants.VALUE_ATTR);
                processProperty(element, IRuleConstants.LABEL_ATTR, "{0}.{1}-label", _sRadioGroupId, sValue); //$NON-NLS-1$
                processProperty(element, IRuleConstants.DESCRIPTION_ATTR, "{0}.{1}-description", _sRadioGroupId, sValue); //$NON-NLS-1$

            } else if (IRuleConstants.CHILD_TABLE_TAG.equals(element.getName())) {
                String sId = element.getAttributes().get(IRuleConstants.ID_ATTR);
                processProperty(element, IRuleConstants.LABEL_ATTR, "{0}.{1}-label", _sComplexParamTableId, sId); //$NON-NLS-1$
                processProperty(element, IRuleConstants.DESCRIPTION_ATTR, "{0}.{1}-description", _sComplexParamTableId, sId); //$NON-NLS-1$
                _sChildTableId = sId;

            } else if (IRuleConstants.COLUMN_TAG.equals(element.getName())) {
                String sId = element.getAttributes().get(IRuleConstants.ID_ATTR);
                String sRefColumnId = element.getAttributes().get(IRuleConstants.REF_COLUMN_INDEX_ATTR);

                if (sRefColumnId == null) {
                    if (_sChildTableId != null) {
                        processProperty(element, IRuleConstants.LABEL_ATTR, "{0}.{1}.{2}-label", _sComplexParamTableId, _sChildTableId, sId); //$NON-NLS-1$
                        processProperty(element, IRuleConstants.DESCRIPTION_ATTR, "{0}.{1}.{2}-description", _sComplexParamTableId, _sChildTableId, sId); //$NON-NLS-1$
                    } else {
                        processProperty(element, IRuleConstants.LABEL_ATTR, "{0}.{1}-label", _sComplexParamTableId, sId); //$NON-NLS-1$
                        processProperty(element, IRuleConstants.DESCRIPTION_ATTR, "{0}.{1}-description", _sComplexParamTableId, sId); //$NON-NLS-1$
                    }
                } else if (sRefColumnId.equals(IRuleConstants.NAME_ATTR)) {
                    if (_sChildTableId != null) {
                        processProperty(element, IRuleConstants.VALUE_ATTR, "{0}.{1}-row.{2}", _sComplexParamTableId, _sChildTableId, _sRowId); //$NON-NLS-1$
                    } else {
                        processProperty(element, IRuleConstants.VALUE_ATTR, "{0}-row.{1}", _sComplexParamTableId, _sRowId); //$NON-NLS-1$
                    }
                }
            }
        }

        private void endElement(RuleDescriptionBody.BodyElement element)
        {
            if (IRuleConstants.RADIO_GROUP_TAG.equals(element.getName())) {
                _sRadioGroupId = null;
            } else if (IRuleConstants.CHILD_TABLE_TAG.equals(element.getName())) {
                _sChildTableId = null;
            } else if (IRuleConstants.ROW_TAG.equals(element.getName())) {
                _sRowId = null;
            }
        }

        protected abstract void processProperty(RuleDescriptionBody.BodyElement element, String attributeId, String intlPropertyPattern, Object...args);

        protected void processParam(RuleDescriptionBody.BodyElement paramElement)
        {
        }

    }

    private static class IntlPropertiesApplier extends IntlPropertiesProcessor
    {
        public IntlPropertiesApplier(Properties props)
        {
            super(props);
        }

        public void apply(RuleDescription ruleDescription)
        {
            String sHeader = ruleDescription.getHeader();

            if (sHeader != null) {
                _props.setProperty(ruleDescription.getRuleId(), sHeader);
            }
            process(ruleDescription);
        }

        protected void processProperty(RuleDescriptionBody.BodyElement element, String attributeId, String intlPropertyPattern, Object...args)
        {
            if (!element.getAttributes().containsKey(attributeId)) {
                return;
            }

            for (Object arg : args) {
                if (arg == null) {
                    return;
                }
            }

            String key = MessageFormat.format(intlPropertyPattern, args);
            String sNewValue = _props.getProperty(key);
            if (sNewValue != null) {
                element.addAttribute(attributeId, sNewValue);
            }
        }

        protected void processParam(RuleDescriptionBody.BodyElement paramElement)
        {
            String sName = paramElement.getAttributes().get(IRuleConstants.NAME_ATTR);
            String sKey = MessageFormat.format("{0}.param.{1}", _sRuleId, sName); //$NON-NLS-1$
            if (sKey != null) {
                String sValue = _props.getProperty(sKey);
                if (sValue != null) {
                    paramElement.addAttribute(IRuleConstants.VALUE_ATTR, sValue);
                }
            }
        }
    }

    private static final String DEFAULT_RULES_INTL_EXTENSION = ".properties"; //$NON-NLS-1$
}
