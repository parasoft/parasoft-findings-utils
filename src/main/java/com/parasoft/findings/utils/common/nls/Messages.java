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

    /** Only be used for unit test */
    public static String MESSAGE_MISSING;

    /** Only be used for unit test */
    public static String TEST_ONE_VARIABLE;

    /** Only be used for unit test */
    public static String TEST_ONE_VARIABLE_WITH_QUOTES;

    /** Only be used for unit test */
    public static String TEST_ONE_VARIABLE_WITH_QUOTES_INVALID_1;

    /** Only be used for unit test */
    public static String TEST_ONE_VARIABLE_WITH_QUOTES_INVALID_2;

    /** Only be used for unit test */
    public static String TEST_ONE_VARIABLE_INVALID;

    /** Only be used for unit test */
    public static String TEST_TWO_VARIABLE;

    /** Only be used for unit test */
    public static String TEST_TWO_VARIABLE_REVERSE;
}
