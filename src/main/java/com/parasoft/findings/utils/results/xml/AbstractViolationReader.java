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

import java.util.Collections;
import java.util.Map;

import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.common.util.XMLUtil;
import com.parasoft.findings.utils.results.location.IResultLocationsReader;
import com.parasoft.findings.utils.results.testableinput.ITestableInput;
import com.parasoft.findings.utils.results.testableinput.ITestableInputLocationMatcher;
import com.parasoft.findings.utils.results.testableinput.PathInput;
import com.parasoft.findings.utils.results.violations.IResult;
import com.parasoft.findings.utils.results.violations.IViolation;
import com.parasoft.findings.utils.results.violations.ResultLocation;
import com.parasoft.findings.utils.results.violations.LocationsException;
import org.xml.sax.SAXException;

import com.parasoft.findings.utils.results.violations.SourceRange;
import com.parasoft.findings.utils.results.violations.ViolationRuleUtil;

public abstract class AbstractViolationReader
        extends AbstractResultReader
        implements IViolationSAXReader {
    private RulesImportHandler _rulesImportHandler = null;

    private boolean _bReadCompleted = false;

    private String _sOriginalAuthor = null;

    private IViolation _readViolation = null;

    private String _sAnalyzerId = null;

    private boolean _bLegacySupport = false;

    protected AbstractViolationReader(boolean bLegacySupport) {
        _bLegacySupport = bLegacySupport;
    }

    /**
     * Default constructor.
     *
     * @return IResult
     */
    @Override
    public IResult getReadResult() {
        return getReadViolation();
    }

    @Override
    public IViolation getReadViolation() {
        if ((_readViolation == null) && _bReadCompleted) {
            _readViolation = createViolation();
        }
        return _readViolation;
    }

    /**
     * Creates the violation.
     *
     * @return violation or <code>null</code> for none (when failed)
     */
    private IViolation createViolation() {
        final Map<String, String> map = getMap();

        // analyzer identifier
        _sAnalyzerId = readAnalyzerId();
        if (_sAnalyzerId == null) {
            Logger.getLogger().warn("Violation with missed analyzer info ignored."); //$NON-NLS-1$
            return null;
        }

        String sLanguageId = getString(IXmlTagsAndAttributes.LANGUAGE_ATTR);

        ResultLocation location = null;
        try {
            location = getLocation();
        } catch (LocationsException le) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during violation location obtaining don't cause the process to fail."
            Logger.getLogger().warn("Failed to read violation's location."); //$NON-NLS-1$
        }
        if (location == null) {
            return null;
        }
        final IViolation violation = createViolation(map, _sAnalyzerId, sLanguageId, location);
        if (violation == null) {
            return null;
        }

        _sOriginalAuthor = getString(IXmlTagsAndAttributes.AUTHOR_V2_ATTR);
        if (_sOriginalAuthor != null) {
            violation.addAttribute(IXmlTagsAndAttributes.AUTHOR_V2_ATTR, _sOriginalAuthor);
        }

        //additional location information
        String sLocationHash = getString(IXmlTagsAndAttributes.RESOURCE_HASH_ATTR);
        if (StringUtil.isNonEmpty(sLocationHash)) {
            violation.addAttribute(IXmlTagsAndAttributes.LOCATION_HASH_ATTR, sLocationHash);
        }
        String lineHash = readLineHash();
        if (StringUtil.isNonEmpty(lineHash)) {
            violation.addAttribute(IXmlTagsAndAttributes.LINE_HASH_ATTR, lineHash);
        }

        // rule information
        String sRuleId = getString(IXmlTagsAndAttributes.RULE_ATTR);
        if (sRuleId != null) {
            if (_rulesImportHandler != null) {
                RuleAttributes ruleAttributes = _rulesImportHandler.getRuleAttributes(sRuleId);
                if (ruleAttributes != null) {
                    violation.addAttribute(IXmlTagsAndAttributes.RULE_CATEGORY_ATTR, ruleAttributes.getRuleCategory());
                    violation.addAttribute(IXmlTagsAndAttributes.RULE_SUBCATEGORY_ATTR,
                            _rulesImportHandler.getCategoryDescription(ruleAttributes.getRuleCategory()));

                    violation.addAttribute(IXmlTagsAndAttributes.RULE_HEADER_ATTR, ruleAttributes.getRuleHeader());
                    violation.addAttribute(IXmlTagsAndAttributes.RULE_SCOPE_ATTR, ruleAttributes.getRuleScope());
                } else {
                    Logger.getLogger().warn("Rule not found in rule list"); //$NON-NLS-1$
                }
            } else {
                String sRuleHeader = getString(IXmlTagsAndAttributes.RULE_HEADER_ATTR);
                if (StringUtil.isNonEmpty(sRuleHeader)) {
                    violation.addAttribute(IXmlTagsAndAttributes.RULE_HEADER_ATTR, sRuleHeader);
                }

                String sRuleSubcategory = getString(IXmlTagsAndAttributes.RULE_SUBCATEGORY_ATTR);
                if (StringUtil.isNonEmpty(sRuleSubcategory)) {
                    violation.addAttribute(IXmlTagsAndAttributes.RULE_SUBCATEGORY_ATTR, sRuleSubcategory);
                }
            }
        }

        // source control information
        String sRevision = getString(IXmlTagsAndAttributes.REVISION_ATTR);
        if ((sRevision != null) && !"unknown.revision".equals(sRevision)) {
            long lRevisionTime = getLong(IXmlTagsAndAttributes.REVISION_TIME_ATTR);
            String sComment = getString(IXmlTagsAndAttributes.REVISION_COMMENT_ATTR);

            violation.addAttribute(IXmlTagsAndAttributes.REVISION_ATTR, sRevision);
            violation.addAttribute(IXmlTagsAndAttributes.REVISION_TIME_ATTR, String.valueOf(lRevisionTime));
            if (sComment != null) {
                violation.addAttribute(IXmlTagsAndAttributes.REVISION_COMMENT_ATTR, sComment);
            }
        }

        // is suppressed?
        readSuppressionInfo(violation);

        if (containsKey(IXmlTagsAndAttributes.SEVERITY_SHORT_ATTR)) {
            ViolationRuleUtil.setSeverity(violation, getInt(IXmlTagsAndAttributes.SEVERITY_SHORT_ATTR));
        }

        int testConfigId = getInt(IXmlTagsAndAttributes.TEST_CONFIG_ATTR, 0);
        if (testConfigId > 0) {
            violation.addAttribute(IXmlTagsAndAttributes.TEST_CONFIG_ATTR,
                    Integer.toString(testConfigId));
        }

        return violation;
    }

    private void readSuppressionInfo(IViolation violation) {
        if (!getBoolean(IXmlTagsAndAttributes.SUPPRESSED_ATTR)) {
            return;
        }
        violation.addAttribute(IXmlTagsAndAttributes.SUPPRESSION_TYPE_ATTR, IXmlTagsAndAttributes.UNKNOWN_SUPPRESSION_TYPE);
        return;
    }

    protected String readLineHash() {
        return _bLegacySupport ? getString(IXmlTagsAndAttributes.LINE_HASH_V1_ATTR)
                : getString(IXmlTagsAndAttributes.LINE_HASH_ATTR);
    }

    protected String readAnalyzerId() {
        return _bLegacySupport ? getString(IXmlTagsAndAttributes.TOOL_ATTR) : getString(IXmlTagsAndAttributes.ANALYZER_ATTR);
    }

    @Override
    protected ResultLocation getLocation(Map<String, String> attributesMap, String sLocPrefix, String sLocRefAttr,
                                         boolean bAcceptModified)
            throws LocationsException {
        return _bLegacySupport ?
                getLegacyLocation(attributesMap, sLocPrefix) : super.getLocation(attributesMap, sLocPrefix, sLocRefAttr, bAcceptModified);
    }

    protected ResultLocation getLegacyLocation(Map<String, String> attributesMap, String sLocPrefix) {
        String locationPathAttr = sLocPrefix + IXmlTagsAndAttributes.FILE_TAG;
        final String path = attributesMap.get(locationPathAttr);

        if (path == null) {
            return null;
        }
        IResultLocationsReader locReader = getLocationsReader();
        ITestableInput input = null;

        String sHash = attributesMap.get(IXmlTagsAndAttributes.RESOURCE_HASH_ATTR);
        long hash = parseLong(sHash, 0);
        try {
            input = locReader.getTestableInput(path, false);
            if (input == null) {
                input = new PathInput(path);
            }
            ITestableInputLocationMatcher locationMatcher = locReader.getLocationMatcher();
            input = locationMatcher.matchLocation(input, Collections.singletonList(hash), null, null, false);
        } catch (LocationsException e) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during legacy location obtaining don't cause the process to fail."
            Logger.getLogger().warn(e.getMessage());
        }
        if (input == null) {
            return null;
        }
        SourceRange sourceRange = getLegacySourceRange(attributesMap, sLocPrefix);
        return new ResultLocation(input, sourceRange);
    }

    private SourceRange getLegacySourceRange(Map<String, String> attributesMap, String sLocPrefix) {
        int startOffset = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_START_POSITION_V2_POSTFIX, -1, attributesMap);
        int endOffset;
        if (startOffset != -1) {
            endOffset = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_END_POSITION_V2_POSTFIX, -1, attributesMap);
            int startLine = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_START_LINE_V2_POSTFIX, -1, attributesMap);
            int line;
            if (startLine != -1) {
                line = XMLUtil.getInt(sLocPrefix + IXmlTagsAndAttributes.LOCATION_END_LINE_V2_POSTFIX, -1, attributesMap);
                return new SourceRange(startLine, startOffset, line, endOffset);
            } else {
                line = XMLUtil.getInt(IXmlTagsAndAttributes.LINE_NUMBER_V2_ATTR, -1, attributesMap);
                return new SourceRange(line, startOffset, line + 1, endOffset);
            }
        } else {
            endOffset = XMLUtil.getInt(IXmlTagsAndAttributes.LINE_NUMBER_V2_ATTR, -1, attributesMap);
            return new SourceRange(endOffset, 0);
        }
    }

    @Override
    public void endElement(String sUri, String sLocalName, String sQName)
            throws SAXException {
        super.endElement(sUri, sQName, sQName);
        if (getElementTagQName().equals(sQName)) {
            _bReadCompleted = true;
        }
    }

    public void setRulesImportHandler(RulesImportHandler rulesImportHandler) {
        _rulesImportHandler = rulesImportHandler;
    }

    /**
     * Returns the qualified name of the element tag.
     *
     * @return qualified name of the element tag
     * @post $result != null
     */
    protected abstract String getElementTagQName();

    protected abstract IViolation createViolation(Map<String, String> map, String sAnalyzerId, String sLanguageId, ResultLocation location);

    /**
     * Utility method to parse long value, which avoid NumberFormatException. If exception
     * thrown, return the value that you pass in the method
     *
     * @param sValue
     * @param defaultValue return value if exception thrown
     * @return the long value represented by the argument or default_value if string doesn't
     * contain a parsable long.
     */
    private long parseLong(String sValue, long defaultValue) {
        if (StringUtil.isEmpty(sValue)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(sValue);
        } catch (Exception exc) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during long value parsing don't cause the process to fail."
            Logger.getLogger().info("Could not parse long value from: " + sValue); //$NON-NLS-1$
        }
        return defaultValue;
    }
}