package com.box.android.sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.ObjectUtils.Null;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.box.boxandroidlibv2.BoxAndroidClient;
import com.box.boxandroidlibv2.activities.FilePickerActivity;
import com.box.boxandroidlibv2.activities.FolderPickerActivity;
import com.box.boxandroidlibv2.activities.OAuthActivity;
import com.box.boxandroidlibv2.dao.BoxAndroidCollection;
import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxjavalibv2.dao.BoxCollection;
import com.box.boxjavalibv2.dao.BoxFolder;
import com.box.boxjavalibv2.dao.BoxTypedObject;
import com.box.boxjavalibv2.requests.requestobjects.BoxFileUploadRequestObject;
import com.box.restclientv2.exceptions.BoxSDKException;

public class FileListActivity extends ListActivity {

	private final static String TAG = "com.box.android.sample.FileListActivity";

	private FileListAdapter adapter;
	private String currentFolderId = "0";

	private final static int AUTHENTICATE_REQUEST = 0;
	private final static int PICK_FOLDER_REQUEST = 1;
	private final static int PICK_FILE_REQUEST = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_list_activity);
		this.findViewById(R.id.btnUploadFile).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						uploadFile(v, currentFolderId);
					}
				});

		this.findViewById(R.id.btnDownloadFileViaPicker).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivityForResult(FilePickerActivity
								.getLaunchIntent(v.getContext(),
										currentFolderId, getClient()),
								PICK_FILE_REQUEST);
					}
				});

		this.findViewById(R.id.btnChangeFolderLocationViaPicker)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivityForResult(FolderPickerActivity
								.getLaunchIntent(v.getContext(),
										currentFolderId, getClient()),
								PICK_FOLDER_REQUEST);
					}
				});

		startAuth();
	}

	/**
	 * Get the result from OAuth activity, this either returns an error message
	 * or a parceled BoxAndroidClient.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case AUTHENTICATE_REQUEST:
			if (resultCode == Activity.RESULT_CANCELED) {
				String failMessage = data
						.getStringExtra(OAuthActivity.ERROR_MESSAGE);
				Toast.makeText(this, "Auth fail:" + failMessage,
						Toast.LENGTH_LONG).show();
				finish();
			} else {
				BoxAndroidClient client = data
						.getParcelableExtra(OAuthActivity.BOX_CLIENT);
				BoxSDKSampleApplication app = (BoxSDKSampleApplication) getApplication();
				app.setClient(client);
			}

			onClientAuthenticated();
			break;
		case PICK_FOLDER_REQUEST:
			if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "No folder chosen", Toast.LENGTH_LONG)
						.show();
			} else {
				BoxAndroidFolder folder = data
						.getParcelableExtra(FolderPickerActivity.EXTRA_BOX_ANDROID_FOLDER);
				navigateToFolder(folder.getId());

			}
			break;
		case PICK_FILE_REQUEST:
			if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "No file chosen", Toast.LENGTH_LONG)
						.show();
			} else {
				BoxAndroidFile file = data
						.getParcelableExtra(FilePickerActivity.EXTRA_BOX_ANDROID_FILE);
				downloadFile(file);
			}
			break;

		}

	}

	@Override
	/**
	 * Override the back button so that we go to the parent folder.
	 */
	public void onBackPressed() {
		if (currentFolderId == "0") {
			super.onBackPressed();
		}
		Thread t = new Thread(){
			public void run(){
				String parentFolderId;
				try {
					BoxFolder parentFolder = getClient().getFoldersManager()
							.getFolder(currentFolderId, null).getParent();
					if (parentFolder == null) {
						parentFolderId = "0";
					} else {
						parentFolderId = parentFolder.getId();
					}
				} catch (BoxSDKException e) {
					Log.e(TAG, "An error occurred when getting a parent folder ID.", e);
					return;
				}

				navigateToFolder(parentFolderId);
			}
		};
		t.start();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		BoxTypedObject object = (BoxTypedObject) this.getListAdapter().getItem(
				position);
		if (object instanceof BoxAndroidFile) {
			downloadFile((BoxAndroidFile) object);
		} else if (object instanceof BoxAndroidFolder) {
			navigateToFolder(((BoxAndroidFolder) object).getId());
		}
	}

	public void navigateToFolder(final String folderId) {
		new AsyncTask<Null, Void, BoxAndroidCollection>() {

			@Override
			protected BoxAndroidCollection doInBackground(Null... params) {
				try {
					return (BoxAndroidCollection) getClient()
							.getFoldersManager().getFolderItems(folderId, null);
				} catch (BoxSDKException e) {
					Log.e(TAG,
							"An error occurred when getting the folder's children.",
							e);
					return new BoxAndroidCollection();
				}
			}

			@Override
			protected void onPostExecute(BoxAndroidCollection result) {
				currentFolderId = folderId;
				ArrayList<BoxTypedObject> boxObjects = result.getEntries();
				adapter.clear();
				if (boxObjects != null) {
					adapter.addAll(boxObjects);
				}
			}
		}.execute();
	}

	public void downloadFile(final BoxAndroidFile file) {
		Toast.makeText(FileListActivity.this,
				"Starting download..." + file.getName(), Toast.LENGTH_LONG)
				.show();
		new AsyncTask<BoxAndroidFile, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(BoxAndroidFile... arg0) {
				try {
					File f = new File(getFilesDir(), file.getName());
					getClient().getFilesManager().downloadFile(file.getId(), f,
							null, null);
				} catch (Exception e) {
					Log.e(TAG, "An error occurred when downloading a file.", e);
					return false;
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					Toast.makeText(FileListActivity.this,
							"Successfully downloaded " + file.getName() + ".",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(FileListActivity.this,
							"An error occurred when downloading.",
							Toast.LENGTH_LONG).show();
				}
			}

		}.execute(file);
	}

	public void uploadFile(View view, final String folderId) {
		Toast.makeText(FileListActivity.this,
				"Uploading \"Sample File.txt\"...", Toast.LENGTH_LONG).show();
		new AsyncTask<Null, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Null... params) {
				final File sampleFile;
				try {
					sampleFile = File.createTempFile("Sample File", ".txt");
					FileWriter fw = new FileWriter(sampleFile);
					fw.write("This is a sample file created with the Box SDK.");
					fw.close();
				} catch (IOException e) {
					Log.e(TAG,
							"An error occurred when creating a sample upload file.",
							e);
					return false;
				}

				try {
					BoxFileUploadRequestObject upload = BoxFileUploadRequestObject
							.uploadFileRequestObject("0", "Sample File.txt",
									sampleFile);
					getClient().getFilesManager().uploadFiles(upload);
					Log.v(TAG, "Sample file successfully uploaded.");
				} catch (BoxSDKException e) {
					Log.e(TAG,
							"An error occurred when uploading a sample file.",
							e);
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					Toast.makeText(FileListActivity.this,
							"Successfully uploaded.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(FileListActivity.this, "Failed to upload.",
							Toast.LENGTH_LONG).show();
				}
			}

		}.execute();
	}

	/**
	 * Start OAuth flow.
	 */
	private void startAuth() {
		Intent intent = OAuthActivity.createOAuthActivityIntent(this,
				BoxSDKSampleApplication.CLIENT_ID,
				BoxSDKSampleApplication.CLIENT_SECRET);
		startActivityForResult(intent, AUTHENTICATE_REQUEST);
	}

	/**
	 * Once BoxAndroidClient is authenticated, we can start making api calls.
	 */
	private void onClientAuthenticated() {

		AsyncTask<Null, Void, BoxCollection> task = new AsyncTask<Null, Void, BoxCollection>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Toast.makeText(FileListActivity.this, "Making api call.",
						Toast.LENGTH_LONG).show();
			}

			@Override
			protected BoxCollection doInBackground(Null... params) {
				try {
					return getClient().getFoldersManager().getFolderItems(
							currentFolderId, null);
				} catch (BoxSDKException e) {
					Toast.makeText(FileListActivity.this,
							"Fail making api call.", Toast.LENGTH_LONG).show();
					return new BoxAndroidCollection();
				}
			}

			@Override
			protected void onPostExecute(BoxCollection items) {
				super.onPostExecute(items);
				ArrayList<BoxTypedObject> boxObjects = items.getEntries();
				adapter = new FileListAdapter(FileListActivity.this, boxObjects);
				setListAdapter(adapter);
			}

		};
		task.execute();
	}

	/**
	 * Convenient method to get BoxAndroidClient.
	 * 
	 * @return BoxAndroidClient
	 */
	private BoxAndroidClient getClient() {
		return ((BoxSDKSampleApplication) getApplication()).getClient();
	}
}