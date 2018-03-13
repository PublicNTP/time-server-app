package org.publicntp.gnssreader.service.ntp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zac on 3/1/18.
 */

public class KillServiceReceiver extends BroadcastReceiver {
    private static final String KILL_NTP_SERVICE_KEY = "KILL_NTP_SERVICE";

    public static Intent getAddressedIntent() {
        Intent intent = new Intent();
        intent.setAction(KILL_NTP_SERVICE_KEY);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NtpService.getNtpService().stopSelf();
    }
}
