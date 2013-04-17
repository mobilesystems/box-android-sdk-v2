package com.box.boxandroidlibv2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.box.boxandroidlibv2.BoxAndroidClient;
import com.box.boxandroidlibv2.R;
import com.box.boxandroidlibv2.adapters.BoxListItemAdapter;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxandroidlibv2.manager.ThumbnailManager;
import com.box.boxandroidlibv2.viewdata.BoxListItem;

/**
 * This class is used to choose a particular folder from the user's account and when chosen will return a result with a BoxAndroidFolder in the extra
 * EXTRA_BOX_ANDROID_FOLDER.
 * 
 * @author dcung
 * 
 */
public class FolderPickerActivity extends FilePickerActivity {

    public static final String EXTRA_BOX_ANDROID_FILE = "extraBoxAndroidFile";

    public static final String EXTRA_BOX_ANDROID_FOLDER = "extraBoxAndroidFile";

    private final int FOLDER_PICKER_REQUEST_CODE = 8;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOLDER_PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // if someone further down the chain has chosen a folder then send result back.
                setResult(resultCode, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initializeViews() {
        setContentView(R.layout.boxandroidlibv2_layout_folder_picker);
        TextView customTitle = (TextView) this.findViewById(R.id.customTitle);
        customTitle.setText(getTitle());
        // to make dialog theme fill the full view.
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        this.findViewById(R.id.btnChoose).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_BOX_ANDROID_FOLDER, mCurrentFolder);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected BoxListItemAdapter initializeBoxListItemAdapter(ThumbnailManager thumbNailManager) {
        return new FolderPickerBoxListItemAdapter(this, thumbNailManager);
    }

    @Override
    protected ListView getListView() {
        return (ListView) this.findViewById(R.id.PickerListView);
    }

    /**
     * Create an intent to launch an instance of this activity to navigate folders.
     * 
     * @param context
     *            current context.
     * @param folderId
     *            folder id to navigate.
     * @param client
     *            the client to use for api calls.
     * @return an intent to launch an instance of this activity.
     */
    public static Intent getLaunchIntent(Context context, final String folderId, final BoxAndroidClient client) {
        Intent intent = FolderNavigationActivity.getLaunchIntent(context, folderId, client);
        intent.setClass(context, FolderPickerActivity.class);
        return intent;
    }
    
    /**
     * Create an intent to launch an instance of this activity to navigate folders. This version will immediately show the given name in the navigation spinner
     * to before information about it has been fetched from the server.
     * 
     * @param context
     *            current context.
     * @param folderName
     *            Name to show in the navigation spinner. Should be name of the folder.
     * @param folderId
     *            folder id to navigate.
     * @param client
     *            to use for api calls.
     * @return an intent to launch an instance of this activity.
     */
    public static Intent getLaunchIntent(Context context, final String folderName, final String folderId, final BoxAndroidClient client) {
        Intent intent = getLaunchIntent(context, folderId, client);
        intent.putExtra(EXTRA_FOLDER_NAME, folderName);
        return intent;
    }

    /**
     * 
     * Adapter that disables clicking on files.
     */
    protected class FolderPickerBoxListItemAdapter extends FolderNavigationActivity.FolderNavigationBoxListItemAdapter {

        public FolderPickerBoxListItemAdapter(Activity context, ThumbnailManager manager) {
            super(context, manager);
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position).getBoxItem() != null && getItem(position).getBoxItem() instanceof BoxAndroidFolder;
        }

        @Override
        public synchronized void add(BoxListItem listItem) {
            if (listItem.getType() == BoxListItem.TYPE_BOX_FILE_ITEM) {
                // do not add files.
                return;
            }
            super.add(listItem);
        }

    }
}
