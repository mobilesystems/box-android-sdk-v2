package com.box.samplesharedlinkcreatorcaller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.btngetsharedlink);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                launchCreateSharedLink();
            }

        });
    }

    protected void launchCreateSharedLink() {
        Intent intent = new Intent();
        intent.setClassName("com.box.boxsharedlinkcreator", "com.box.boxsharedlinkcreator.CreateSharedLinkActivity");
        // TODO: use your own client id.
        intent.putExtra("clientId", "");
        // TODO: use your own client secret.
        intent.putExtra("clientSecret", "");

        String auth = getSharedPref().getString("auth", "");
        if (auth != "") {
            intent.putExtra("auth", auth);
        }
        startActivityForResult(intent, 12345);
    }

    private SharedPreferences getSharedPref() {
        return getSharedPreferences("sharedlinkcreator", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            getSharedPref().edit().remove("auth").commit();
        }
        else {
            String str = data.getStringExtra("sharedLink");
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
            String auth = data.getStringExtra("auth");
            getSharedPref().edit().putString("auth", auth).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
