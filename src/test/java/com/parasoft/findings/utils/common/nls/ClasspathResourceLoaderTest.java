package com.parasoft.findings.utils.common.nls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClasspathResourceLoaderTest {
    ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(Messages.class);

    @Test
    public void testGetResourceAndPathIsNull() {
        assertNull(resourceLoader.getResource(null));
    }

    @Test
    public void testWhenResourceAndPathIsNotNull() {
        assertNotNull(resourceLoader.getResource("/com/parasoft/findings/utils/common/nls/res/messages.properties"));
    }
}
