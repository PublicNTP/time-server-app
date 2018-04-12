package org.publicntp.gnssreader.service.ntp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.IpAddressHelper;
import org.publicntp.gnssreader.helper.RootChecker;
import org.publicntp.gnssreader.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.chainfire.libsuperuser.Shell;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

/**
 * Created by zac on 2/22/18.
 */

public class NtpService extends Service {
    private static final int SERVICE_ID = 1;
    private static final String CHANNEL_ID = "NTP_SERVICE";

    public static final int NTP_DEFAULT_PORT = 123;
    public static final int NTP_USABLE_PORT = 1234;

    private static NtpService ntpService;

    Thread serverThread;
    SimpleNTPServer simpleNTPServer;

    boolean rootRedirected = false;

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

    @SuppressLint("DefaultLocale")
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
                .setContentText(String.format("Running on %s:%d", IpAddressHelper.ipAddress(this), rootRedirected ? NTP_DEFAULT_PORT : NTP_USABLE_PORT))
                .setPriority(PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.icon_publicntp_logo, getString(R.string.kill_ntp_service), pendingKillServiceIntent)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ntpService = this;

        serverThread = new Thread(() -> {
            simpleNTPServer = new SimpleNTPServer(NTP_USABLE_PORT);
            try {
                simpleNTPServer.start();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                NtpService.this.stopSelf();
            }
        });
        serverThread.run();

        if (Shell.SU.available()) {
            boolean allowed = PortForwardingHelper.allowAllForwarding();
            boolean forwarded = PortForwardingHelper.forwardUDPPort(NTP_DEFAULT_PORT, NTP_USABLE_PORT);
            if (!allowed || !forwarded) {
                Toast.makeText(NtpService.this, "Firewall routing failed. Server may not work.", Toast.LENGTH_SHORT).show();
            } else {
                rootRedirected = true;
            }
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
        if (serverThread != null) {
            if (serverThread.isAlive()) serverThread.interrupt();
            serverThread = null;
        }
    }
}
