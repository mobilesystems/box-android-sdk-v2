package com.example.helloworld;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils.Null;

import com.box.boxandroidlibv2.BoxAndroidClient;
import com.box.boxandroidlibv2.activities.FilePickerActivity;
import com.box.boxandroidlibv2.activities.FolderPickerActivity;
import com.box.boxandroidlibv2.activities.OAuthActivity;
import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.boxjavalibv2.requests.requestobjects.BoxFileUploadRequestObject;
import com.box.restclientv2.exceptions.BoxRestException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final static int AUTH_REQUEST = 1;
	private final static int UPLOAD_REQUEST = 2;
	private final static int DOWNLOAD_REQUEST = 3;
	
	private Button btnUpload;
	private Button btnAuth;
	private Button btnDownload;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUI();
    }


    private void initUI() {
    	initAuthButton();
    	initUploadButton();
    	initDownloadButton();
	}
    
    private void initAuthButton() {
		btnAuth = (Button) findViewById(R.id.authenticate);
		btnAuth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startAuthentication();
			}
			
		});
	}
	
    private void startAuthentication() {
		Intent intent = OAuthActivity.createOAuthActivityIntent(this, HelloWorldApplication.CLIENT_ID,HelloWorldApplication.CLIENT_SECRET);
		this.startActivityForResult(intent, AUTH_REQUEST);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == AUTH_REQUEST) {
			onAuthenticated(resultCode, data);
		} else if (requestCode == UPLOAD_REQUEST) {
			onFolderSelected(resultCode, data);
		} else if (requestCode == DOWNLOAD_REQUEST) {
			onFileSelected(resultCode, data);
		}
	}

    private void onAuthenticated(int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
		} else {
			BoxAndroidClient client = data.getParcelableExtra(OAuthActivity.BOX_CLIENT);
			if (client == null) {
				Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
			} else {
				((HelloWorldApplication) getApplication()).setClient(client);
				Toast.makeText(this, "authenticated", Toast.LENGTH_LONG).show();
			}
		}
	}
    
    
	private void initDownloadButton() {
		btnDownload = (Button) findViewById(R.id.downloadfile);
		btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doDownload();
			}
			
		});
	}


	private void initUploadButton() {
		btnUpload = (Button) findViewById(R.id.uploadfile);
		btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doUpload();
			}
			
		});
	}

	private void onFileSelected(int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
		} else {
			final BoxAndroidFile file = data.getParcelableExtra(FilePickerActivity.EXTRA_BOX_ANDROID_FILE);
			AsyncTask<Null, Integer, Null> task = new AsyncTask<Null, Integer, Null> () {

				@Override
				protected void onPostExecute(Null result) {
					Toast.makeText(MainActivity.this, "done downloading", Toast.LENGTH_LONG).show();
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					Toast.makeText(MainActivity.this, "start downloading", Toast.LENGTH_LONG).show();
					super.onPreExecute();
				}

				@Override
				protected Null doInBackground(Null... params) {
					BoxAndroidClient client = ((HelloWorldApplication) getApplication()).getClient();
					try {
						File f = new File(Environment.getExternalStorageDirectory(), file.getName());
						System.out.println(f.getAbsolutePath());
						client.getFilesManager().downloadFile(file.getId(), f, null, null);
					} catch (Exception e) {
					}
					return null;
				}
			};
			task.execute();

			
			
		}
	}


	private void onFolderSelected(int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
		} else {
			final BoxAndroidFolder folder = data
					.getParcelableExtra(FolderPickerActivity.EXTRA_BOX_ANDROID_FOLDER);
			AsyncTask<Null, Integer, Null> task = new AsyncTask<Null, Integer, Null> () {

				@Override
				protected void onPostExecute(Null result) {
					Toast.makeText(MainActivity.this, "done uploading", Toast.LENGTH_LONG).show();
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					Toast.makeText(MainActivity.this, "start uploading", Toast.LENGTH_LONG).show();
					super.onPreExecute();
				}

				@Override
				protected Null doInBackground(Null... params) {
					BoxAndroidClient client = ((HelloWorldApplication) getApplication()).getClient();
					try {
						File mockFile = createMockFile();
						client.getFilesManager().uploadFile(
								BoxFileUploadRequestObject.uploadFileRequestObject(
										folder.getId(), mockFile.getName(), mockFile));
					} catch (Exception e) {
					}
					return null;
				}
			};
			task.execute();
		}
	}

	
	
	private void doUpload() {
		BoxAndroidClient client = ((HelloWorldApplication) getApplication()).getClient();
		Intent intent = FolderPickerActivity.getLaunchIntent(this, "0", client);
		startActivityForResult(intent, UPLOAD_REQUEST);
	}
	

	private void doDownload() {
		BoxAndroidClient client = ((HelloWorldApplication) getApplication()).getClient();
		Intent intent = FilePickerActivity.getLaunchIntent(this, "0", client);
		startActivityForResult(intent, DOWNLOAD_REQUEST);
	}


	private File createMockFile() {
		try {
			File file = File.createTempFile("tmp", ".txt");
			FileUtils.writeStringToFile(file, "string");
	    	return file;
		} catch (Exception e) {
			return null;
		}
    }
}

