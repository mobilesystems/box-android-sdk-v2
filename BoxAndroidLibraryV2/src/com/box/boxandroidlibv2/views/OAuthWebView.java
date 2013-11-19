package com.box.boxandroidlibv2.views;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.box.boxandroidlibv2.exceptions.BoxAndroidLibException;
import com.box.boxandroidlibv2.viewdata.OAuthWebViewData;
import com.box.boxandroidlibv2.viewlisteners.OAuthDataMessage;
import com.box.boxandroidlibv2.viewlisteners.OAuthWebViewListener;
import com.box.boxandroidlibv2.viewlisteners.StringMessage;
import com.box.boxjavalibv2.BoxClient;
import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.events.OAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthFlowListener;
import com.box.boxjavalibv2.interfaces.IAuthFlowMessage;
import com.box.boxjavalibv2.interfaces.IAuthFlowUI;
import com.box.boxjavalibv2.requests.requestobjects.BoxOAuthRequestObject;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.httpclientsupport.HttpClientURIBuilder;

/**
 * A WebView used for OAuth flow.
 */
public class OAuthWebView extends WebView implements IAuthFlowUI {

    private boolean allowShowingRedirectPage = true;

    private OAuthWebViewData mWebViewData;

    private OAuthWebViewClient mWebClient;

    /**
     * Constructor.
     * 
     * @param context
     *            context
     * @param attrs
     *            attrs
     */
    public OAuthWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set the state, this is optional.
     * 
     * @param optionalState
     *            state
     */
    public void setOptionalState(final String optionalState) {
        mWebViewData.setOptionalState(optionalState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initializeAuthFlow(final BoxClient boxClient, final Object activity) {
        this.mWebViewData = new OAuthWebViewData(boxClient.getOAuthDataController());
        mWebClient = new OAuthWebViewClient(mWebViewData, (Activity) activity, boxClient);
        mWebClient.setAllowShowRedirectPage(allowShowRedirectPage());
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(mWebClient);
    }

    @Override
    public void authenticate(IAuthFlowListener listener) {
        mWebClient.addListener(listener);

        try {
            loadUrl(mWebViewData.buildUrl().toString());
        }
        catch (URISyntaxException e) {
            if (listener != null) {
                listener.onAuthFlowException(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.webkit.WebView#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        mWebClient.destroy();
    }

    /**
     * @return the allowShowingRedirectPage
     */
    public boolean allowShowRedirectPage() {
        return allowShowingRedirectPage;
    }

    /**
     * @param allowShowingRedirectPage
     *            the allowShowingRedirectPage to set
     */
    public void setAllowShowingRedirectPage(boolean allowShowingRedirectPage) {
        this.allowShowingRedirectPage = allowShowingRedirectPage;
    }

    /**
     * WebViewClient for the OAuth WebView.
     */
    private static class OAuthWebViewClient extends WebViewClient {

        private BoxClient mBoxClient;
        private final OAuthWebViewData mwebViewData;
        private boolean allowShowRedirectPage = true;

        private final List<IAuthFlowListener> mListeners = new ArrayList<IAuthFlowListener>();

        /**
         * Constructor.
         * 
         * @param webViewData
         *            data
         * @param listener
         *            listener
         * @param activity
         *            activity hosting this webview
         */
        public OAuthWebViewClient(final OAuthWebViewData webViewData, final Activity activity, final BoxClient boxClient) {
            super();
            this.mwebViewData = webViewData;
            this.mBoxClient = boxClient;
        }

        public void addListener(final IAuthFlowListener listener) {
            this.mListeners.add(listener);
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            for (IAuthFlowListener listener : mListeners) {
                if (listener != null) {
                    listener.onAuthFlowEvent(OAuthEvent.PAGE_STARTED, new StringMessage(StringMessage.MESSAGE_URL, url));
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String code = null;
            try {
                code = getResponseValueFromUrl(url);
            }
            catch (URISyntaxException e) {
                fireExceptions(e);
            }
            if (StringUtils.isNotEmpty(code)) {
                for (IAuthFlowListener listener : mListeners) {
                    if (listener != null) {
                        listener.onAuthFlowMessage(new StringMessage(mwebViewData.getResponseType(), code));
                    }
                }
                startCreateOAuth(code);
                if (!allowShowRedirectPage()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            fireEvents(OAuthEvent.PAGE_FINISHED, new StringMessage(StringMessage.MESSAGE_URL, url));
        }

        /**
         * Start to create OAuth after getting the code.
         * 
         * @param config
         *            config
         * @param code
         *            code
         */
        private void startCreateOAuth(final String code) {
            AsyncTask<Null, Null, BoxOAuthToken> task = new AsyncTask<Null, Null, BoxOAuthToken>() {

                @Override
                protected BoxOAuthToken doInBackground(final Null... params) {
                    BoxOAuthToken oauth = null;
                    try {
                        oauth = mBoxClient.getOAuthManager().createOAuth(
                            BoxOAuthRequestObject.createOAuthRequestObject(code, mwebViewData.getClientId(), mwebViewData.getClientSecret(),
                                mwebViewData.getRedirectUrl()));
                    }
                    catch (Exception e) {
                        oauth = null;
                    }
                    return oauth;
                }

                @Override
                protected void onPostExecute(final BoxOAuthToken result) {
                    if (result != null) {
                        try {
                            fireEvents(OAuthEvent.OAUTH_CREATED, new OAuthDataMessage(result));
                        }
                        catch (BoxRestException e) {
                            fireExceptions(new BoxAndroidLibException(e));
                        }
                    }
                    else {
                        fireExceptions(new BoxAndroidLibException());
                    }
                }
            };
            task.execute();
        }

        @Override
        public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
            for (IAuthFlowListener listener : mListeners) {
                if (listener != null && listener instanceof OAuthWebViewListener) {
                    ((OAuthWebViewListener) listener).onError(errorCode, description, failingUrl);
                }
            }
        }

        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
            boolean sslErrorHandled = false;
            for (IAuthFlowListener listener : mListeners) {
                if (listener != null && listener instanceof OAuthWebViewListener) {
                    ((OAuthWebViewListener) listener).onSslError(handler, error);
                    sslErrorHandled = true;
                }
            }
            
            if(!sslErrorHandled) {
                // Quick fix of the bug that no OAuthWebViewListener is actually provided to the WebView/Client but only an IAuthFlowListener (the BoxClient itself)
                view.clearSslPreferences();
                handler.cancel();
                fireExceptions(new IOException(error.toString()));
            }
        }

        /**
         * Destroy.
         */
        public void destroy() {
            mListeners.clear();
            mBoxClient = null;
        }

        /**
         * Get response value.
         * 
         * @param url
         *            url
         * @return response value
         * @throws URISyntaxException
         *             exception
         */
        private String getResponseValueFromUrl(final String url) throws URISyntaxException {
            HttpClientURIBuilder builder = new HttpClientURIBuilder(url);
            List<NameValuePair> query = builder.getQueryParams();
            for (NameValuePair pair : query) {
                if (pair.getName().equalsIgnoreCase(mwebViewData.getResponseType())) {
                    return pair.getValue();
                }
            }
            return null;
        }

        private void fireExceptions(Exception e) {
            for (IAuthFlowListener listener : mListeners) {
                if (listener != null) {
                    listener.onAuthFlowException(e);
                }
            }
        }

        private void fireEvents(IAuthEvent event, IAuthFlowMessage message) {
            for (IAuthFlowListener listener : mListeners) {
                if (listener != null) {
                    listener.onAuthFlowEvent(event, message);
                }
            }
        }

        /**
         * @return the allowShowRedirectPage
         */
        public boolean allowShowRedirectPage() {
            return allowShowRedirectPage;
        }

        /**
         * @param allowShowRedirectPage
         *            the allowShowRedirectPage to set
         */
        public void setAllowShowRedirectPage(boolean allowShowRedirectPage) {
            this.allowShowRedirectPage = allowShowRedirectPage;
        }
    }

}
