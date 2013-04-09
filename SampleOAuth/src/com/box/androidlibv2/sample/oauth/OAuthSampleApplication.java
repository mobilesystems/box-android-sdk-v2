package com.box.androidlibv2.sample.oauth;

import android.app.Application;

import com.box.boxandroidlibv2.BoxAndroidClient;

public class OAuthSampleApplication extends Application {

    // TODO: use your own client settings
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";

    private BoxAndroidClient client;

    public BoxAndroidClient getClient() {
        if (client == null) {
            client = new BoxAndroidClient(CLIENT_ID, CLIENT_SECRET);
        }
        return client;
    }
}
