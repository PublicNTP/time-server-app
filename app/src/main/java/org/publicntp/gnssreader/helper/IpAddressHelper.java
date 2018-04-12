package org.publicntp.gnssreader.helper;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddressHelper {
    public static String ipAddress(Context context) {
        try {
            //return InetAddress.getLocalHost().getHostAddress();
            int ipaddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress();
            return Formatter.formatIpAddress(ipaddress);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown Address";
        }
    }
}
