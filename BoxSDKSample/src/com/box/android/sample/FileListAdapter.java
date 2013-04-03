package com.box.android.sample;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxjavalibv2.dao.BoxItem;
import com.box.boxjavalibv2.dao.BoxTypedObject;

public class FileListAdapter extends ArrayAdapter<BoxTypedObject> {

    public FileListAdapter(Context context, ArrayList<BoxTypedObject> items) {
        super(context, R.layout.file_row, items);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        BoxTypedObject item = getItem(position);
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.file_row, parent, false);
        }

        TextView filenameTextView = (TextView) row.findViewById(R.id.filename);

        // Check the type of item so we can label it as a file or a folder.
        if (item instanceof BoxAndroidFolder) {
            BoxAndroidFolder folder = (BoxAndroidFolder) item;
            filenameTextView.setText("[Folder] " + folder.getName());
        }
        else if (item instanceof BoxAndroidFile) {
            BoxAndroidFile file = (BoxAndroidFile) item;
            filenameTextView.setText("[File] " + file.getName());
        }
        else {
            BoxItem boxItem = (BoxItem) item;
            filenameTextView.setText(boxItem.getName());
        }

        return row;
    }
}