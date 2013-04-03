package com.box.boxandroidlibv2.viewlisteners;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;

import com.box.boxjavalibv2.interfaces.IAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthFlowListener;
import com.box.boxjavalibv2.interfaces.IAuthFlowMessage;

/**
 * Listener listening to the {@link com.box.boxandroidlibv2.views.OAuthWebView}.
 */
public abstract class OAuthWebViewListener implements IAuthFlowListener {

    @Override
    public abstract void onAuthFlowMessage(IAuthFlowMessage message);

    @Override
    public abstract void onAuthFlowException(Exception e);

    @Override
    public abstract void onAuthFlowEvent(IAuthEvent event, IAuthFlowMessage message);

    /**
     * This indicates a SSL Error, implement this method to handle this error.
     * 
     * @param handler
     *            SslErrorHandler the handler handle this error. For example, you can simply do handler.process() to ignore this error or handler.cancel() to
     *            stop web page loading.
     * @param error
     *            error details.
     */
    public abstract void onSslError(SslErrorHandler handler, SslError error);

    /**
     * This indicates the webview receives some error. Implement this method to handle properly.
     * 
     * @param errorCode
     *            error code
     * @param description
     *            description of the error
     * @param failingUrl
     *            the failing url
     */
    public abstract void onError(int errorCode, String description, String failingUrl);
}
