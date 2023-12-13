package com.parasoft.findings.utils.rules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class RuleDescriptionImporterTest {

    private static final String TEST_BUILTIN_RULES_PATH = "src/test/resources/builtinRules";

    private static final String TEST_BUILTIN_RULES_2023_1_0_PATH = TEST_BUILTIN_RULES_PATH + "/2023.1.0";
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    @AfterEach
    public void tearDown() {
        Locale.setDefault(DEFAULT_LOCALE);
    }

    @Test
    public void testImportCpptestRuleDescription() throws Throwable {
        Locale.setDefault(Locale.ROOT);
        File ruleFile = new File(TEST_BUILTIN_RULES_2023_1_0_PATH, "cpptest/rules.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(ruleFile);
        assertImportCpptestRuleDescription(3828, ruleDescriptions,
                "A macro parameter immediately following a # operator shall not immediately be followed by or preceded by a ## operator");
    }

    @Test
    public void testImportCpptestRuleDescriptionInSimplifiedChinese() throws Throwable {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        File ruleFile = new File(TEST_BUILTIN_RULES_2023_1_0_PATH, "cpptest/zh_CN/rules.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(ruleFile);
        assertImportCpptestRuleDescription(4151, ruleDescriptions,
                "一个紧跟在 # 操作符的宏参数不应该在其后紧跟或在其前加上 ## 操作符");
    }

    private static void assertImportCpptestRuleDescription(final int expectedRuleDescriptionCount, final List<RuleDescription> ruleDescriptions,
                                                           final String expectedTheFirstRuleDescriptionHeader) {
        assertEquals(expectedRuleDescriptionCount, ruleDescriptions.size());
        RuleDescription firstRuleDescription = ruleDescriptions.get(0);
        assertEquals("MISRAC2012.RULE_20_11", firstRuleDescription.getCategoryId());
        assertEquals(2, firstRuleDescription.getSeverity());
        assertTrue(firstRuleDescription.toString().contains("MISRAC2012.RULE_20_11.a"));
        assertNotNull(firstRuleDescription.getBody());
        assertFalse(firstRuleDescription.getBody().getElements().isEmpty());
        assertEquals("<file path=PREPROC\\PREPROC-16.rule>", firstRuleDescription.getBody().getElements().get(0).toString());
        assertEquals(expectedTheFirstRuleDescriptionHeader, firstRuleDescription.getHeader());
    }

    @Test
    public void testImportJtestRuleDescription() throws Throwable {
        Locale.setDefault(Locale.ROOT);
        File ruleFile = new File(TEST_BUILTIN_RULES_2023_1_0_PATH, "jtest/corerules.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(ruleFile);
        assertImportJtestRuleDescription( ruleDescriptions,
                "Avoid using insecure cryptographic algorithms for data encryption with Spring");
    }

    @Test
    public void testImportJtestRuleDescriptionInSimplifiedChinese() throws Throwable {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        File ruleFile = new File(TEST_BUILTIN_RULES_2023_1_0_PATH, "jtest/corerules.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(ruleFile);
        assertImportJtestRuleDescription(ruleDescriptions,
                "避免使用不安全的加密算法对 Spring 进行数据加密");
    }

    private static void assertImportJtestRuleDescription(final List<RuleDescription> ruleDescriptions,
                                                           final String expectedTheFirstRuleDescriptionHeader) {
        assertEquals(1927, ruleDescriptions.size());
        RuleDescription firstRuleDescription = ruleDescriptions.get(0);
        assertEquals("OWASP_ASVS_403.V2_8_3", firstRuleDescription.getCategoryId());
        assertEquals(1, firstRuleDescription.getSeverity());
        assertTrue(firstRuleDescription.toString().contains("OWASP_ASVS_403.V2_8_3.AISSAJAVA"));
        assertNotNull(firstRuleDescription.getBody());
        assertFalse(firstRuleDescription.getBody().getElements().isEmpty());
        assertEquals("<messages>", firstRuleDescription.getBody().getElements().get(0).toString());
        assertEquals(expectedTheFirstRuleDescriptionHeader, firstRuleDescription.getHeader());
    }

    @Test
    public void testImportDottestRuleDescription() throws Throwable {
        Locale.setDefault(Locale.ROOT);
        File ruleFile = new File(TEST_BUILTIN_RULES_2023_1_0_PATH, "dottest/rules_builtin.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(ruleFile);
        assertImportDottestRuleDescription(ruleDescriptions,
                "Avoid 'protected' access for members of 'sealed' classes");
    }

    @Test
    public void testImportDottestRuleDescriptionInSimplifiedChinese() throws Throwable {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        File ruleFile = new File(TEST_BUILTIN_RULES_2023_1_0_PATH, "dottest/rules_builtin.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(ruleFile);
        assertImportDottestRuleDescription(ruleDescriptions,
                "避免对 sealed 类的成员进行 protected 访问。");
    }

    private static void assertImportDottestRuleDescription(final List<RuleDescription> ruleDescriptions,
                                                         final String expectedTheFirstRuleDescriptionHeader) {
        assertEquals(132, ruleDescriptions.size());
        RuleDescription firstRuleDescription = ruleDescriptions.get(0);
        assertEquals("BRM", firstRuleDescription.getCategoryId());
        assertEquals(3, firstRuleDescription.getSeverity());
        assertTrue(firstRuleDescription.toString().contains("BRM.APRIS"));
        assertNull(firstRuleDescription.getBody());
        assertEquals(expectedTheFirstRuleDescriptionHeader, firstRuleDescription.getHeader());
    }

    @Test
    public void testImportRuleDescriptionWhenRulesFileNotFound() throws Throwable {
        File notExistRuleFile = new File(TEST_BUILTIN_RULES_PATH, "not-exist-rules-file.xml");
        List<RuleDescription> ruleDescriptions = new RuleDescriptionImporter().performImport(notExistRuleFile);
        assertTrue(ruleDescriptions.isEmpty());
    }

    @Test
    public void testImportRuleDescriptionWhenRulesFileIsInvalid() {
        File invalidRulesFile = new File(TEST_BUILTIN_RULES_PATH, "invalid-rules-file.xml");
        Exception exception = assertThrows(IOException.class, () ->
                new RuleDescriptionImporter().performImport(invalidRulesFile));
        assertTrue(exception.getMessage().contains("Error while parsing document: "));
    }
}