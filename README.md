Copyright 2013 Box, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

box-android-sdk-private
=======================

Building
--------

### Eclipse

The Android SDK depends on the [Box Java SDK][java-sdk], so you must first
import it into your workspace and make sure it builds. Import the Android SDK
second and make the Java SDK a build dependency.

### Ant

First clone the [Box Java SDK][java-sdk] and follow the instructions in its
readme on how to build it. Copy the the built BoxJavaLibraryV2.jar to
BoxAndroidLibraryV2/libs. You can then use Ant to build the project like you
would with any other Android library. The simplest way to do this is by running
`ant debug`.

### Gradle (Experimental)

There is also experimental support for Gradle, allowing you to use the SDK with
Android Studio. You must have [Gradle 1.6](http://www.gradle.org/downloads)
installed.

Before the Android SDK can be built, you must first install the [Box Java SDK
][java-sdk] to your local Maven repository. This can be done by following the
Gradle build instructions included in the Java SDK's readme.

The Android SDK also depends on the Android Support Library. Unfortunately,
telling Gradle to look for the android-support JAR directly will likely result
in dex merge conflicts if more than one project uses the support library. The
easiest way to get around this is by also installing android-support-v4.jar to
your local Maven repo. Run the following command, where $ANDROID_HOME points to
your Android SDK root (you must have Maven installed).

	mvn install:install-file \
	-Dfile=$ANDROID_HOME/extras/android/support/v4/android-support-v4.jar \
	-DgroupId=com.google.android \
	-DartifactId=support-v4 \
	-Dversion=r13 \
	-Dpackaging=jar

You can now run `gradle build` to build the SDK. However, building the library
by itself isn't very useful. To reference the SDK from another Android Gradle
project, add the following to your list of dependencies:

	dependencies {
		...
		compile project(':box-android-sdk-private:BoxAndroidLibraryV2')
	}

You can refer to the Android Gradle guide on multi project setups [here
][android-gradle].

API Calls Quickstart
--------------------

### Authenticate

Authenticate the client with OAuth. See the authentication section below for
more information.

```java
boxClient.authenticate(oAuthView, autoRefreshToken, listener);
```

### Get Default File Info

```java
BoxFile boxFile = boxClient.getFilesManager().getFile(fileId, null);
```

### Get Additional File Info

Get default file info plus its description and SHA1.

```java
BoxDefaultRequestObject requestObj =
  (new BoxDefaultRequestObject()).addField(BoxFile.FIELD_SHA1);
  	.addField(BoxFile.FIELD_DESCRIPTION);
BoxFile boxFile = boxClient.getFilesManager().getFile(fileId, requestObj);
```

### Get Folder Children

Get 30 child items, starting from the 20th one, requiring etag, description, and
name to be included.

```java
BoxFolderRequestObject requestObj = 
	BoxFolderRequestObject.getFolderItemsRequestObject(30, 20)
		.addField(BoxFolder.FIELD_NAME)
		.addField(BoxFolder.FIELD_DESCRIPTION)
		.addField(BoxFolder.FIELD_ETAG);
BoxCollection collection = 
	boxClient.getFoldersManager().getFolderItems(folderId, requestObj);
```

### Upload a New File

```java
BoxFileUploadRequestObject requestObj = 
	BoxFileUploadRequestObject.uploadFileRequestObject(parent, "name", file);
List<BoxFile> bFiles = boxClient.getFilesManager().uploadFiles(requestObj);
```

### Upload a File with a Progress Listener

```java
BoxFileUploadRequestObject requestObj = 
	BoxFileUploadRequestObject.uploadFileRequestObject(parent, "name", file)
		.setListener(listener));
List<BoxFile> bFiles = boxClient.getFilesManager().uploadFiles(requestObj);
```

### Download a File

```java
boxClient.getFilesManager().downloadFile(fileId, null);
```

### Delete a File

Delete a file, but only if the etag matches.

```java
BoxFileRequestObject requestObj =
	BoxFileRequestObject.deleteFileRequestObject().setIfMatch(etag);
boxClient.deleteFile(fileId, requestObj);
```

Authentication
--------------

You can find a full example of how to perform authentication in the sample app.

### Basic Authentication

The easiest way to authenticate is to use the OAuthActivity, which is included
in the SDK. Add it to your manifest to use it.

```java
Intent intent = OAuthActivity.createOAuthActivityIntent(this, clientId, 
	clientSecret);
startActivityForResult(intent);

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == Activity.RESULT_CANCELED) {
		// Get the error message for why authentication failed.
		String failMessage = data.getStringExtra(OAuthActivity.ERROR_MESSAGE);
		// Implement your own logic to handle the error.
	   handleFail(failMessage);
	} else {
		// You will get an authenticated BoxClient object back upon success.
		BoxClient client =
			data.getParcelableExtra(OAuthActivity.BOX_CLIENT);
		youOwnMethod(client);
	}
}
```

### Advanced Authentication

Alternatively, you can use your own custom login activity with a WebView for
authentication.

```java
oauthView = (OAuthWebView) findViewById(R.id.oauthview);
oauthView.initializeAuthFlow(boxClient, this);
boxClient.authenticate(oauthView, autoRefreshOAuth, getOAuthFlowListener());

// Create a listener listening to OAuth flow. The most important part you need
// to implement is onAuthFlowEvent and catch the OAUTH_CREATED event. This event
// indicates that the OAuth flow is done, the BoxClient is authenticated and
// that you can start making API calls. 
private OAuthWebViewListener getOAuthFlowListener() {
	return new OAuthWebViewListener() {
		@Override
		public onAuthFlowEvent(final IAuthEvent event,
			final IAuthFlowMessage message) {

			// Authentication is done, you can start using your BoxClient
			// instance.
		}
	}
}

// You can get a BoxOAuthToken and use it to authenticate the client at a later
// time or in a different activity.
BoxOAuthToken oauthObject = boxClient.getAuthData();

// Re-authenticate using the previously obtained OAuth object.
boxClient.authenticate(oauthObject);
```

[java-sdk]: https://github.com/box/box-java-sdk-private
[android-gradle]: http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Multi-project-setup
