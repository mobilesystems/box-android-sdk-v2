package com.box.boxandroidlibv2;

import com.box.boxjavalibv2.BoxConfig;

public class BoxAndroidConfig extends BoxConfig {

    /** Default User-Agent String. */
    private static final String USER_AGENT = "BoxAndroidLibraryV2";
    /** User-Agent String to use. */
    private String mUserAgent = USER_AGENT;

    private static BoxAndroidConfig config;

    private BoxAndroidConfig() {
        super();
    }

    public static BoxAndroidConfig getInstance() {
        if (config == null) {
            config = new BoxAndroidConfig();
        }
        return config;
    }

    /**
     * Set the String to use as the User-Agent HTTP header.
     * 
     * @param agent
     *            User-Agent String
     */
    @Override
    public void setUserAgent(final String agent) {
        mUserAgent = agent;
    }

    /**
     * Get the User-Agent String to apply to the HTTP(S) calls.
     * 
     * @return String to use for User-Agent.
     */
    @Override
    public String getUserAgent() {
        return mUserAgent;
    }
}
