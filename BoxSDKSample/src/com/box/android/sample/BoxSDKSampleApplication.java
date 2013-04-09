package com.box.android.sample;

import android.app.Application;

import com.box.boxandroidlibv2.BoxAndroidClient;

/**
 * The application class which contains a singleton instance of BoxAndroidClient.
 */
public class BoxSDKSampleApplication extends Application {

    // TODO: use your own client settings
    public static final String CLIENT_ID = "";
    public static final String CLIENT_SECRET = "";

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
