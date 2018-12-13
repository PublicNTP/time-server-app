package org.publicntp.timeserver.service.ntp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.publicntp.timeserver.R;
import org.publicntp.timeserver.helper.NetworkInterfaceHelper;
import org.publicntp.timeserver.ui.MainActivity;

import java.io.IOException;

import eu.chainfire.libsuperuser.Shell;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by zac on 2/22/18.
 */

public class NtpService extends Service {
    private static final int SERVICE_ID = 1;
    private static final String CHANNEL_ID = "NTP_SERVICE";

    public static final int NTP_DEFAULT_PORT = 123;
    public static final int NTP_UNRESTRICTED_PORT = 1234;

    private static NtpService ntpService;

    private NetworkChangeReceiver networkChangeReceiver;

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

        NetworkInterfaceHelper networkInterfaceHelper = new NetworkInterfaceHelper();
        String ipAddress;
        String chosenInterface = "";
        final String ethernetInterfaceName = "eth0";
        final String wifiInterfaceName = "wlan0";
        if (networkInterfaceHelper.hasConnectivityOn(ethernetInterfaceName)) {
            ipAddress = networkInterfaceHelper.ipFor(ethernetInterfaceName);
            chosenInterface = ethernetInterfaceName;
        } else if (networkInterfaceHelper.hasConnectivityOn(wifiInterfaceName)) {
            ipAddress = networkInterfaceHelper.ipFor(wifiInterfaceName);
            chosenInterface = wifiInterfaceName;
        } else {
            ipAddress = "0.0.0.0";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_large_w_transparency)
                .setContentTitle("NTP Server Running")
                .setPriority(PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.icon_publicntp_logo, getString(R.string.kill_ntp_service), pendingKillServiceIntent);

        if (chosenInterface.equals("")) {
            builder = builder.setContentText(String.format("Running on %s:%d", ipAddress, rootRedirected ? NTP_DEFAULT_PORT : NTP_UNRESTRICTED_PORT));
        } else {
            builder = builder.setContentText(String.format("Running on %s, %s:%d", chosenInterface, ipAddress, rootRedirected ? NTP_DEFAULT_PORT : NTP_UNRESTRICTED_PORT));
        }

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ntpService = this;

        serverThread = new Thread(() -> {
            simpleNTPServer = new SimpleNTPServer(NTP_UNRESTRICTED_PORT);
            try {
                simpleNTPServer.start();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                NtpService.this.stopSelf();
            }
        });
        serverThread.run();

        tryForwardPorts();

        startForeground(SERVICE_ID, buildNotification());
        addConnectivityBroadcastReceiver();

        return START_STICKY;
    }

    private void tryForwardPorts() {
        if (Shell.SU.available()) {
            boolean allowed = PortForwardingHelper.allowAllForwarding();
            boolean forwarded = PortForwardingHelper.forwardUDPPort(NTP_DEFAULT_PORT, NTP_UNRESTRICTED_PORT);
            if (!allowed || !forwarded) {
                Toast.makeText(NtpService.this, "Firewall routing failed. Server may not work.", Toast.LENGTH_SHORT).show();
            } else {
                rootRedirected = true;
            }
        }
    }

    private void addConnectivityBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        registerReceiver(networkChangeReceiver, filter);
    }

    private void removeConnectivityBroadcastReceiver() {
        unregisterReceiver(networkChangeReceiver);
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

        removeConnectivityBroadcastReceiver();
    }

    public static boolean exists() {
        return ntpService != null;
    }

    public void rebuildNotification() {
        startForeground(SERVICE_ID, buildNotification());
    }
}
