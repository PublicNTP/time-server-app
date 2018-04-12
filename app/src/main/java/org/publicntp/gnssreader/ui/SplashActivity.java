package org.publicntp.gnssreader.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.PermissionsHelper;
import org.publicntp.gnssreader.helper.TimeMillis;
import org.publicntp.gnssreader.listener.LocationHelper;
import org.publicntp.gnssreader.model.Permission;

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
