package com.box.boxandroidlibv2.viewdata;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;

import com.box.boxjavalibv2.authorization.OAuthDataController;
import com.box.restclientv2.httpclientsupport.HttpClientURIBuilder;
import com.box.restclientv2.httpclientsupport.HttpClientURLEncodedUtils;

/**
 * Data on the OAuth WebView.
 */
public class OAuthWebViewData {

    public static final String CODE_RESPONSE = "code";
    private static final String REDIRECT_URL = "https://cloud.box.com/platform/";
    private static final String RESPONSE_TYPE = "code";
    private final OAuthDataController mOAuthDataController;
    private String mOptionalState;

    /**
     * Constructor.
     * 
     * @param redirectUrl
     *            redirection url
     * @param clientId
     *            client id
     * @param clientSecret
     *            client secret
     * @param responseType
     *            response type, currently only supports {@link #CODE_RESPONSE}
     * @param scheme
     *            scheme for oauth
     * @param host
     *            host for oauth
     */
    public OAuthWebViewData(final OAuthDataController oAuthDataController) {
        this.mOAuthDataController = oAuthDataController;
    }

    /**
     * @return the optionalState
     */
    public String getOptionalState() {
        return mOptionalState;
    }

    /**
     * @param optionalState
     *            the optionalState to set
     */
    public void setOptionalState(final String optionalState) {
        this.mOptionalState = optionalState;
    }

    /**
     * @return the redirectUrl
     */
    public String getRedirectUrl() {
        return REDIRECT_URL;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return mOAuthDataController.getClientId();
    }

    /**
     * @return the responseType
     */
    public String getResponseType() {
        return RESPONSE_TYPE;
    }

    /**
     * @return the scheme
     */
    public String getScheme() {
        return mOAuthDataController.getScheme();
    }

    /**
     * @return the host
     */
    public String getHost() {
        return mOAuthDataController.getAuthority();
    }

    public String getUrlPath() {
        return mOAuthDataController.getUrlPath();
    }

    /**
     * @return the client secret
     */
    public String getClientSecret() {
        return mOAuthDataController.getClientSecret();
    }

    /**
     * build the oauth URI.
     * 
     * @return URI uri
     * @throws URISyntaxException
     *             exception
     */
    public URI buildUrl() throws URISyntaxException {
        HttpClientURIBuilder ub = new HttpClientURIBuilder(getUrlPath());
        ub.setHost(getHost());
        ub.setScheme(getScheme());
        ub.addParameter("response_type", getResponseType());
        ub.addParameter("client_id", getClientId());
        if (StringUtils.isNotEmpty(getOptionalState())) {
            ub.addParameter("state", getOptionalState());
        }
        HttpClientURLEncodedUtils.format(ub.getQueryParams(), "UTF-8");
        return ub.build();
    }
}
