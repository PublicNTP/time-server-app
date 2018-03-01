package org.publicntp.gnssreader.service.ntp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.publicntp.gnssreader.R;

import java.io.IOException;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

/**
 * Created by zac on 2/22/18.
 */

public class NtpService extends Service {
    private static final int SERVICE_ID = 1;
    private static final String CHANNEL_ID = "NTP_SERVICE";

    private static NtpService ntpService;
    SimpleNTPServer simpleNTPServer;

    public static NtpService getNtpService() {
        return ntpService;
    }

    public static Intent ignitionIntent(Context context) {
        return new Intent(context, NtpService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(chan1);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_publicntp_logo)
                .setContentTitle("NTP Server Running")
                .setContentText("Running on port " + simpleNTPServer.getPort())
                .setPriority(PRIORITY_MIN)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ntpService = this;

        simpleNTPServer = new SimpleNTPServer(8765);
        try {
            simpleNTPServer.start();
            Toast.makeText(this, "Started server on port " + simpleNTPServer.getPort(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        startForeground(SERVICE_ID, buildNotification());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (simpleNTPServer != null) {
            simpleNTPServer.stop();
            simpleNTPServer = null;
        }
        ntpService = null;
    }
}
