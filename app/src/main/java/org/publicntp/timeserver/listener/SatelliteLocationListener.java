package org.publicntp.timeserver.listener;

import android.location.GnssStatus;
import android.location.Location;
import android.os.Bundle;

import org.publicntp.timeserver.repository.time.TimeStorage;
import org.publicntp.timeserver.repository.location.LocationStorage;


public class SatelliteLocationListener extends GnssStatus.Callback implements android.location.LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        TimeStorage.setMillis(location.getTime());
        LocationStorage.setLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        LocationStorage.setSatelliteList(status);
    }
}
