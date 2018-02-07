package org.publicntp.gnssreader.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;

/**
 * Created by zac on 2/7/18.
 */

public class LocationHelper {
    @SuppressLint("MissingPermission")
    public static void registerNmeaListenerAndStartGettingFixes(Context context) {
        LocationManager locationManager = context.getSystemService(LocationManager.class);
        locationManager.addNmeaListener(new NmeaMsgListener(context));
    }
}
