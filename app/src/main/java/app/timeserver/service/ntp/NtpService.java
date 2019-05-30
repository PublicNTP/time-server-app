package app.timeserver.service.ntp;

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
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.util.Log;
import android.provider.Settings;

import app.timeserver.R;
import app.timeserver.helper.NetworkInterfaceHelper;
import app.timeserver.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by zac on 2/22/18.
 */

public class NtpService extends Service {
    protected PowerManager.WakeLock mWakeLock;

    private static final int SERVICE_ID = 1;
    private static final String CHANNEL_ID = "NTP_SERVICE";

    public static final int NTP_DEFAULT_PORT = 123;
    public static final int NTP_UNRESTRICTED_PORT = 1234;

    private Intent serverIntent;
    private Intent restartIntent;
    public static final String SERVICE_ACTION = "START_NTP_SERVICE";
    public static final String RESTART_ACTION = "RESTART_NTP_SERVICE";

    public static String chosenInterface = "";
    public static String ipAddress = "";
    private static NtpService ntpService;

    private NetworkChangeReceiver networkChangeReceiver;

    public static String port = "";
    public static String stratum = "1";
    public static ArrayList<String> portList = new ArrayList<String>();

    Thread serverThread;
    SimpleNTPServer simpleNTPServer;

    boolean started = false;
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
      serverIntent = new Intent(SERVICE_ACTION);
      restartIntent = new Intent(RESTART_ACTION);

      if(!started){
        BeginNTPService();
      }
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

        final String ethernetInterfaceName = "eth0";
        final String wifiInterfaceName = "wlan0";
        final String usbInterfaceName = "usb0";
        if(chosenInterface == ""){
          if (networkInterfaceHelper.hasConnectivityOn(ethernetInterfaceName)) {
              ipAddress = networkInterfaceHelper.ipFor(ethernetInterfaceName);
              chosenInterface = ethernetInterfaceName;
          } else if (networkInterfaceHelper.hasConnectivityOn(wifiInterfaceName)) {
              ipAddress = networkInterfaceHelper.ipFor(wifiInterfaceName);
              chosenInterface = wifiInterfaceName;
          } else {
              ipAddress = "0.0.0.0";
          }
        }else{
          ipAddress = networkInterfaceHelper.ipFor(chosenInterface);
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

        if(!started){
          BeginNTPService();
        }

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

    private void BeginNTPService(){
      ntpService = this;
      started = true;
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
      NetworkInterfaceHelper networkInterfaceHelper = new NetworkInterfaceHelper();
      final String ethernetInterfaceName = "eth0";
      final String wifiInterfaceName = "wlan0";
      final String usbInterfaceName = "usb0";
      portList = new ArrayList();
      if (networkInterfaceHelper.hasConnectivityOn(ethernetInterfaceName)) {
          portList.add(ethernetInterfaceName);
      }
      if (networkInterfaceHelper.hasConnectivityOn(wifiInterfaceName)) {
          portList.add(wifiInterfaceName);
      }
      if (networkInterfaceHelper.hasConnectivityOn(usbInterfaceName)) {
          portList.add(usbInterfaceName);
      }
      port = Integer.toString(rootRedirected ? NTP_DEFAULT_PORT : NTP_UNRESTRICTED_PORT);


      final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
      this.mWakeLock.acquire();
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 Intent optimizationIntent = new Intent();
                 String packageName = getPackageName();
                 if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                     optimizationIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                     optimizationIntent.setData(Uri.parse("package:" + packageName));
                     startActivity(optimizationIntent);
                 }
             }
      startForeground(SERVICE_ID, buildNotification());
      addConnectivityBroadcastReceiver();
      Log.i("NTP", "NTP Service Started, Show interface");
      sendBroadcast(serverIntent);
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();

        if (simpleNTPServer != null) {
            simpleNTPServer.stop();
            simpleNTPServer = null;
        }

        ntpService = null;
        started = false;
        if (serverThread != null) {
            if (serverThread.isAlive()) serverThread.interrupt();
            serverThread = null;
        }

        removeConnectivityBroadcastReceiver();
        //Log.i("NTP", "NTP Service destroyed, Restart");
        //sendBroadcast(restartIntent);
    }

    public void changeNetwork(String selected){
      chosenInterface = selected;
      startForeground(SERVICE_ID, buildNotification());
    }

    public void setStratumNumber(String selected) {
      stratum = selected;
      simpleNTPServer.setStratumNumber(selected);
    }

    public String getStratumNumber() {
      String stratum;
      if(simpleNTPServer != null){
        stratum = simpleNTPServer.getStratumNumber();
      }else{
        stratum = "1";
      }
      return stratum;
    }

    public static boolean exists() {
        return ntpService != null;
    }

    public void rebuildNotification() {
        startForeground(SERVICE_ID, buildNotification());
    }

}
