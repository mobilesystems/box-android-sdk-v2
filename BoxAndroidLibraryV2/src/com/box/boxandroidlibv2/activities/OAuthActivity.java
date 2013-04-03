package com.box.boxandroidlibv2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;

import com.box.boxandroidlibv2.BoxAndroidClient;
import com.box.boxandroidlibv2.R;
import com.box.boxandroidlibv2.viewlisteners.OAuthWebViewListener;
import com.box.boxandroidlibv2.views.OAuthWebView;
import com.box.boxjavalibv2.events.OAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthFlowMessage;

/**
 * Activity for OAuth. Use this activity by using the intent from createOAuthActivityIntent method. On completion, this activity will put the parcelable
 * BoxAndroidClient into activity result. In case of failing, the activity result will be {@link Activity#RESULT_CANCELED} together will a error message in
 * intent extra.
 */
public class OAuthActivity extends Activity {

    public static final String ERROR_MESSAGE = "exception";
    public static final String BOX_CLIENT = "boxAndroidClient";

    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    private OAuthWebView oauthView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        String clientId = getIntent().getStringExtra(CLIENT_ID);
        String clientSecret = getIntent().getStringExtra(CLIENT_SECRET);
        startOAuth(clientId, clientSecret);
    }

    /**
     * Start oauth flow.
     * 
     * @param clientId
     * @param clientSecret
     */
    private void startOAuth(final String clientId, final String clientSecret) {
        oauthView = (OAuthWebView) findViewById(R.id.oauthview);
        BoxAndroidClient boxClient = new BoxAndroidClient(clientId, clientSecret);
        oauthView.initializeAuthFlow(boxClient, this);
        boxClient.authenticate(oauthView, false, getOAuthFlowListener(boxClient));
    }

    /**
     * Create a listener to listen to OAuth flow events.
     * 
     * @param boxClient
     * 
     * @return OAuthWebViewListener
     */
    private OAuthWebViewListener getOAuthFlowListener(final BoxAndroidClient boxClient) {
        return new OAuthWebViewListener() {

            @Override
            public void onAuthFlowException(final Exception e) {
                Intent intent = new Intent();
                intent.putExtra(ERROR_MESSAGE, e.getMessage());
                OAuthActivity.this.setResult(RESULT_CANCELED, intent);
                finish();
            }

            @Override
            public void onAuthFlowEvent(final IAuthEvent event, final IAuthFlowMessage message) {
                if (event == OAuthEvent.OAUTH_CREATED) {
                    Intent intent = new Intent();
                    intent.putExtra(BOX_CLIENT, boxClient);
                    OAuthActivity.this.setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onSslError(final SslErrorHandler handler, final SslError error) {
                handler.proceed();
            }

            @Override
            public void onError(final int errorCode, final String description, final String failingUrl) {
                Intent intent = new Intent();
                intent.putExtra(ERROR_MESSAGE, description);
                OAuthActivity.this.setResult(RESULT_CANCELED, intent);
                finish();
            }

            @Override
            public void onAuthFlowMessage(IAuthFlowMessage message) {
            }

        };
    }

    /**
     * Create intent to launch OAuthActivity
     * 
     * @param context
     *            context
     * @param clientId
     *            your box client id
     * @param clientSecret
     *            your box client secret
     * @return
     */
    public static Intent createOAuthActivityIntent(final Context context, final String clientId, final String clientSecret) {
        Intent intent = new Intent(context, OAuthActivity.class);
        intent.putExtra(CLIENT_ID, clientId);
        intent.putExtra(CLIENT_SECRET, clientSecret);
        return intent;
    }
}
