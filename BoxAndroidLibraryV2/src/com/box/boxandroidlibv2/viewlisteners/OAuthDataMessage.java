package com.box.boxandroidlibv2.viewlisteners;

import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.utils.Utils;
import com.box.restclientv2.exceptions.BoxRestException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A message for OAuthData.
 */
public class OAuthDataMessage extends StringMessage {

    public static final String OAUTH_DATA_MESSAGE_KEY = "oauth_data";

    /**
     * Constructor.
     * 
     * @param oauthData
     *            OAuthData
     * @throws BoxRestException
     *             excetption
     */
    public OAuthDataMessage(final BoxOAuthToken oauthData) throws BoxRestException {
        super(OAUTH_DATA_MESSAGE_KEY, oauthData.toJSONString(new ObjectMapper()));
    }

    /**
     * Get the OAuthData out from the value(String).
     * 
     * @return OAuthData
     */
    @Override
    public BoxOAuthToken getData() {
        return (BoxOAuthToken) Utils.parseJSONStringIntoObject((String) super.getData(), BoxOAuthToken.class);
    }
}
