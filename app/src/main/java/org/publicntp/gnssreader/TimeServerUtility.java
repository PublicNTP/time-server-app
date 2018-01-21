package org.publicntp.gnssreader;


public class TimeServerUtility {

    private static final String CONTACT_EMAIL_ADDR = "contact@publicntp.org";
    private static final String CONTACT_EMAIL_SUBJ = "PublicNTP Inquiry";

    private static final String DEVELOPER_WEB_ADDR = "https://roosterglue.com";
    private static final String DEVELOPER_NAME = "Rooster Glue, Inc.";
    private static final String OWNER_WEB_ADDR = "https://publicntp.org";
    private static final String OWNER_NAME = "PublicNTP";


    public static String getContactEmailAddr() {
        return CONTACT_EMAIL_ADDR;
    }

    public static String getContactEmailSubj() {
        return CONTACT_EMAIL_SUBJ;
    }

    public static String getDeveloperWebAddr() {
        return DEVELOPER_WEB_ADDR;
    }

    public static String getDeveloperName() {
        return DEVELOPER_NAME;
    }

    public static String getOwnerWebAddr() {
        return OWNER_WEB_ADDR;
    }

    public static String getOwnerName() {
        return OWNER_NAME;
    }
}
