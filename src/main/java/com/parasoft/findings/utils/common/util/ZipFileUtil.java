package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.IStringConstants;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public final class ZipFileUtil {
    /**
     * Private constructor to prevent instantiation.
     */
    private ZipFileUtil() {
    }

    /**
     * Reads file contents from the specified zip file
     *
     * @param zipFilePath  absolute path of the zip file
     * @param ruleId       the rule id of rule file read in the zip file
     * @return file content
     * @throws IOException
     */
    public static String readRuleDocFileInZip(String zipFilePath, String ruleId) throws IOException {
        List<String> BASE_DIRS = new ArrayList<>(Arrays.asList("doc", "docs"));
        Map<String, String> LANGUAGE_SUBDIR_MAP = new HashMap<>();
        LANGUAGE_SUBDIR_MAP.put(Locale.CHINESE.getLanguage(), "zh_CN/");
        LANGUAGE_SUBDIR_MAP.put(Locale.JAPANESE.getLanguage(), "ja/");

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // Find the rule doc directory (doc or docs) in the zip
            String ruleDocDir = BASE_DIRS.stream()
                    .map(zipFile::getEntry)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(ZipEntry::getName)
                    .orElse(IStringConstants.EMPTY);

            // Find the localization directory based on language environment
            String localeLanguage = Locale.getDefault().getLanguage();
            String localizationDir = LANGUAGE_SUBDIR_MAP.getOrDefault(localeLanguage, "");

            String filePathInZip = localizationDir + ruleDocDir + ruleId + ".html";
            return readFileInZip(zipFile, filePathInZip);
        }
    }

    /**
     * Reads file contents from a provided zip file
     *
     * @param zipFile        specified zip file
     * @param filePathInZip  the file path in the zip file
     * @return file contents
     * @throws IOException
     */
    public static String readFileInZip(ZipFile zipFile, String filePathInZip) throws IOException {
        ZipEntry entry = zipFile.getEntry(filePathInZip);
        if (entry == null) {
            return IStringConstants.EMPTY;
        }

        try (InputStream inputStream = zipFile.getInputStream(entry);
             InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader)) {
            return FileUtil.readFile(reader);
        }
    }

    /**
     * Search the zip file in the specified directory path
     *
     * @param docRootPath the directory path for searching documentation zip file
     * @return the absolute path of rule documentation zip file
     */
    public static String getDocZipFileInDir(String docRootPath) {
        String[] zipNames = {"doc.zip", "docs.zip"};

        for (String name : zipNames) {
            File zipFile = new File(docRootPath, name);
            if (zipFile.exists()) {
                return zipFile.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * Check whether the specified file is a zip file
     *
     * @param filePath the specified file path
     * @return the checked result
     */
    public static boolean isZipFile(String filePath) {
        try {
            new ZipFile(filePath).close();
            return true;
        } catch (ZipException e) {
            // Not Zip format file
            return false;
        } catch (IOException e) {
            // Zip file path errors or permission issues
            Logger.getLogger().debug(e.getMessage());
            return false;
        }
    }
}
