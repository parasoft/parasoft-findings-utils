package com.parasoft.findings.utils.doc;

import com.parasoft.findings.utils.common.IStringConstants;
import com.parasoft.findings.utils.common.util.FileUtil;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class RuleDocZipReader {
    private final String ruleDocZipFilePath;

    public RuleDocZipReader(String ruleDocZipFilePath) {
        this.ruleDocZipFilePath = ruleDocZipFilePath;
    }

    /**
     * Read the contents of a rule document file from a provided zip file
     *
     * @param filePathInZip  the file path in the zip file
     * @param sEncoding      encoding file, example: UTF-8
     * @return file contents
     * @throws IOException
     */
    public String readRuleDocFileInZip(String filePathInZip, String sEncoding) throws IOException {
        try (ZipFile zipFile = new ZipFile(this.ruleDocZipFilePath)) {
            ZipEntry entry = zipFile.getEntry(filePathInZip);
            if (entry == null) {
                Logger.getLogger().warn(filePathInZip + " could not be found in zip file: " + filePathInZip);
                return IStringConstants.EMPTY;
            }

            if (sEncoding == null || sEncoding.trim().isEmpty()) {
                sEncoding = "UTF-8";
            }

            try (InputStream inputStream = zipFile.getInputStream(entry);
                 InputStreamReader streamReader = new InputStreamReader(inputStream, sEncoding);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                return FileUtil.readFile(reader);
            }
        }
    }

    /**
     * Get the rule doc file location in the specified zip file
     *
     * @param ruleId  rule file name
     * @return the rule file path in the specified zip file
     */
    public String getRuleDocFileLocationInZip(String ruleId) {
        List<String> baseDirs = new ArrayList<>(Arrays.asList("doc", "docs"));
        Map<String, String> languageSubdirMap = new HashMap<>();
        languageSubdirMap.put(Locale.CHINESE.getLanguage(), "zh_CN/");
        languageSubdirMap.put(Locale.JAPANESE.getLanguage(), "ja/");

        try (ZipFile zipFile = new ZipFile(this.ruleDocZipFilePath)) {
            // Find the rule doc directory (doc or docs) in the zip
            String ruleDocDir = baseDirs.stream()
                    .map(zipFile::getEntry)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(ZipEntry::getName)
                    .orElse(IStringConstants.EMPTY);

            if (ruleDocDir.isEmpty()) {
               Logger.getLogger().warn("Rule document directory could not be found in zip file: " + this.ruleDocZipFilePath);
               return ruleDocDir;
            }

            // Find the localization directory based on language environment
            String localeLanguage = Locale.getDefault().getLanguage();
            String localizationDir = languageSubdirMap.getOrDefault(localeLanguage, "");
            return ruleDocDir + localizationDir + ruleId + ".html";
        } catch (IOException e) {
            // Zip file path errors or permission issues
            Logger.getLogger().error("Error while reading rule doc file in: " + e.getMessage(), e);
            return null;
        }
    }
}
