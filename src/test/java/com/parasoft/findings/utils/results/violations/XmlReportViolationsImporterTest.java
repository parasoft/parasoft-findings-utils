package com.parasoft.findings.utils.results.violations;


import com.parasoft.findings.utils.results.testableinput.ProjectFileTestableInput;
import com.parasoft.findings.utils.results.testableinput.RemoteTestableInput;
import com.parasoft.findings.utils.results.xml.IXmlTagsAndAttributes;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class XmlReportViolationsImporterTest {

    @Test
    public void testPerformImport_not_existing_report() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "not_existing_report.xml");
        Properties properties = new Properties();
        XmlReportViolationsImporter underTest = new XmlReportViolationsImporter(properties);
        XmlReportViolations results = underTest.performImport(reportPath);

        // Then
       assertNull(results);
    }

    @Test
    public void testPerformImport_cpptest_pro_report_202001() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> violations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(2149, violations.size());
        RuleViolation ruleViolation = (RuleViolation)violations.get(0);

        validateRuleViolationAttributes(
                ruleViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.2020.1.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(1, 0, 1, 1),
                null,
                "Add comment containing the copyright information at the begin of file 'DeadLock.cpp'",
                3,
                "COMMENT",
                "Comments",
                "COMMENT-02",
                "Provide copyright information",
                "devtest",
                null,
                "cpp");

        MetricsViolation metricsViolation = (MetricsViolation)violations.get(12);
        validateRuleViolationAttributes(
                metricsViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.2020.1.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(1, 0, 2, 0),
                null,
                "Value 152 is out of acceptable range: 'lower than 50'",
                3,
                "METRIC",
                "Metric",
                "METRIC.NOPLIF",
                "Physical Lines in Files",
                "devtest",
                null,
                "cpp");

        DupCodeViolation dupCodeViolation = (DupCodeViolation)violations.get(405);
        validateRuleViolationAttributes(
                dupCodeViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.2020.1.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(73, 0, 76, 1),
                null,
                "Duplicated function: 'THREAD_RETURN_TYPE GameLogic_Thread ( void * ) { return (...'",
                2,
                "CDD",
                "Code Duplication Detection",
                "CDD-DUPM",
                "Avoid function duplication",
                "devtest",
                null,
                "cpp");
        assertEquals(2, dupCodeViolation.getPathElements().length);

        FlowAnalysisViolation flowAnalysisViolation = (FlowAnalysisViolation)violations.get(556);
        validateRuleViolationAttributes(
                flowAnalysisViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.2020.1.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(94, 0, 95, 0),
                null,
                "\"velocityArray\" is used in two critical sections in context of single method, using one critical section will improve atomicity of operation",
                1,
                "BD-TRS",
                "Threads & Synchronization",
                "BD-TRS-DIFCS",
                "Variable should be used in context of single critical section",
                "devtest",
                null,
                "cpp");
        assertEquals(12, flowAnalysisViolation.getPathElements().length);
    }

    @Test
    public void testPerformImport_cpptest_pro_report_202201() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202201.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> violations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(3207, violations.size());

        RuleViolation ruleViolation = (RuleViolation)violations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(1, 0, 1, 1),
                null,
                "Add comment containing the copyright information at the begin of file 'DeadLock.cpp'",
                3,
                "COMMENT",
                "Comments",
                "COMMENT-02",
                "Provide copyright information",
                "devtest",
                null,
                "cpp");

        MetricsViolation metricsViolation = (MetricsViolation)violations.get(12);
        validateRuleViolationAttributes(
                metricsViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(1, 0, 2, 0),
                null,
                "Value 152 is out of acceptable range: 'lower than 50'",
                3,
                "METRIC",
                "Metric",
                "METRIC.NOPLIF",
                "Physical Lines in Files",
                "devtest",
                null,
                "cpp");

        DupCodeViolation dupCodeViolation = (DupCodeViolation)violations.get(613);
        validateRuleViolationAttributes(
                dupCodeViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(73, 0, 76, 1),
                null,
                "Duplicated function: 'THREAD_RETURN_TYPE GameLogic_Thread ( void * ) { return (...'",
                2,
                "CDD",
                "Code Duplication Detection",
                "CDD-DUPM",
                "Avoid function duplication",
                "devtest",
                null,
                "cpp");
        assertEquals(2, dupCodeViolation.getPathElements().length);

        FlowAnalysisViolation flowAnalysisViolation = (FlowAnalysisViolation)violations.get(802);
        validateRuleViolationAttributes(
                flowAnalysisViolation,
                "C:\\Workspace\\jenkins_refactoring_code_workspace\\workspace\\cicd.findings.cpptest.pro.FlowAnalysisCpp.remote_docs\\DeadLock.cpp",
                new SourceRange(93, 0, 94, 0),
                null,
                "\"participants\" is used in two critical sections in context of single method, using one critical section will improve atomicity of operation",
                2,
                "BD-TRS",
                "Threads & Synchronization",
                "BD-TRS-DIFCS",
                "Variable should be used in context of single critical section",
                "devtest",
                null,
                "cpp");
        assertEquals(11, flowAnalysisViolation.getPathElements().length);
    }

    @Test
    public void testPerformImport_cpptest_std_report_202001() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "cpptest_std_report_202001.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> violations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(2428, violations.size());

        RuleViolation ruleViolation = (RuleViolation)violations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.cpptest.std.Timer.remote_docs\\clock.c",
                new SourceRange(1, 0, 1, 1),
                null,
                "The assertion density is lower than two assertions per function",
                3,
                "METRICS",
                "Metrics",
                "METRICS-31",
                "The assertion density of the code should average to a minimum of two assertions per function",
                "devtest",
                null,
                "cpp");

        DupCodeViolation dupCodeViolation = (DupCodeViolation)violations.get(41);
        validateRuleViolationAttributes(
                dupCodeViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.cpptest.std.Timer.remote_docs\\clock.c",
                new SourceRange(15, 4, 15, 23),
                null,
                "Duplicated code: 'char buf[BUF_SIZE];'",
                3,
                "CDD",
                "Code Duplication Detection",
                "CDD-DUPC",
                "Avoid code duplication",
                "devtest",
                null,
                "cpp");
        assertEquals(3, dupCodeViolation.getPathElements().length);

        FlowAnalysisViolation flowAnalysisViolation = (FlowAnalysisViolation)violations.get(1059);
        validateRuleViolationAttributes(
                flowAnalysisViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.cpptest.std.Timer.remote_docs\\timer.c",
                new SourceRange(40, 0, 41, 0),
                null,
                "Value of \"end_m\" is never used",
                2,
                "AUTOSAR-A0_1_1",
                "AUTOSAR A0-1-1 A project shall not contain instances of non-volatile variables being given values that are not subsequently used",
                "AUTOSAR-A0_1_1-a",
                "Avoid unused values",
                "devtest",
                null,
                "cpp");
        assertEquals(1, flowAnalysisViolation.getPathElements().length);
    }

    @Test
    public void testPerformImport_cpptest_std_report_202201() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "cpptest_std_report_202201.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> violations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(2431, violations.size());

        RuleViolation ruleViolation = (RuleViolation)violations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "D:\\Parasoft Findings\\VSTS\\BitBucket\\flowanalysiscpp\\NullPointer.cpp",
                new SourceRange(1, 0, 1, 1),
                null,
                "The assertion density is lower than two assertions per function",
                3,
                "METRICS",
                "Metrics",
                "METRICS-31",
                "The assertion density of the code should average to a minimum of two assertions per function",
                "renee",
                null,
                "cpp");

        MetricsViolation metricsViolation = (MetricsViolation)violations.get(604);
        validateRuleViolationAttributes(
                metricsViolation,
                "D:\\Parasoft Findings\\VSTS\\BitBucket\\flowanalysiscpp\\DeadLock.cpp",
                new SourceRange(1, 0, 2, 0),
                null,
                "Value 152 is out of acceptable range: 'lower than 50'",
                3,
                "METRIC",
                "Metric",
                "METRIC.NOPLIF",
                "Physical Lines in Files",
                "renee",
                null,
                "cpp");

        DupCodeViolation dupCodeViolation = (DupCodeViolation)violations.get(431);
        validateRuleViolationAttributes(
                dupCodeViolation,
                "D:\\Parasoft Findings\\VSTS\\BitBucket\\flowanalysiscpp\\MemoryLeak.cpp",
                new SourceRange(7, 14, 7, 18),
                null,
                "Duplicated string: '\"%d\"'",
                3,
                "CDD",
                "Code Duplication Detection",
                "CDD-DUPS",
                "Avoid string literal duplication",
                "renee",
                null,
                "cpp");
        assertEquals(2, dupCodeViolation.getPathElements().length);

        FlowAnalysisViolation flowAnalysisViolation = (FlowAnalysisViolation)violations.get(162);
        validateRuleViolationAttributes(
                flowAnalysisViolation,
                "D:\\Parasoft Findings\\VSTS\\BitBucket\\flowanalysiscpp\\NullPointer.cpp",
                new SourceRange(11, 0, 12, 0),
                null,
                "\"point\" may possibly be null",
                2,
                "AUTOSAR-A5_3_2",
                "AUTOSAR A5-3-2 Null pointers shall not be dereferenced",
                "AUTOSAR-A5_3_2-a",
                "Avoid null pointer dereferencing",
                "renee",
                null,
                "cpp");
        assertEquals(3, flowAnalysisViolation.getPathElements().length);
    }

    @Test
    public void testPerformImport_jtest_report_202001() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "jtest_report_202001.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> violations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(93, violations.size());

        RuleViolation ruleViolation = (RuleViolation)violations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.jtest.2020.1.parabank.remote_docs\\src\\main\\java\\com\\parasoft\\parabank\\dao\\internal\\DynamicDataInserter.java",
                new SourceRange(1, 0, 1, 43),
                null,
                "This source file does not include a file header comment",
                2,
                "FORMAT",
                "Formatting",
                "FORMAT.MCH",
                "Include a meaningful file header comment in every source file",
                "devtest",
                null,
                "java");
    }

    @Test
    public void testPerformImport_jtest_report_202202() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "jtest_report_202202.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> violations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(1, violations.size());
        RuleViolation ruleViolation = (RuleViolation)violations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "D:\\AzureDevOpsAgentVersions\\vsts-agent-win-x64-2.206.1\\_work\\1\\s\\src\\main\\java\\Calculator.java",
                new SourceRange(1, 0, 1, 0),
                null,
                "Compilation unit should be placed inside a non-default package",
                3,
                "CODSTA.ORG",
                "Organization",
                "CODSTA.ORG.UNDPN",
                "Ensure all types have a non default package name",
                "Roller Wang",
                "533262ab518caaa481cb7902b588dc6251cb9326",
                "java");
    }

    @Test
    public void testPerformImport_dottest_report_202001() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "dottest_report_202001.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> allViolations = importRuleViolation(reportPath, properties);
        List<IRuleViolation> suppressedViolations = filterSuppressedViolations(allViolations);

        // Then
        assertEquals(2028, allViolations.size());
        assertEquals(30, suppressedViolations.size());

        RuleViolation ruleViolation = (RuleViolation)allViolations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.dottest.2020.1.BankExample.remote_docs\\Parasoft.Dottest.Examples.Bank.Tests.NUnit\\AccountNumberTests.cs",
                new SourceRange(1, -1, 2, -1),
                null,
                "Appropriate header was found neither at the beginning nor at the end of file.",
                3,
                "BRM",
                "Better Readability and Maintainability",
                "BRM.SFH",
                "Always provide appropriate file header (copyright information, etc.)",
                "devtest",
                null,
                "dotnet");

        RuleViolation suppressedRuleViolation = (RuleViolation)allViolations.get(893);
        validateRuleViolationAttributes(
                suppressedRuleViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.dottest.2020.1.BankExample.remote_docs\\Parasoft.Dottest.Examples.Bank\\ExampleBank.cs",
                new SourceRange(19, 46, 19, 51),
                IXmlTagsAndAttributes.UNKNOWN_SUPPRESSION_TYPE,
                "Internationalize string \"USD\" in member 'CreateExampleBank' in class 'ExampleBank'.",
                3,
                "CS.INTER",
                "Internationalization",
                "CS.INTER.ITT",
                "String literals should be internationalized",
                "devtest",
                null,
                "dotnet");

        MetricsViolation metricsViolation = (MetricsViolation)allViolations.get(1);
        validateRuleViolationAttributes(
                metricsViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.dottest.2020.1.BankExample.remote_docs\\Parasoft.Dottest.Examples.Bank.Tests.NUnit\\AccountNumberTests.cs",
                new SourceRange(1, 0, 2, 0),
                null,
                "Value 10 is out of acceptable range: 'lower than 1'",
                3,
                "METRIC",
                "Metric",
                "METRIC.NOBLIF",
                "Blank Lines in Files",
                "devtest",
                null,
                "dotnet");

        DupCodeViolation dupCodeViolation = (DupCodeViolation)allViolations.get(16);
        validateRuleViolationAttributes(
                dupCodeViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.dottest.2020.1.BankExample.remote_docs\\Parasoft.Dottest.Examples.Bank.Tests.NUnit\\AccountNumberTests.cs",
                new SourceRange(15, 12, 15, 24),
                null,
                "Duplicated string: \"091a151413\"",
                3,
                "CDD",
                "Code Duplication Detection",
                "CDD.DUPS",
                "Avoid string literal duplication",
                "devtest",
                null,
                "dotnet");
        assertEquals(2, dupCodeViolation.getPathElements().length);

        FlowAnalysisViolation flowAnalysisViolation = (FlowAnalysisViolation)allViolations.get(345);
        validateRuleViolationAttributes(
                flowAnalysisViolation,
                "C:\\Workspace\\jenkins_original_code_workspace\\workspace\\cicd.findings.dottest.2020.1.BankExample.remote_docs\\Parasoft.Dottest.Examples.Bank\\AccountNumber.cs",
                new SourceRange(57, 16, 57, 72),
                null,
                "\"number\" may possibly be null",
                1,
                "CWE.476",
                "CWE-476: NULL Pointer Dereference",
                "CWE.476.NR",
                "Avoid NullReferenceException",
                "devtest",
                null,
                "dotnet");
        assertEquals(2, flowAnalysisViolation.getPathElements().length);
    }

    @Test
    public void testPerformImport_dottest_report_202201() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "dottest_report_202201.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> allViolations = importRuleViolation(reportPath, properties);
        List<IRuleViolation> suppressedViolations = filterSuppressedViolations(allViolations);

        // Then
        assertEquals(1701, allViolations.size());
        assertEquals(30, suppressedViolations.size());

        RuleViolation ruleViolation = (RuleViolation)allViolations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "D:\\DEV_FINDS\\VSTS\\BitBucket\\bankexample.net-dottest2020.1\\Parasoft.Dottest.Examples.Bank\\AccountNumber.cs",
                new SourceRange(1, -1, 2, -1),
                null,
                "Appropriate header was found neither at the beginning nor at the end of file.",
                3,
                "BRM",
                "Better Readability and Maintainability",
                "BRM.SFH",
                "Always provide appropriate file header (copyright information, etc.)",
                "CD-RLIU",
                null,
                "dotnet");

        MetricsViolation metricsViolation = (MetricsViolation)allViolations.get(1);
        validateRuleViolationAttributes(
                metricsViolation,
                "D:\\DEV_FINDS\\VSTS\\BitBucket\\bankexample.net-dottest2020.1\\Parasoft.Dottest.Examples.Bank\\AccountNumber.cs",
                new SourceRange(1, 0, 2, 0),
                null,
                "Value 8 is out of acceptable range: 'lower than 1'",
                3,
                "METRIC",
                "Metric",
                "METRIC.NOBLIF",
                "Blank Lines in Files",
                "CD-RLIU",
                null,
                "dotnet");

        DupCodeViolation dupCodeViolation = (DupCodeViolation)allViolations.get(33);
        validateRuleViolationAttributes(
                dupCodeViolation,
                "D:\\DEV_FINDS\\VSTS\\BitBucket\\bankexample.net-dottest2020.1\\Parasoft.Dottest.Examples.Bank\\AccountNumber.cs",
                new SourceRange(21, 16, 21, 26),
                null,
                "Duplicated code: int digit;",
                3,
                "CDD",
                "Code Duplication Detection",
                "CDD.DUPC",
                "Avoid code duplication",
                "CD-RLIU",
                null,
                "dotnet");
        assertEquals(2, dupCodeViolation.getPathElements().length);

        FlowAnalysisViolation flowAnalysisViolation = (FlowAnalysisViolation)allViolations.get(79);
        validateRuleViolationAttributes(
                flowAnalysisViolation,
                "D:\\DEV_FINDS\\VSTS\\BitBucket\\bankexample.net-dottest2020.1\\Parasoft.Dottest.Examples.Bank\\AccountNumber.cs",
                new SourceRange(57, 16, 57, 72),
                null,
                "\"number\" may possibly be null",
                1,
                "BD.EXCEPT",
                "Exceptions",
                "BD.EXCEPT.NR",
                "Avoid NullReferenceException",
                "CD-RLIU",
                null,
                "dotnet");
        assertEquals(2, flowAnalysisViolation.getPathElements().length);
    }

    @Test
    public void testPerformImport_soatest_report_202101_not_supported() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "soatest_report_202101.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> allViolations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(0, allViolations.size());
    }

    @Test
    public void testPerformImport_soatest_report_202201() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "soatest_report_202201.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> allViolations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(1011, allViolations.size());

        RuleViolation ruleViolation = (RuleViolation)allViolations.get(0);
        validateRuleViolationAttributes(
                ruleViolation,
                "http://192.168.3.30/loginPage",
                new SourceRange(1, 0, 1, 0),
                null,
                "java.lang.NullPointerException",
                1,
                "INTEROP",
                "Interoperability",
                "INTEROP.WSI",
                "Check WS-Interoperability",
                "liwbo",
                null, "web", RemoteTestableInput.class);
    }

    @Test
    public void testPerformImport_soatest_report_202202() {
        // Given
        File reportPath = new File("src/test/resources/xml/staticanalysis/", "soatest_report_202202.xml");
        assertTrue(reportPath.exists());
        Properties properties = new Properties();
        List<IRuleViolation> allViolations = importRuleViolation(reportPath, properties);

        // Then
        assertEquals(1011, allViolations.size());

        RuleViolation ruleViolation = (RuleViolation)allViolations.get(852);
        validateRuleViolationAttributes(
                ruleViolation,
                "http://192.168.3.30/lib/bootstrap/css/bootstrap.min.css",
                new SourceRange(5, 0, 5, 0),
                null,
                "Malformed CSS code detected \"-webkit-box-shadow:inset 0 1px 0 rgba(255,255,255,.1)\".",
                4,
                "INVC.CSS",
                "CSS",
                "INVC.CSS.WF",
                "Ensure that the CSS declaration is well-formed",
                "liwbo",
                null, "web", RemoteTestableInput.class);
    }

    private void validateRuleViolationAttributes(IRuleViolation ruleViolation, String filePath, SourceRange sourceRange,
                                                 String suppressionType, String message, int severity, String category, String categoryDescription, String ruleId,
                                                 String ruleTitle, String author, String revision, String languageId, Class... inputFileType) {
        assertEquals(suppressionType, ruleViolation.getAttribute(IXmlTagsAndAttributes.SUPPRESSION_TYPE_ATTR));
        if(inputFileType.length != 0 && inputFileType[0].equals(RemoteTestableInput.class)) {
            RemoteTestableInput fileTestableInput = (RemoteTestableInput)ruleViolation.getResultLocation().getTestableInput();
            assertEquals(filePath, fileTestableInput.getProjectRelativePath());
        } else {
            ProjectFileTestableInput fileTestableInput = (ProjectFileTestableInput)ruleViolation.getResultLocation().getTestableInput();
            assertEquals(filePath, fileTestableInput.getFileLocation().getAbsolutePath());
        }
        assertEquals(sourceRange, ruleViolation.getResultLocation().getSourceRange());
        assertEquals(message, ruleViolation.getMessage());
        assertEquals(ruleId, ruleViolation.getRuleId());
        assertEquals(severity, ViolationRuleUtil.getSeverity(ruleViolation));
        assertEquals(category, ViolationRuleUtil.getRuleCategory(ruleViolation));
        assertEquals(categoryDescription, ruleViolation.getAttribute(IXmlTagsAndAttributes.RULE_SUBCATEGORY_ATTR));
        assertEquals(ruleTitle, ViolationRuleUtil.getRuleTitle(ruleViolation));
        assertEquals(author, ruleViolation.getAttribute(IXmlTagsAndAttributes.AUTHOR_V2_ATTR));
        assertEquals(revision, ruleViolation.getAttribute(IXmlTagsAndAttributes.REVISION_ATTR));
        assertEquals(languageId, ruleViolation.getLanguageId());
    }

    private List<IRuleViolation> filterSuppressedViolations(List<IRuleViolation> ruleViolations) {
        List<IRuleViolation> violations = new ArrayList<>();
        ruleViolations.forEach((violation) -> {
            if (IXmlTagsAndAttributes.UNKNOWN_SUPPRESSION_TYPE.equals(violation.getAttribute(IXmlTagsAndAttributes.SUPPRESSION_TYPE_ATTR))) {
                violations.add(violation);
            }

        });
        return violations;
    }

    private List<IRuleViolation> importRuleViolation(File reportPath, Properties properties) {
        XmlReportViolationsImporter underTest = new XmlReportViolationsImporter(properties);
        XmlReportViolations results = underTest.performImport(reportPath);
        List<IRuleViolation> ruleViolations = new ArrayList<>();
        while (results.hasNext()) {
            IViolation result = results.next();
            if (result instanceof IRuleViolation) {
                ruleViolations.add((IRuleViolation) result);
            }
        }
        return ruleViolations;
    }

}