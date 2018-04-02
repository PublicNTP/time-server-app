package org.publicntp.gnssreader.service.ntp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent killServiceIntent = KillServiceReceiver.getAddressedIntent();
        PendingIntent pendingKillServiceIntent = PendingIntent.getBroadcast(this, 0, killServiceIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_large_w_transparency)
                .setContentTitle("NTP Server Running")
                .setContentText("Running on port " + simpleNTPServer.getPort())
                .setPriority(PRIORITY_MIN)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.icon_publicntp_logo, getString(R.string.kill_ntp_service), pendingKillServiceIntent)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ntpService = this;

        simpleNTPServer = new SimpleNTPServer(1234);
        try {
            simpleNTPServer.start();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            this.stopSelf();
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

    public ServerLogDataPoint getDataPoint(int seed) {
        return getDataPoint(System.currentTimeMillis(), seed);
    }

    public ServerLogDataPoint getDataPoint(long i, int seed) {
        Random random = new Random();
        seed += random.nextInt() % 10;
        if(seed < 0) seed = 0;
        return new ServerLogDataPoint(i, seed);
    }

    public List<ServerLogDataPoint> createServerLogData() {
        serverLogData = new ArrayList<>();
        int current = 100;
        long end = System.currentTimeMillis();
        for (long i = end - 60000; i < end; i += 1000) {
            ServerLogDataPoint point = getDataPoint(i, current);
            current = point.numberReceived;
            serverLogData.add(point);
        }
        return serverLogData;
    }

    List<ServerLogDataPoint> serverLogData;
    public List<ServerLogDataPoint> getServerLogData() {
        if(serverLogData == null) {
            serverLogData = createServerLogData();
        }
        return serverLogData;
    }

    public void incrementMockData() {
        if(serverLogData != null && serverLogData.size() != 0) {
            serverLogData.remove(0);
            serverLogData.add(getDataPoint(mostRecentData().numberReceived));
        }
    }

    public ServerLogDataPoint mostRecentData() {
        List<ServerLogDataPoint> serverLogDataPoints = getServerLogData();
        if(serverLogDataPoints == null || serverLogDataPoints.size() == 0) {
            return null;
        }
        return serverLogDataPoints.get(serverLogDataPoints.size()-1);
    }
}
