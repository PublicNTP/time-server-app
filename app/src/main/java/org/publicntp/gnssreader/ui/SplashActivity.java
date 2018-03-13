package org.publicntp.gnssreader.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by zac on 3/12/18.
 */

public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}
