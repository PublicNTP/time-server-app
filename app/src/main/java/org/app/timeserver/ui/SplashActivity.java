package org.app.timeserver.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.app.timeserver.R;
import org.app.timeserver.helper.permissions.PermissionsHelper;
import org.app.timeserver.helper.TimeMillis;
import org.app.timeserver.listener.LocationHelper;
import org.app.timeserver.helper.permissions.Permission;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by zac on 3/12/18.
 */

public class SplashActivity extends Activity {
    final int SPLASH_DELAY = (int) (TimeMillis.SECOND * 3);

    @BindView(R.id.splash_logo) GifImageView spinningLogo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        GifDrawable spinningDrawable = (GifDrawable) spinningLogo.getDrawable();
        spinningDrawable.setSpeed(2f);
        spinningDrawable.start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DELAY);


        if (PermissionsHelper.permissionIsGranted(this, Permission.FINE_LOCATION)) {
            LocationHelper.registerNmeaListenerAndStartGettingFixes(this);
        }
    }
}
