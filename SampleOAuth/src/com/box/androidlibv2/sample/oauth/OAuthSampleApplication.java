package com.box.androidlibv2.sample.oauth;

import android.app.Application;

import com.box.boxandroidlibv2.BoxAndroidClient;

public class OAuthSampleApplication extends Application {

    private static final String CLIENT_ID = "vipzeyjh3g4s7phlzwjyvm79jfxkt4ga";
    private static final String CLIENT_SECRET = "szBTd8Q86KgEJNgJgeOPDEEHdWY8Nrkr";

    private BoxAndroidClient client;

    public BoxAndroidClient getClient() {
        if (client == null) {
            client = new BoxAndroidClient(CLIENT_ID, CLIENT_SECRET);
        }
        return client;
    }
}
