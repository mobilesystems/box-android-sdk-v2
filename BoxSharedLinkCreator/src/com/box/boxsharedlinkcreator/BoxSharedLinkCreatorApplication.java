package com.box.boxsharedlinkcreator;

import android.app.Application;

import com.box.boxandroidlibv2.BoxAndroidClient;

public class BoxSharedLinkCreatorApplication extends Application {

    private BoxAndroidClient mClient;

    public void setClient(BoxAndroidClient client) {
        this.mClient = client;
    }

    public void clearClient() {
        mClient = null;
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
