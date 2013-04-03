package com.box.androidlibv2.sample.oauth;

import java.net.URISyntaxException;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.widget.Button;
import android.widget.TextView;

import com.box.boxandroidlibv2.BoxAndroidClient;
import com.box.boxandroidlibv2.viewlisteners.OAuthDataMessage;
import com.box.boxandroidlibv2.viewlisteners.OAuthWebViewListener;
import com.box.boxandroidlibv2.views.OAuthWebView;
import com.box.boxjavalibv2.events.OAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthEvent;
import com.box.boxjavalibv2.interfaces.IAuthFlowMessage;

/**
 * Sample OAuth App, this activity allows user to go through the OAuth flow, retrieve OAuth tokens and display on screen.
 */
public class OAuthActivity extends Activity {

    private static final String HINT = "You are not authorized yet";
    private TextView mainText;
    private OAuthWebView oauthView;
    private Button exitBtn;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        initUI();
        startOAuth();
    }

    /**
     * Initialize UI.
     * 
     * @throws URISyntaxException
     */
    private void initUI() {
        mainText = (TextView) findViewById(R.id.maintext);
        oauthView = (OAuthWebView) findViewById(R.id.oauthview);
        BoxAndroidClient boxClient = ((OAuthSampleApplication) getApplication()).getClient();
        oauthView.initializeAuthFlow(boxClient, this);

        exitBtn = (Button) findViewById(R.id.exit);

        mainText.setText(HINT);
        exitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                OAuthActivity.this.finish();
            }
        });
    }

    /**
     * Start OAuth flow.
     */
    private void startOAuth() {
        ((OAuthSampleApplication) getApplication()).getClient().authenticate(oauthView, false, getOAuthFlowListener());
    }

    /**
     * Create a listener to listen to OAuth flow events.
     * 
     * @return OAuthWebViewListener
     */
    private OAuthWebViewListener getOAuthFlowListener() {
        return new OAuthWebViewListener() {

            @Override
            public void onAuthFlowMessage(final IAuthFlowMessage message) {
                mainText.setText("message:" + message.getData());
                mainText.invalidate();
            }

            @Override
            public void onAuthFlowException(final Exception e) {
                mainText.setText("exception:" + e.getClass().getCanonicalName());
                mainText.invalidate();
            }

            @Override
            public void onAuthFlowEvent(final IAuthEvent event, final IAuthFlowMessage message) {
                if (event == OAuthEvent.OAUTH_CREATED) {
                    OAuthDataMessage msg = (OAuthDataMessage) message;
                    mainText.setText("access_token:" + msg.getData().getAccessToken() + "\nrefresh_token:" + msg.getData().getRefreshToken());
                }
                else {
                    mainText.setText("event:" + event.toString() + ", message:" + message.getData());
                }
                mainText.invalidate();
            }

            @Override
            public void onSslError(final SslErrorHandler handler, final SslError error) {
                mainText.setText("sslError:" + error.toString());
                mainText.invalidate();
                handler.proceed();
            }

            @Override
            public void onError(final int errorCode, final String description, final String failingUrl) {
                mainText.setText("error:" + description);
                mainText.invalidate();
            }

        };
    }
}
