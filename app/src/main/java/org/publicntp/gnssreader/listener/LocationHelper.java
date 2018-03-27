package org.publicntp.gnssreader.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by zac on 2/7/18.
 */

public class LocationHelper {
    @SuppressLint("MissingPermission")
    public static void registerNmeaListenerAndStartGettingFixes(Context context) {
        LocationManager locationManager = context.getSystemService(LocationManager.class);
        //locationManager.addNmeaListener(new NmeaMsgListener(context));

        SatelliteLocationListener locationListener = new SatelliteLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListener);
        locationManager.registerGnssStatusCallback(locationListener);
    }
}
