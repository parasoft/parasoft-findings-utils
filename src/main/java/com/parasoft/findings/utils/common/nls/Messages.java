package com.parasoft.findings.utils.common.nls;

public class Messages extends NLS {

    static {
        // initialize resource bundle
        NLS.initMessages(Messages.class);
    }

    private Messages()
    {}

    /** Only be used for unit test */
    public static String TEST;

}
