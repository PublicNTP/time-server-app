package org.publicntp.gnssreader.service.ntp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private final NtpService ntpService;

    public NetworkChangeReceiver(NtpService ntpService) {
        this.ntpService = ntpService;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ntpService.rebuildNotification();
    }
}
