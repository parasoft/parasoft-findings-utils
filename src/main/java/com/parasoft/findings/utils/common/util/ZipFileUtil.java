package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.IStringConstants;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipFileUtil {
    private static String ruleDocZipFilePath;

    /**
     * Private constructor to prevent instantiation.
     */
    private ZipFileUtil() {
    }

    /**
     * Read the contents of a rule document file from a provided zip file
     *
     * @param filePathInZip  the file path in the zip file
     * @param sEncoding      encoding file, example: UTF-8
     * @return file contents
     * @throws IOException
     */
    public static String readRuleDocFileInZip(String filePathInZip, String sEncoding) throws IOException {
        try (ZipFile zipFile = new ZipFile(ruleDocZipFilePath)){
            ZipEntry entry = zipFile.getEntry(filePathInZip);
            if (entry == null) {
                return IStringConstants.EMPTY;
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
     * @param zipFilePath the absolute path of the zip file
     * @param ruleId      rule id
     * @return the rule file path in the specified zip file
     */
    public static String getRuleDocFileLocationInZip(String zipFilePath, String ruleId) {
        List<String> baseDirs = new ArrayList<>(Arrays.asList("doc", "docs"));
        Map<String, String> languageSubdirMap = new HashMap<>();
        languageSubdirMap.put(Locale.CHINESE.getLanguage(), "zh_CN/");
        languageSubdirMap.put(Locale.JAPANESE.getLanguage(), "ja/");

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // Find the rule doc directory (doc or docs) in the zip
            String ruleDocDir = baseDirs.stream()
                    .map(zipFile::getEntry)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(ZipEntry::getName)
                    .orElse(IStringConstants.EMPTY);

            // Find the localization directory based on language environment
            String localeLanguage = Locale.getDefault().getLanguage();
            String localizationDir = languageSubdirMap.getOrDefault(localeLanguage, "");
            return ruleDocDir + localizationDir + ruleId + ".html";
        } catch (IOException e) {
            // Zip file path errors or permission issues
            Logger.getLogger().error(e.getMessage());
            return null;
        }
    }

    public static void setDocZipFilePath(String ruleDocZipFilePath) {
        ZipFileUtil.ruleDocZipFilePath = ruleDocZipFilePath;
    }
}
