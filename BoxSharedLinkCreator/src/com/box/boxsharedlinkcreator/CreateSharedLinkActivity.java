package com.box.boxsharedlinkcreator;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.box.boxandroidlibv2.BoxAndroidClient;
import com.box.boxandroidlibv2.activities.FilePickerActivity;
import com.box.boxandroidlibv2.activities.OAuthActivity;
import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidOAuthData;
import com.box.boxjavalibv2.dao.BoxSharedLinkAccess;
import com.box.boxjavalibv2.requests.requestobjects.BoxFileRequestObject;
import com.box.boxjavalibv2.requests.requestobjects.BoxSharedLinkRequestObject;
import com.box.boxjavalibv2.utils.Utils;
import com.box.restclientv2.exceptions.BoxSDKException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateSharedLinkActivity extends Activity {

    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String AUTH = "auth";
    private static final String EXCEPTION = "exception";
    private static final String SHARED_LINK = "sharedLink";

    private final static int AUTHENTICATE_REQUEST = 101;
    private final static int PICK_FILE_REQUEST = 102;

    private final static String STATE = "state";
    private final static int STATE_FIRST_LAUNCH = 1;
    private final static int STATE_CREATED = 2;
    private final static int STATE_AUTHENTICATED = 3;
    private final static int STATE_FILE_PICKED = 4;
    private final static int STATE_CREATING_SHARED_LINK = 5;

    private int state = STATE_FIRST_LAUNCH;

    private final static int DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String clientId = getIntent().getStringExtra(CLIENT_ID);
        String clientSecret = getIntent().getStringExtra(CLIENT_SECRET);
        if (StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(clientSecret)) {
            state = STATE_FIRST_LAUNCH;
        }
        else if (savedInstanceState != null) {
            state = savedInstanceState.getInt(STATE);
        }

        if (state == STATE_FIRST_LAUNCH) {
            String auth = getIntent().getStringExtra(AUTH);
            // state == 0 if it's not in the savedInstanceState
            handleAuth(clientId, clientSecret, auth);
        }
        else if (state == STATE_CREATING_SHARED_LINK) {
            showDialog(DIALOG_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE, state);
        super.onSaveInstanceState(outState);
    }

    private void handleAuth(String clientId, String clientSecret, String auth) {
        state = STATE_CREATED;
        if (StringUtils.isNotEmpty(auth)) {
            getSharedLinkCreatorApplication().setClient(createClient(clientId, clientSecret, auth));
            startFilePicker();
        }
        else {
            startOAuth(clientId, clientSecret);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result:" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AUTHENTICATE_REQUEST:
                onAuthenticated(resultCode, data);
                break;
            case PICK_FILE_REQUEST:
                onFileSelected(resultCode, data);
                break;
            default:
        }
    }

    private void onFileSelected(int resultCode, Intent data) {
        state = STATE_FILE_PICKED;
        if (resultCode == Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        }
        else {
            BoxAndroidFile bFile = data.getParcelableExtra(FilePickerActivity.EXTRA_BOX_ANDROID_FILE);
            createSharedLinkAndFinish(bFile);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Creating shared link...");
        return dialog;
    }

    private void createSharedLinkAndFinish(final BoxAndroidFile bFile) {
        new AsyncTask<Null, Void, String>() {

            @Override
            protected void onPreExecute() {
                state = STATE_CREATING_SHARED_LINK;
                CreateSharedLinkActivity.this.showDialog(DIALOG_ID);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Null... params) {
                try {
                    BoxAndroidClient client = getSharedLinkCreatorApplication().getClient();
                    BoxFileRequestObject createSharedLinkObject = BoxFileRequestObject.createSharedLinkRequestObject(BoxSharedLinkRequestObject
                        .createSharedLinkRequestObject(BoxSharedLinkAccess.OPEN));
                    BoxAndroidFile resultFile = (BoxAndroidFile) client.getFilesManager().createSharedLink(bFile.getId(), createSharedLinkObject);
                    String sharedLink = null;
                    if (resultFile != null && resultFile.getSharedLink() != null) {
                        sharedLink = resultFile.getSharedLink().getUrl();
                    }
                    return sharedLink;
                }
                catch (BoxSDKException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Intent intent = new Intent();
                int resultCode = Activity.RESULT_OK;
                if (StringUtils.isEmpty(result)) {
                    resultCode = Activity.RESULT_CANCELED;
                }
                else {
                    try {
                        intent.putExtra(AUTH, getSharedLinkCreatorApplication().getClient().getAuthData().toJSONString(new ObjectMapper()));
                        intent.putExtra(SHARED_LINK, result);
                    }
                    catch (Exception e) {
                        intent.putExtra(EXCEPTION, e);
                    }
                }

                setResult(resultCode, intent);
                super.onPostExecute(result);
                finish();
            }

        }.execute();
    }

    private void onAuthenticated(int resultCode, Intent data) {
        state = STATE_AUTHENTICATED;
        if (resultCode == Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED, data);
            getSharedLinkCreatorApplication().clearClient();
            finish();
        }
        else {
            BoxAndroidClient client = data.getParcelableExtra(OAuthActivity.BOX_CLIENT);
            getSharedLinkCreatorApplication().setClient(client);
            startFilePicker();
        }
    }

    private BoxAndroidClient createClient(String clientId, String clientSecret, String authString) {
        BoxAndroidOAuthData oauthData = Utils.parseJSONStringIntoObject(authString, BoxAndroidOAuthData.class);
        BoxAndroidClient client = new BoxAndroidClient(clientId, clientSecret);
        client.authenticate(oauthData);
        return client;
    }

    private void startOAuth(String clientId, String clientSecret) {
        Intent intent = OAuthActivity.createOAuthActivityIntent(this, clientId, clientSecret);
        startActivityForResult(intent, AUTHENTICATE_REQUEST);
    }

    private void startFilePicker() {
        Intent intent = FilePickerActivity.getLaunchIntent(this, "0", getSharedLinkCreatorApplication().getClient());
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    private BoxSharedLinkCreatorApplication getSharedLinkCreatorApplication() {
        return (BoxSharedLinkCreatorApplication) getApplication();
    }
}
