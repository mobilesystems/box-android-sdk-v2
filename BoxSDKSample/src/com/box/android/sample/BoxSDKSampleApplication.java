package com.box.android.sample;

import android.app.Application;

import com.box.boxandroidlibv2.BoxAndroidClient;

/**
 * The application class which contains a singleton instance of BoxAndroidClient.
 */
public class BoxSDKSampleApplication extends Application {

    public static final String CLIENT_ID = "vipzeyjh3g4s7phlzwjyvm79jfxkt4ga";
    public static final String CLIENT_SECRET = "szBTd8Q86KgEJNgJgeOPDEEHdWY8Nrkr";

    private BoxAndroidClient mClient;

    public void setClient(BoxAndroidClient client) {
        this.mClient = client;
    }

    /**
     * Gets the BoxAndroidClient for this app.
     * 
     * @return a singleton instance of BoxAndroidClient.
     */
    public BoxAndroidClient getClient() {
        return mClient;
    }

}
