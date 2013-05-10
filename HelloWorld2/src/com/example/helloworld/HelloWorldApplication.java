package com.example.helloworld;

import com.box.boxandroidlibv2.BoxAndroidClient;

import android.app.Application;


public class HelloWorldApplication extends Application{

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
